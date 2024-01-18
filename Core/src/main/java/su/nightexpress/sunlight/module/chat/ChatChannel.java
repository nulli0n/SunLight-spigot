package su.nightexpress.sunlight.module.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.*;
import java.util.stream.Collectors;

public class ChatChannel implements Placeholder {

    private final String         id;
    private final String         name;
    private final boolean        isDefault;
    private final boolean        isAutoJoin;
    private final boolean        isPermissionRequiredHear;
    private final boolean        isPermissionRequiredSpeak;
    private final int            distance;
    private final int            messageCooldown;
    private final String         commandAlias;
    private final String         messagePrefix;
    private final String         format;
    private final PlaceholderMap placeholderMap;

    private final Set<Player> players;

    public ChatChannel(@NotNull String id,
                       @NotNull String name,
                       boolean isDefault,
                       boolean isAutoJoin,
                       boolean isPermissionRequiredHear,
                       boolean isPermissionRequiredSpeak,
                       int distance, int messageCooldown,
                       @NotNull String commandAlias,
                       @NotNull String messagePrefix,
                       @NotNull String format) {
        this.id = id.toLowerCase();
        this.name = Colorizer.apply(name);
        this.isDefault = isDefault;
        this.isAutoJoin = isAutoJoin;
        this.isPermissionRequiredHear = isPermissionRequiredHear;
        this.isPermissionRequiredSpeak = isPermissionRequiredSpeak;
        this.distance = distance;
        this.messageCooldown = messageCooldown;
        this.commandAlias = commandAlias;
        this.messagePrefix = messagePrefix;
        this.format = Colorizer.apply(format);
        this.players = new HashSet<>();

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.CHANNEL_ID, this::getId)
            .add(Placeholders.CHANNEL_NAME, this::getName)
            .add(Placeholders.CHANNEL_RADIUS, () -> String.valueOf(this.getDistance()));
    }

    @NotNull
    public static ChatChannel read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        String name = JOption.create(path + ".Name", StringUtil.capitalizeUnderscored(id),
            "Channel display name."
        ).read(cfg);

        boolean isDefault = JOption.create(path + ".Default", false,
            "Sets if this channel is default channel.",
            "Default channel is used when player does not specify any channel in message via prefix or command.",
            "You must have at least ONE default channel."
        ).read(cfg);

        boolean isAutoJoin = JOption.create(path + ".Auto_Join", false,
            "When enabled, players will automatically join this channel on server join.",
            "Player must have channel permission to be able to join it (if enabled)."
        ).read(cfg);

        boolean isPermissionRequiredHear = JOption.create(path + ".Permission_Required.Hear", false,
            "When enabled, players will must have permission to be able to read (receive) messages in this channel, as well as ability to join it."
        ).read(cfg);

        boolean isPermissionRequiredSpeak = JOption.create(path + ".Permission_Required.Speak", false,
            "When enabled, players will must have permission to be able to write messages in this channel."
        ).read(cfg);

        int radius = JOption.create(path + ".Radius", -1,
            "Sets reach distance for channel messages.",
            "All players in that radius from a player who sent message will be able to see it.",
            "Set this to -1 to make channel server-wide."
        ).read(cfg);

        int messageCooldown = JOption.create(path + ".Message_Cooldown", 2,
            "Sets per-player messages cooldown (in seconds) for this channel.",
            "Set this to 0 to disable."
        ).read(cfg);

        String commandAlias = JOption.create(path + ".Command_Alias", "",
            "Sets custom command-shortcut to join/write this channel.",
            "Do not leave this empty."
        ).read(cfg);

        String messagePrefix = JOption.create(path + ".Message_Prefix", "",
            "Sets custom message prefix for this channel.",
            "This prefix can be used at a start of message to write in this channel.",
            "Leave this empty ('') to disable."
        ).read(cfg);

        String format = JOption.create(path + ".Format",
            "&b[%channel_name%] &7%format_player_name%%format_player_color%: %format_player_message%",
            "Sets the chat format for the channel.",
            "PlaceholderAPI is supported here.",
            "JSON Formatting is allowed: " + Placeholders.ENGINE_URL_LANG_JSON,
            "Channel Placeholders:",
            Placeholders.CHANNEL_ID + " - Channel unique identifier.",
            Placeholders.CHANNEL_NAME + " - Channel display name.",
            Placeholders.CHANNEL_RADIUS + " - Channel message radius."
        ).read(cfg);

        return new ChatChannel(id, name, isDefault, isAutoJoin,
            isPermissionRequiredHear, isPermissionRequiredSpeak,
            radius, messageCooldown,
            commandAlias, messagePrefix, format);
    }

    @NotNull
    public Collection<Player> getRecipients(@NotNull Player speaker) {
        return this.getPlayers().stream().filter(recipient -> this.isInRadius(speaker, recipient)).collect(Collectors.toSet());
    }

    public boolean isPrefix(@NotNull String prefix) {
        return !this.getMessagePrefix().isEmpty() && prefix.startsWith(this.getMessagePrefix());
    }

    public boolean isCommand(@NotNull String command) {
        return this.getCommandAlias().equalsIgnoreCase(command);
    }

    public boolean canJoin(@NotNull Player player) {
        return this.canHear(player) || this.canSpeak(player);
    }

    public boolean join(@NotNull Player player) {
        return this.canJoin(player) && this.addPlayer(player);
    }

    public boolean addPlayer(@NotNull Player player) {
        return this.getPlayers().add(player);
    }

    public boolean removePlayer(@NotNull Player player) {
        return this.getPlayers().remove(player);
    }

    public boolean contains(@NotNull Player player) {
        return this.getPlayers().contains(player);
    }

    public boolean canSpeak(@NotNull Player player) {
        return this.isDefault() || !this.isPermissionRequiredSpeak()
            || player.hasPermission(ChatPerms.CHANNEL_SPEAK + this.getId());
    }

    public boolean canHear(@NotNull Player player) {
        return this.canSpeak(player) || !this.isPermissionRequiredHear()
            || player.hasPermission(ChatPerms.CHANNEL_HEAR + this.getId());
    }

    public boolean isInRadius(@NotNull Player speaker, @NotNull Player recipient) {
        if (this.getDistance() <= 0) return true;
        if (recipient.hasPermission(ChatPerms.BYPASS_CHANNEL_DISTANCE_HEAR)) return true;
        if (speaker.hasPermission(ChatPerms.BYPASS_CHANNEL_DISTANCE_SPEAK)) return true;

        if (!speaker.getWorld().equals(recipient.getWorld())) return false;
        return speaker.getLocation().distance(recipient.getLocation()) <= this.getDistance();
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public boolean isAutoJoin() {
        return isAutoJoin;
    }

    public boolean isPermissionRequiredHear() {
        return isPermissionRequiredHear;
    }

    public boolean isPermissionRequiredSpeak() {
        return isPermissionRequiredSpeak;
    }

    public int getDistance() {
        return distance;
    }

    public int getMessageCooldown() {
        return messageCooldown;
    }

    @NotNull
    public String getMessagePrefix() {
        return messagePrefix;
    }

    @NotNull
    public String getCommandAlias() {
        return commandAlias;
    }

    @NotNull
    public String getFormat() {
        return this.replacePlaceholders().apply(this.format);
    }

    @NotNull
    public Set<Player> getPlayers() {
        return players;
    }

    /*@NotNull
    private String replaceBadBrackets(@NotNull String format) {
        Matcher matchPercents = RegexUtil.getMatcher(PATTERN_BRACKETS, format);
        if (matchPercents != null) {
            while (matchPercents.find()) {
                String match = matchPercents.group(0);
                String fined = "{" + match.substring(1, match.length() - 1) + "}";
                format = format.replace(match, fined);
            }
        }
        return format;
    }*/
}
