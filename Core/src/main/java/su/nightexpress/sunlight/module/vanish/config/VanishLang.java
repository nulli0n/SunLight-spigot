package su.nightexpress.sunlight.module.vanish.config;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.tag.Tags.LIGHT_GRAY;
import static su.nightexpress.nightcore.util.text.tag.Tags.LIGHT_YELLOW;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_STATE;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class VanishLang implements LangContainer {

    public static final TextLocale COMMAND_VANISH_DESC = LangEntry.builder("Command.Vanish.Desc").text("Toggle Vanish.");

    public static final MessageLocale COMMAND_VANISH_NOTIFY = LangEntry.builder("Command.Vanish.Notify").chatMessage(
        LIGHT_GRAY.wrap("Vanish has been set on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale COMMAND_VANISH_TARGET = LangEntry.builder("Command.Vanish.Target").chatMessage(
        LIGHT_GRAY.wrap("Set Vanish on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " for " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );
}
