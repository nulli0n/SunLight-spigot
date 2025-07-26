package su.nightexpress.sunlight.module.chat.handler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.wrapper.UniSound;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.core.user.IgnoredUser;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

public class PrivateMessageHandler {

    private final SunLightPlugin plugin;
    private final ChatModule     module;

    private final CommandSender sender;
    private final CommandSender receiver;

    @Nullable private final Player playerSender;
    @Nullable private final Player playerReceiver;

    private String message;

    public PrivateMessageHandler(@NotNull SunLightPlugin plugin,
                                 @NotNull ChatModule module,
                                 @NotNull CommandSender sender,
                                 @NotNull CommandSender receiver,
                                 @NotNull String message) {
        this.plugin = plugin;
        this.module = module;

        this.sender = sender;
        this.receiver = receiver;
        this.playerSender = sender instanceof Player player ? player : null;
        this.playerReceiver = receiver instanceof Player player ? player : null;

        this.setMessage(message);
    }

    public boolean handle() {
        if (this.message.isBlank()) return false;

        if (!this.checkDisabledPM()) return false;
        if (!this.checkIgnorance()) return false;

        if (ChatUtils.containsItemLink(this.message) && this.playerSender != null) {
            ItemStack item = this.playerSender.getInventory().getItemInMainHand();
            this.message = ChatUtils.appendItemComponent(this.message, item);
        }

        // Call custom plugin event.
        PlayerPrivateMessageEvent event = new PlayerPrivateMessageEvent(this.sender, this.receiver, this.message);
        plugin.getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        this.postMessageEffect(this.playerSender, this.playerReceiver, ChatConfig.PM_SOUND_OUTGOING.get());
        this.postMessageEffect(this.playerReceiver, this.playerSender, ChatConfig.PM_SOUND_INCOMING.get());

        Players.sendModernMessage(this.sender, Placeholders.forSender(this.receiver).apply(ChatConfig.PM_FORMAT_OUTGOING.get())
            .replace(Placeholders.GENERIC_MESSAGE, this.message));

        Players.sendModernMessage(this.receiver, Placeholders.forSender(this.sender).apply(ChatConfig.PM_FORMAT_INCOMING.get())
            .replace(Placeholders.GENERIC_MESSAGE, this.message));

        return true;
    }

    private void postMessageEffect(@Nullable Player first, @Nullable Player second, @NotNull UniSound sound) {
        if (first == null) return;

        sound.play(first);

        if (second != null) {
            this.module.getChatData(first).setLastConversationWith(second.getUniqueId());
        }
    }

    private boolean checkDisabledPM() {
        if (this.playerReceiver == null) return true;

        SunUser user = plugin.getUserManager().getOrFetch(this.playerReceiver);
        if (!user.getSettings().get(SettingRegistry.ACCEPT_PM) && !this.sender.hasPermission(ChatPerms.BYPASS_PM_DISABLED)) {
            ChatLang.PRIVATE_MESSAGE_ERROR_DISABLED.getMessage()
                .replace(Placeholders.forPlayer(this.playerReceiver))
                .send(this.sender);
            return false;
        }

        return true;
    }

    private boolean checkIgnorance() {
        if (this.playerReceiver == null) return true;
        if (this.playerSender == null) return true;

        SunUser user = plugin.getUserManager().getOrFetch(this.playerReceiver);
        IgnoredUser ignoredUser = user.getIgnoredUser(this.playerSender);
        if (ignoredUser != null && ignoredUser.isDenyConversations() && !this.sender.hasPermission(Perms.BYPASS_IGNORE_PM)) {
            ChatLang.PRIVATE_MESSAGE_ERROR_IGNORANCE.getMessage()
                .replace(Placeholders.forPlayer(this.playerReceiver))
                .send(this.sender);
            return false;
        }

        return true;
    }

    public void setMessage(@NotNull String message) {
        this.message = ChatUtils.cleanMessage(message);
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public CommandSender getSender() {
        return sender;
    }

    @NotNull
    public CommandSender getReceiver() {
        return receiver;
    }
}
