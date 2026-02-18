package su.nightexpress.sunlight.module.chat.channel;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.Enums;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.chat.ChatDefaults;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.nightcore.util.placeholder.CommonPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.module.chat.ChatPlaceholders.*;

public class ChannelSchema {

    public static final ConfigProperty<String> NAME = ConfigProperty.of(ConfigTypes.STRING, "Name", "Channel", "Channel display name.");

    public static final ConfigProperty<String> FORMAT = ConfigProperty.of(ConfigTypes.STRING, "Format",
        ChatDefaults.DEFAULT_CHANNEL_FORMAT,
        "Channel message format.",
        "[>] Text Formations: %s".formatted(SLPlaceholders.URL_WIKI_TEXT),
        "[>] %s Support: Yes".formatted(Plugins.PLACEHOLDER_API),
        "[>] Built-in Placeholders:",
        "- " + SLPlaceholders.GENERIC_FORMAT + " - User message format from the module's settings.",
        "- " + PLAYER_NAME + " - Player name.",
        "- " + PLAYER_DISPLAY_NAME + " - Player display (custom) name.",
        "- " + PLAYER_PREFIX + " - Player prefix (from your permissions plugin)",
        "- " + PLAYER_SUFFIX + " - Player suffix (from your permissions plugin)",
        "- " + PLAYER_WORLD + " - Player's world name.",
        "- " + CHANNEL_ID + " - Channel unique identifier.",
        "- " + CHANNEL_NAME + " - Channel display name.",
        "- " + CHANNEL_DISTANCE_TYPE + " - Channel distance type.",
        "- " + CHANNEL_DISTANCE_RANGE + " - Channel distance range."
    );

    public static final ConfigProperty<Boolean> AUTO_JOIN = ConfigProperty.of(ConfigTypes.BOOLEAN, "Auto_Join",
        true,
        "If enabled, players will automatically join this channel upon entering the server, provided they have the permissions to read or speak in it."
    );

    public static final ConfigProperty<Boolean> PERMISSION_TO_LISTEN = ConfigProperty.of(ConfigTypes.BOOLEAN, "Permission_Required.Hear",
        false,
        "If enabled, only players with the channel permission are able to read and join this channel."
    );

    public static final ConfigProperty<Boolean> PERMISSION_TO_SPEAK = ConfigProperty.of(ConfigTypes.BOOLEAN, "Permission_Required.Speak",
        false,
        "If enabled, only players with the channel permission are able to speak in this channel."
    );

    public static final ConfigProperty<Integer> MESSAGE_COOLDOWN = ConfigProperty.of(ConfigTypes.INT, "Message_Cooldown",
        1,
        "Sets per-player message cooldown (in seconds) for this channel.",
        "[>] Set 0 to disable."
    );



    public static final ConfigProperty<ChannelDistanceType> DISTANCE_TYPE = ConfigProperty.of(ConfigTypes.forEnum(ChannelDistanceType.class), "Distance.Type",
        ChannelDistanceType.SERVER_WIDE,
        "Channel distance mode.",
        "[>] Available Values: [ %s ]".formatted(Enums.inline(ChannelDistanceType.class))
    );

    public static final ConfigProperty<Double> DISTANCE_RANGE = ConfigProperty.of(ConfigTypes.DOUBLE, "Distance.Range",
        -1D,
        "Sets the hearing distance (in blocks) within which players will be able to see each other's messages.",
        "[*] Useful for the '%s' distance mode only.".formatted(ChannelDistanceType.RANGE.name())
    );



    public static final ConfigProperty<Boolean> COMMAND_ENABLED = ConfigProperty.of(ConfigTypes.BOOLEAN, "Command.Enabled",
        true,
        "If enabled, adds a dedicated one-shot channel command for players to join the channel."
    );

    public static final ConfigProperty<String> COMMAND_ALIAS = ConfigProperty.of(ConfigTypes.STRING, "Command.Alias",
        "",
        "Channel command alias."
    );



    public static final ConfigProperty<Boolean> PREFIX_ENABLED = ConfigProperty.of(ConfigTypes.BOOLEAN, "Prefix.Enabled",
        true,
        "If enabled, players can send messages to this channel by adding the channel prefix to the beginning of their message."
    );

    public static final ConfigProperty<String> PREFIX_VALUE = ConfigProperty.of(ConfigTypes.STRING, "Prefix.Value",
        "",
        "Channel message prefix."
    );

    @NotNull
    public static List<ChatChannel> getDefaultChannels() {
        List<ChatChannel> channels = new ArrayList<>();

        channels.add(ChatChannel.create(ChatDefaults.DEFAULT_LOCAL_CHANNEL_ID,
            new ChannelDisplay(SOFT_AQUA.wrap("Local"), ChatDefaults.DEFAULT_CHANNEL_FORMAT),
            new ChannelAccessibility(true, false, false, 1),
            new ChannelDistance(ChannelDistanceType.RANGE, 100),
            new ChannelCommand(true, "localchat"),
            new ChannelPrefix(true, ":")
        ));

        channels.add(ChatChannel.create("world",
            new ChannelDisplay(ORANGE.wrap("World"), ChatDefaults.DEFAULT_CHANNEL_FORMAT),
            new ChannelAccessibility(true, false, false, 3),
            new ChannelDistance(ChannelDistanceType.WORLD_WIDE, -1),
            new ChannelCommand(true, "worldchat"),
            new ChannelPrefix(true, ".")
        ));

        channels.add(ChatChannel.create("global",
            new ChannelDisplay(ORANGE.wrap("Global"), ChatDefaults.DEFAULT_CHANNEL_FORMAT),
            new ChannelAccessibility(true, false, false, 3),
            new ChannelDistance(ChannelDistanceType.SERVER_WIDE, -1),
            new ChannelCommand(true, "globalchat"),
            new ChannelPrefix(true, "!")
        ));

        channels.add(ChatChannel.create("trade",
            new ChannelDisplay(GREEN.wrap("Trade"), ChatDefaults.DEFAULT_CHANNEL_FORMAT),
            new ChannelAccessibility(true, false, true, 30),
            new ChannelDistance(ChannelDistanceType.SERVER_WIDE, -1),
            new ChannelCommand(true, "tradechat"),
            new ChannelPrefix(true, "$")
        ));

        channels.add(ChatChannel.create(
            "staff",
            new ChannelDisplay(SOFT_RED.wrap("Staff"), ChatDefaults.DEFAULT_CHANNEL_FORMAT),
            new ChannelAccessibility(true, true, true, 0),
            new ChannelDistance(ChannelDistanceType.SERVER_WIDE, -1),
            new ChannelCommand(true, "staffchat"),
            new ChannelPrefix(true, "#")
        ));

        return channels;
    }

    @NotNull
    public static ChatChannel createDefaultChannel() {
        return ChatChannel.create(
            ChatDefaults.DEFAULT_CHANNEL_ID,
            new ChannelDisplay(WHITE.wrap("Default"), SLPlaceholders.GENERIC_FORMAT),
            new ChannelAccessibility(true, false, false, 0),
            new ChannelDistance(ChannelDistanceType.SERVER_WIDE, -1),
            new ChannelCommand(false, ""),
            new ChannelPrefix(false, "")
        );
    }
}
