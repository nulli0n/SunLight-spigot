package su.nightexpress.sunlight.module.nerfphantoms.config;

import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class PhantomsLang extends CoreLang {

    public static final LangString COMMAND_NO_PHANTOM_DESC = LangString.of("NerfPhantoms.Command.NoPhantom.Desc",
        "Toggle anti-phantom mode.");

    public static final LangText COMMAND_NO_PHANTOM_TOGGLE_NOTIFY = LangText.of("NerfPhantoms.Command.NoPhantom.Toggle.Notify",
        LIGHT_GRAY.enclose("Anti-Phantom mode: " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_NO_PHANTOM_TOGGLE_OTHERS = LangText.of("NerfPhantoms.Command.NoPhantom.Toggle.Others",
        LIGHT_GRAY.enclose("Set Anti-Phantom mode " + LIGHT_YELLOW.enclose(GENERIC_STATE) + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

}
