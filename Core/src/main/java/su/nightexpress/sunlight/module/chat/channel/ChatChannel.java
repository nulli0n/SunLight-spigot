package su.nightexpress.sunlight.module.chat.channel;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static su.nightexpress.sunlight.module.chat.ChatPlaceholders.CHANNEL;

public class ChatChannel implements PlaceholderResolvable {

    private final Path      path;
    private final String    id;
    private final Set<UUID> players;

    private final ChannelDisplay       display;
    private final ChannelAccessibility accessibility;
    private final ChannelDistance      distance;
    private final ChannelCommand       command;
    private final ChannelPrefix        prefix;

    private ChatChannel(@Nullable Path path,
                        @NotNull String id,
                        @NotNull ChannelDisplay display,
                        @NotNull ChannelAccessibility accessibility,
                        @NotNull ChannelDistance distance,
                        @NotNull ChannelCommand command,
                        @NotNull ChannelPrefix prefix) {
        this.path = path;
        this.id = id;
        this.players = new HashSet<>();

        this.display = display;
        this.accessibility = accessibility;
        this.distance = distance;
        this.command = command;
        this.prefix = prefix;
    }

    @NotNull
    public static ChatChannel fromFile(@NotNull Path file) {
        FileConfig config = FileConfig.load(file);
        String fileName = file.getFileName().toString();
        String id = fileName.substring(0, fileName.length() - FileConfig.EXTENSION.length());

        String name = ChannelSchema.NAME.resolveWithDefaults(config);
        String format = ChannelSchema.FORMAT.resolveWithDefaults(config);

        boolean autoJoin = ChannelSchema.AUTO_JOIN.resolveWithDefaults(config);
        boolean permissionToListen = ChannelSchema.PERMISSION_TO_LISTEN.resolveWithDefaults(config);
        boolean permissionToSpeak = ChannelSchema.PERMISSION_TO_SPEAK.resolveWithDefaults(config);
        int messageCooldown = ChannelSchema.MESSAGE_COOLDOWN.resolveWithDefaults(config);

        ChannelDistanceType distanceType = ChannelSchema.DISTANCE_TYPE.resolveWithDefaults(config);
        double distanceRange = ChannelSchema.DISTANCE_RANGE.resolveWithDefaults(config);

        //boolean commandEnabled = ChannelSchema.COMMAND_ENABLED.resolveWithDefaults(config);
        //String commandAlias = ChannelSchema.COMMAND_ALIAS.resolveWithDefaults(config);

        boolean prefixEnabled = ChannelSchema.PREFIX_ENABLED.resolveWithDefaults(config);
        String prefixValue = ChannelSchema.PREFIX_VALUE.resolveWithDefaults(config);

        ChannelDisplay display = new ChannelDisplay(name, format);
        ChannelAccessibility accessibility = new ChannelAccessibility(autoJoin, permissionToListen, permissionToSpeak, messageCooldown);
        ChannelDistance distance = new ChannelDistance(distanceType, distanceRange);
        ChannelCommand command = new ChannelCommand(false, "");//new ChannelCommand(commandEnabled, commandAlias);
        ChannelPrefix prefix = new ChannelPrefix(prefixEnabled, prefixValue);

        config.saveChanges();

        return new ChatChannel(file, id, display, accessibility, distance, command, prefix);
    }

    @NotNull
    public static ChatChannel create(@NotNull String id,
                                     @NotNull ChannelDisplay display,
                                     @NotNull ChannelAccessibility accessibility,
                                     @NotNull ChannelDistance distance,
                                     @NotNull ChannelCommand command,
                                     @NotNull ChannelPrefix prefix) {
        return new ChatChannel(null, id, display, accessibility, distance, command, prefix);
    }

