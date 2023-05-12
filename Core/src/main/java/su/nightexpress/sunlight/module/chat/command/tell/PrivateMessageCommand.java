package su.nightexpress.sunlight.module.chat.command.tell;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.data.impl.IgnoredUser;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PrivateMessageCommand extends GeneralCommand<SunLight> {

    private static final Map<CommandSender, String> LAST_MESSAGE = new WeakHashMap<>();

    private final Type type;

    public PrivateMessageCommand(@NotNull SunLight plugin, @NotNull String[] aliases, @NotNull Permission permission, @NotNull Type type) {
        super(plugin, aliases, permission);
        this.type = type;
    }

    enum Type {
        INITIAL, REPLY
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    @Override
    public final boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public final List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (this.getType() == Type.INITIAL) {
            if (arg == 1) {
                return CollectionsUtil.playerNames(player);
            }
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public final void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        String receiverName;

        if (this.getType() == Type.REPLY) {
            if (args.length == 0) {
                this.printUsage(sender);
                return;
            }
            receiverName = LAST_MESSAGE.get(sender);
            if (receiverName == null) {
                plugin.getMessage(ChatLang.COMMAND_REPLY_ERROR_EMPTY).send(sender);
                return;
            }
        }
        else {
            // Check if /tell contains player name and text.
            if (args.length < 2) {
                this.printUsage(sender);
                return;
            }
            // Get player name from the command arguments.
            receiverName = args[0];
        }

        // Get receiver by a player name or just Console.
        boolean isToConsole = receiverName.equalsIgnoreCase(Placeholders.GENERIC_CONSOLE);
        CommandSender receiver = isToConsole ? plugin.getServer().getConsoleSender() : plugin.getServer().getPlayer(receiverName);
        if (receiver == null) {
            this.errorPlayer(sender);
            return;
        }

        String nameFrom = sender.getName();
        String nameTo = receiver.getName();

        // Check if receiver has blocked messages from sender.
        Player pReceiver = receiver instanceof Player ? (Player) receiver : null;
        if (pReceiver != null) {
            SunUser userGeter = plugin.getUserManager().getUserData(pReceiver);
            IgnoredUser ignoredUser = userGeter.getIgnoredUser(nameFrom);
            if (ignoredUser != null && ignoredUser.isDenyConversations()) {
                this.errorPermission(sender);
                return;
            }
        }

        String text = Stream.of(args).skip(this.getType() == Type.INITIAL ? 1 : 0).collect(Collectors.joining(" "));
        String message = ChatUtils.legalizeMessage(MessageUtil.stripJson(Colorizer.strip(text.trim()))).strip();

        boolean isItemLink = ChatConfig.ITEM_SHOW_ENABLED.get() && message.contains(ChatConfig.ITEM_SHOW_PLACEHOLDER.get());
        if (isItemLink) {
            message = message.replace(ChatConfig.ITEM_SHOW_PLACEHOLDER.get(), "");
        }
        message = StringUtil.oneSpace(message);

        if (!Colorizer.strip(message).strip().isEmpty()) {
            // Call custom plugin event.
            PlayerPrivateMessageEvent messageEvent = new PlayerPrivateMessageEvent(sender, receiver, message);
            plugin.getPluginManager().callEvent(messageEvent);
            if (messageEvent.isCancelled()) return;

            LAST_MESSAGE.put(sender, receiver.getName());
            LAST_MESSAGE.put(receiver, sender.getName());

            ChatConfig.PM_FORMAT_OUTGOING.get()
                .replace(Placeholders.GENERIC_MESSAGE, message).replace(Placeholders.Player.replacer(receiver)).send(sender);

            ChatConfig.PM_FORMAT_INCOMING.get()
                .replace(Placeholders.GENERIC_MESSAGE, message).replace(Placeholders.Player.replacer(sender)).send(receiver);
        }

        if (isItemLink && sender instanceof Player player) {
            ItemStack item = player.getInventory().getItemInMainHand();
            String itemName = ItemUtil.getItemName(item);
            String itemValue = String.valueOf(ItemUtil.toBase64(item));

            // Call custom plugin event.
            PlayerPrivateMessageEvent messageEvent = new PlayerPrivateMessageEvent(sender, receiver, itemName);
            plugin.getPluginManager().callEvent(messageEvent);
            if (messageEvent.isCancelled()) return;

            String formatOut = Placeholders.Player.replacer(player).apply(ChatConfig.ITEM_SHOW_FORMAT_PM_OUT.get())
                .replace(Placeholders.ITEM_NAME, itemName)
                .replace(Placeholders.ITEM_VALUE, itemValue);
            MessageUtil.sendWithJson(sender, formatOut);

            String formatIn = Placeholders.Player.replacer(player).apply(ChatConfig.ITEM_SHOW_FORMAT_PM_IN.get())
                .replace(Placeholders.ITEM_NAME, itemName)
                .replace(Placeholders.ITEM_VALUE, itemValue);
            MessageUtil.sendWithJson(receiver, formatIn);
        }
    }
}
