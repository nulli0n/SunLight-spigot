package su.nightexpress.sunlight.module.greetings;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigType;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.greetings.message.GreetingMessage;
import su.nightexpress.sunlight.module.greetings.message.MessageType;

import java.util.*;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class GreetingsSettings extends AbstractConfig {

    private static final ConfigType<GreetingMessage> MESSAGE_CONFIG_TYPE = ConfigType.of(
        GreetingMessage::read,
        FileConfig::set
    );

    private final ConfigProperty<Map<String, GreetingMessage>> joinMessages = this.addProperty(ConfigTypes.forMapWithLowerKeys(MESSAGE_CONFIG_TYPE),
        "Messages.Join",
        getDefaultJoins(),
        "Create custom join messages here.",
        "[>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "[>] Placeholders to use in messages:",
        "- " + CommonPlaceholders.PLAYER_NAME + " -> Player name.",
        "- " + CommonPlaceholders.PLAYER_DISPLAY_NAME + " -> Player display (custom) name.",
        "- " + CommonPlaceholders.PLAYER_PREFIX + " -> Player prefix (from permissions plugin).",
        "- " + CommonPlaceholders.PLAYER_SUFFIX + " -> Player name (from permissions plugin).",
        "- " + Plugins.PLACEHOLDER_API
    );

    private final ConfigProperty<Map<String, GreetingMessage>> quitMessages = this.addProperty(ConfigTypes.forMapWithLowerKeys(MESSAGE_CONFIG_TYPE),
        "Messages.Quit",
        getDefaultQuits(),
        "Create custom quit messages here.",
        "[>] Text Formations: " + SLPlaceholders.URL_WIKI_TEXT,
        "[>] Placeholders to use in messages:",
        "- " + CommonPlaceholders.PLAYER_NAME + " -> Player name.",
        "- " + CommonPlaceholders.PLAYER_DISPLAY_NAME + " -> Player display (custom) name.",
        "- " + CommonPlaceholders.PLAYER_PREFIX + " -> Player prefix (from permissions plugin).",
        "- " + CommonPlaceholders.PLAYER_SUFFIX + " -> Player name (from permissions plugin).",
        "- " + Plugins.PLACEHOLDER_API
    );

    @NotNull
    private static Map<String, GreetingMessage> getDefaultJoins() {
        Map<String, GreetingMessage> map = new HashMap<>();

        map.put(DEFAULT, new GreetingMessage(0, GRAY.wrap("[" + GREEN.wrap("+") + "]" + " " + PLAYER_PREFIX + PLAYER_DISPLAY_NAME), Set.of(WILDCARD)));

        return map;
    }

    @NotNull
    private static Map<String, GreetingMessage> getDefaultQuits() {
        Map<String, GreetingMessage> map = new HashMap<>();

        map.put(DEFAULT, new GreetingMessage(0, GRAY.wrap("[" + RED.wrap("-") + "]" + " " + PLAYER_PREFIX + PLAYER_DISPLAY_NAME), Set.of(WILDCARD)));

        return map;
    }

    @NotNull
    public Map<String, GreetingMessage> getMessages(@NotNull MessageType type) {
        return switch (type) {
            case JOIN -> this.getJoinMessages();
            case QUIT -> this.getQuitMessages();
        };
    }

    @NotNull
    public Map<String, GreetingMessage> getJoinMessages() {
        return this.joinMessages.get();
    }

    @NotNull
    public Map<String, GreetingMessage> getQuitMessages() {
        return this.quitMessages.get();
    }
}