    public void write(@NotNull FileConfig config) {
        ChannelSchema.NAME.writeValue(config, this.display.name());
        ChannelSchema.FORMAT.writeValue(config, this.display.format());

        ChannelSchema.AUTO_JOIN.writeValue(config, this.accessibility.autoJoin());
        ChannelSchema.PERMISSION_TO_LISTEN.writeValue(config, this.accessibility.permissionToListen());
        ChannelSchema.PERMISSION_TO_SPEAK.writeValue(config, this.accessibility.permissionToSpeak());
        ChannelSchema.MESSAGE_COOLDOWN.writeValue(config, this.accessibility.messageCooldown());

        ChannelSchema.DISTANCE_TYPE.writeValue(config, this.distance.type());
        ChannelSchema.DISTANCE_RANGE.writeValue(config, this.distance.range());

        //ChannelSchema.COMMAND_ENABLED.writeValue(config, this.command.enabled());
        //ChannelSchema.COMMAND_ALIAS.writeValue(config, this.command.alias());

        ChannelSchema.PREFIX_ENABLED.writeValue(config, this.prefix.enabled());
        ChannelSchema.PREFIX_VALUE.writeValue(config, this.prefix.value());
    }

    @Override
    @NotNull
    public PlaceholderResolver placeholders() {
        return CHANNEL.resolver(this);
    }

    @Nullable
    public Path getPath() {
        return this.path;
    }

    public boolean canListenOrSpeakHere(@NotNull Player player) {
        return this.canListenHere(player) || this.canSpeakHere(player);
    }

    public boolean addPlayer(@NotNull Player player) {
        return this.players.add(player.getUniqueId());
    }

    public boolean removePlayer(@NotNull Player player) {
        return this.players.remove(player.getUniqueId());
    }

    public boolean contains(@NotNull Player player) {
        return this.players.contains(player.getUniqueId());
    }

    public boolean canSpeakHere(@NotNull Player player) {
        return !this.accessibility.permissionToSpeak() || ChatPerms.CHANNEL_SPEAK.hasChildAccess(player, this.id);
    }

    public boolean canListenHere(@NotNull Player player) {
        return !this.accessibility.permissionToListen() || this.canSpeakHere(player) || ChatPerms.CHANNEL_LISTEN.hasChildAccess(player, this.id);
    }

    public boolean isInRadius(@NotNull CommandSender recipient, @NotNull Player speaker) {
        ChannelDistanceType distanceType = this.distance.type();
        if (distanceType == ChannelDistanceType.SERVER_WIDE) return true;

        if (recipient instanceof Entity listener) {
            if (listener.getWorld() == speaker.getWorld()) {
                if (distanceType == ChannelDistanceType.WORLD_WIDE) return true;

                if (distanceType == ChannelDistanceType.RANGE) {
                    double range = this.distance.range();
                    if (range <= 0D) return true;

                    double rangeSqr = range * range;
                    double distanceSqr = listener.getLocation().distanceSquared(speaker.getLocation());

                    return distanceSqr <= rangeSqr;
                }
            }
        }

        return true;
    }

    public boolean isInChannelRadius(@NotNull CommandSender who, @NotNull Player speaker) {
        if (who == speaker) return true;
        if (who instanceof ConsoleCommandSender) return true;
        if (who instanceof Player other && !this.contains(other)) return false;

        return this.isInRadius(who, speaker);
    }

    public boolean hasPrefix() {
        return this.prefix.enabled() && !this.prefix.value().isBlank();
    }

    public char getPrefixChar() {
        return this.prefix.value().charAt(0);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public Set<UUID> getPlayers() {
        return this.players;
    }

    @NotNull
    public ChannelDisplay getDisplay() {
        return this.display;
    }

    @NotNull
    public ChannelAccessibility getAccessibility() {
        return this.accessibility;
    }

    @NotNull
    public ChannelDistance getDistance() {
        return this.distance;
    }

    @NotNull
    public ChannelCommand getCommand() {
        return this.command;
    }

    @NotNull
    public ChannelPrefix getPrefix() {
        return this.prefix;
    }
}
