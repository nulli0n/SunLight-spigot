package su.nightexpress.sunlight.module.texts;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.sunlight.SLPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class TextsLang implements LangContainer {

    public static final TextLocale COMMAND_TEXT_DESC = LangEntry.builder("CustomText.Command.Text.Desc").text("View a text content.");

    public static final MessageLocale COMMAND_SYNTAX_INVALID_TEXT = LangEntry.builder("CustomText.Error.Command.Argument.InvalidCustomText").chatMessage(
        GRAY.wrap(SOFT_RED.wrap(GENERIC_INPUT) + " is not a valid custom text!")
    );
}
