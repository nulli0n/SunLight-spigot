package su.nightexpress.sunlight.module.chat.handler;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.PlayerChatData;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.util.ChatUtils;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandHandler {

    //private final SunLightPlugin               plugin;
    private final ChatModule                   module;
    private final PlayerCommandPreprocessEvent event;
    private final Player                       player;
    private final PlayerChatData               chatData;

    private String rawMessage;
    private String commandPart;
    private String textPart;
    private Set<String> aliases;

    public CommandHandler(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull PlayerCommandPreprocessEvent event) {
        //this.plugin = plugin;
        this.module = module;
        this.event = event;
        this.player = event.getPlayer();
        this.chatData = module.getChatData(this.player);
    }

    private void setMessage(@NotNull String message) {
        String[] split = message.split(" ");
        this.commandPart = split[0];
        this.textPart = split.length >= 2 ? Stream.of(split).skip(1).collect(Collectors.joining(" ")) : "";
        this.rawMessage = ChatUtils.cleanMessage(message);

        String commandName = CommandUtil.getCommandName(this.commandPart);
        this.aliases = CommandUtil.getAliases(commandName, true);
    }

    public boolean handle() {
        if (!this.checkCooldown()) return false;

        this.setMessage(this.event.getMessage());

        if (!this.checkAntiSpam()) return false;
        if (!this.checkRules()) return false;

        this.applyAntiCaps();

        String result = (this.commandPart + " " + this.textPart).trim();

        this.event.setMessage(result);

        if (!this.player.hasPermission(ChatPerms.BYPASS_ANTISPAM)) {
            this.chatData.setLastCommand(result);
        }
        if (!this.player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND)) {
            this.chatData.setCommandCooldown();
        }

        return true;
    }

    private boolean checkCooldown() {
        if (this.player.hasPermission(ChatPerms.BYPASS_COOLDOWN_COMMAND)) return true;
        if (!this.chatData.hasCommandCooldown()) return true;

        String time = TimeUtil.formatDuration(this.chatData.getNextCommandTimestamp());
        ChatLang.ANTI_SPAM_DELAY_COMMAND.getMessage().replace(Placeholders.GENERIC_TIME, time).send(this.player);

        return false;
    }

    private boolean checkAntiSpam() {
        if (!ChatUtils.isAntiSpamEnabled()) return true;
        if (this.player.hasPermission(ChatPerms.BYPASS_ANTISPAM)) return true;

        Set<String> whitelist = ChatConfig.ANTI_SPAM_COMMAND_WHITELIST.get();
        if (this.aliases.stream().anyMatch(whitelist::contains)) return true;

        if (this.chatData.isSpamCommand(this.rawMessage)) {
            ChatLang.ANTI_SPAM_SIMILAR_COMMAND.getMessage().send(this.player);
            return false;
        }

        return true;
    }

    private boolean checkRules() {
        if (this.player.hasPermission(ChatPerms.BYPASS_RULES)) return true;

        String result = this.module.getRuleManager().checkRules(this.player, this.textPart, ChatUtils.cleanMessage(this.textPart));
        if (result == null) return false;

        this.textPart = result;
        return true;
    }

    private void applyAntiCaps() {
        if (!ChatUtils.isAntiCapsEnabled()) return;
        if (this.player.hasPermission(ChatPerms.BYPASS_ANTICAPS)) return;

        Set<String> goodlist = ChatConfig.ANTI_CAPS_AFFECTED_COMMANDS.get();
        if (this.aliases.stream().noneMatch(goodlist::contains)) return;

        this.textPart = ChatUtils.doAntiCaps(this.textPart);
    }
}
