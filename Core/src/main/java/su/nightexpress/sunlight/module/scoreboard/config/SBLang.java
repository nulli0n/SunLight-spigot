package su.nightexpress.sunlight.module.scoreboard.config;

import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class SBLang {

    public static final LangString COMMAND_SCOREBOARD_DESC = LangString.of("Scoreboard.Command.Scoreboard.Desc",
        "Toggle scoreboard.");

    public static final LangText COMMAND_SCOREBOARD_NOTIFY = LangText.of("Scoreboard.Command.Scoreboard.Notify",
        LIGHT_GRAY.wrap("Scoreboard: " + LIGHT_YELLOW.wrap(GENERIC_STATE))
    );

    public static final LangText COMMAND_SCOREBOARD_TARGET = LangText.of("Scoreboard.Command.Scoreboard.Target",
        LIGHT_GRAY.wrap("Set Scoreboard " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );

}
