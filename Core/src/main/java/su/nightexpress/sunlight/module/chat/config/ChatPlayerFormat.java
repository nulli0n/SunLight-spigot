package su.nightexpress.sunlight.module.chat.config;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.MessageUtil;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

public class ChatPlayerFormat {

    private final int priority;
    private final String nameFormat;
    private final String messageFormat;
    private final ChatColor color;

    public ChatPlayerFormat(int priority, @NotNull String nameFormat, @NotNull String messageFormat, @NotNull String color) {
        this.priority = priority;
        this.nameFormat = Colorizer.apply(nameFormat);
        this.messageFormat = Colorizer.apply(messageFormat);
        ChatColor chatColor;
        try {
            chatColor = ChatColor.of(color);
        }
        catch (IllegalArgumentException e) {
            chatColor = ChatColor.GRAY;
        }
        this.color = chatColor;
    }

    @NotNull
    public static ChatPlayerFormat read(@NotNull JYML cfg, @NotNull String path) {
        int priority = cfg.getInt(path + ".Priority");
        String nameFormat = cfg.getString(path + ".Name", "");
        String messageFormat = cfg.getString(path + ".Message", "");
        String color = cfg.getString(path + ".Color", ChatColor.GRAY.getName());
        return new ChatPlayerFormat(priority, nameFormat, messageFormat, color);
    }

    public static void write(@NotNull ChatPlayerFormat format, @NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Priority", format.getPriority());
        cfg.set(path + ".Name", format.getNameFormat());
        cfg.set(path + ".Message", format.getMessageFormat());
        cfg.set(path + ".Default_Color", format.getColor().getName());
    }

    @NotNull
    public String prepareFormat(@NotNull Player player, @NotNull String format) {
        format = format
            .replace(Placeholders.FORMAT_PLAYER_NAME, this.getNameFormat())
            .replace(Placeholders.FORMAT_PLAYER_MESSAGE, Placeholders.GENERIC_MESSAGE)
            .replace(Placeholders.FORMAT_PLAYER_COLOR, this.getColor().toString())
            .replace(Placeholders.PLAYER_PREFIX, Hooks.getPrefix(player))
            .replace(Placeholders.PLAYER_SUFFIX, Hooks.getSuffix(player))
            .replace(Placeholders.PLAYER_DISPLAY_NAME, player.getDisplayName())
            .replace(Placeholders.PLAYER_NAME, player.getName())
            .replace(Placeholders.PLAYER_WORLD, LangManager.getWorld(player.getWorld()));
        if (Hooks.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }
        return format;
    }

    @NotNull
    public String prepareMessage(@NotNull Player player, @NotNull String message) {
        // If player's message already contains some Json elements (mentions, item showcase, etc),
        // we need to 'fix' the message by replacing non-json parts with the Player Format before other placeholders.
        // Player Format can also contain Json elements, but Engine can not handle json in json, this is why we need to do this.
        if (MessageUtil.hasJson(message)) {
            for (String nonJson : MessageUtil.extractNonJson(message)) {
                message = message.replace(nonJson, this.getMessageFormat(player).replace(Placeholders.GENERIC_MESSAGE, nonJson));
            }
        }
        else {
            message = this.getMessageFormat(player).replace(Placeholders.GENERIC_MESSAGE, message);
        }
        // Replace default player color for message (it may popup from mentions and item showcase formatting).
        return message.replace(Placeholders.FORMAT_PLAYER_COLOR, this.getColor().toString());
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getNameFormat() {
        return nameFormat;
    }

    @NotNull
    public String getMessageFormat() {
        return messageFormat;
    }

    @NotNull
    public String getMessageFormat(@NotNull Player player) {
        String format = this.getMessageFormat();
        if (Hooks.hasPlaceholderAPI()) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }
        return format;
    }

    @NotNull
    public ChatColor getColor() {
        return color;
    }
}
