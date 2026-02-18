package su.nightexpress.sunlight.module.afk.core;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.BooleanLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class AfkLang implements LangContainer {

    public static final TextLocale COMMAND_AFK_OFF_DESC    = LangEntry.builder("Afk.Command.Afk.Off.Desc").text("Exit AFK mode.");
    public static final TextLocale COMMAND_AFK_ON_DESC     = LangEntry.builder("Afk.Command.Afk.On.Desc").text("Enter AFK mode.");
    public static final TextLocale COMMAND_AFK_TOGGLE_DESC = LangEntry.builder("Afk.Command.Afk.Toggle.Desc").text("Toggle AFK mode.");

    public static final BooleanLocale PLACEHOLDER_MODE = LangEntry.builder("Afk.Placeholder.Mode").bool(
        DARK_GRAY.wrap(" [AFK]"),
        ""
    );

    public static final MessageLocale AFK_TOGGLE_FEEDBACK = LangEntry.builder("Afk.Command.Afk.Done.Others").chatMessage(
        GRAY.wrap("You have set " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + "'s AFK mode on " + WHITE.wrap(GENERIC_STATE) + "."));

    public static final MessageLocale AFK_ENTER_BROADCAST = LangEntry.builder("Afk.Mode.Enter").chatMessage(
        GRAY.wrap(WHITE.wrap(PLAYER_DISPLAY_NAME) + " is AFK now."));

    public static final MessageLocale AFK_EXIT_BROADCAST = LangEntry.builder("Afk.Mode.Exit").chatMessage(
        GRAY.wrap(WHITE.wrap(PLAYER_DISPLAY_NAME) + " is back. (was AFK for " + ORANGE.wrap(GENERIC_TIME) + ")"));
}
