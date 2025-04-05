package su.nightexpress.sunlight.module.afk.config;

import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class AfkLang {

    public static final LangString COMMAND_AFK_DESC = LangString.of("Afk.Command.Afk.Desc", "Toggle AFK mode.");

    public static final LangText COMMAND_AFK_DONE_OTHERS = LangText.of("Afk.Command.Afk.Done.Others",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " afk mode: " + LIGHT_YELLOW.wrap(GENERIC_STATE) + "."));

    public static final LangText AFK_ENTER = LangText.of("Afk.Mode.Enter",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("Player " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " is AFK now.")
    );

    public static final LangText AFK_EXIT = LangText.of("Afk.Mode.Exit",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("Player " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " returned after " + LIGHT_YELLOW.wrap(GENERIC_TIME) + " of afk.")
    );

    public static final LangText AFK_NOTIFY_PM = LangText.of("Afk.Notify.PrivateMessage",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("Player " + LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + " is AFK and may not respond.")
    );

    public static final LangText AFK_NOTIFY_TELEPORT = LangText.of("Afk.Notify.TeleportRequest",
        TAG_NO_PREFIX,
        LIGHT_GRAY.wrap("Player " + LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + " is AFK and may not respond.")
    );
}
