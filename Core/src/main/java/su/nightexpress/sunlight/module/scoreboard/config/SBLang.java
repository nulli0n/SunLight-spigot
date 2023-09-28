package su.nightexpress.sunlight.module.scoreboard.config;

import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.Placeholders;

import static su.nexmedia.engine.utils.Colors.*;

public class SBLang {

    public static final LangKey COMMAND_SCOREBOARD_DESC   = LangKey.of("Scoreboard.Command.Scoreboard.Desc", "Toggle [player's] scoreboard.");
    public static final LangKey COMMAND_SCOREBOARD_USAGE  = LangKey.of("Scoreboard.Command.Scoreboard.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_SCOREBOARD_NOTIFY = LangKey.of("Scoreboard.Command.Scoreboard.Notify", LIGHT_YELLOW + "Scoreboard " + ORANGE + Placeholders.GENERIC_STATE);
    public static final LangKey COMMAND_SCOREBOARD_TARGET = LangKey.of("Scoreboard.Command.Scoreboard.Target", LIGHT_YELLOW + "Scoreboard " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");

}
