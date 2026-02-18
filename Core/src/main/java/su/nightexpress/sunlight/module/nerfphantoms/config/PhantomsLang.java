package su.nightexpress.sunlight.module.nerfphantoms.config;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class PhantomsLang implements LangContainer {

    public static final TextLocale COMMAND_PHANTOMS_TOGGLE_DESC = LangEntry.builder("NerfPhantoms.Command.NoPhantom.Toggle.Desc").text("Toggle phantoms.");
    public static final TextLocale COMMAND_PHANTOMS_ROOT_DESC   = LangEntry.builder("NerfPhantoms.Command.NoPhantom.Root.Desc").text("Phantoms commands.");

    public static final MessageLocale COMMAND_NO_PHANTOM_TOGGLE_NOTIFY = LangEntry.builder("NerfPhantoms.Command.NoPhantom.Toggle.Notify").chatMessage(
        GRAY.wrap("Anti-Phantom mode: " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale COMMAND_NO_PHANTOM_TOGGLE_OTHERS = LangEntry.builder("NerfPhantoms.Command.NoPhantom.Toggle.Others").chatMessage(
        GRAY.wrap("Set Anti-Phantom mode " + WHITE.wrap(GENERIC_STATE) + " for " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

}
