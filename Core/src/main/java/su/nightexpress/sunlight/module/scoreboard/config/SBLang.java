package su.nightexpress.sunlight.module.scoreboard.config;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class SBLang implements LangContainer {

    public static final TextLocale COMMAND_SCOREBOARD_TOGGLE_DESC = LangEntry.builder("Scoreboard.Command.Scoreboard-Toggle.Desc").text("Toggle scoreboard.");
    public static final TextLocale COMMAND_SCOREBOARD_ON_DESC     = LangEntry.builder("Scoreboard.Command.Scoreboard-On.Desc").text("Enable scoreboard.");
    public static final TextLocale COMMAND_SCOREBOARD_OFF_DESC    = LangEntry.builder("Scoreboard.Command.Scoreboard-Off.Desc").text("Disable scoreboard.");

    public static final MessageLocale COMMAND_SCOREBOARD_NOTIFY = LangEntry.builder("Scoreboard.Command.Scoreboard.Notify").chatMessage(
        GRAY.wrap("You have set scoreboard to " + ORANGE.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale COMMAND_SCOREBOARD_TARGET = LangEntry.builder("Scoreboard.Command.Scoreboard.Target").chatMessage(
        GRAY.wrap("You have set scoreboard to " + ORANGE.wrap(GENERIC_STATE) + " for " + WHITE.wrap(PLAYER_NAME) + ".")
    );

}
