package su.nightexpress.sunlight.module.customtext.config;

import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.sunlight.Placeholders.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class CTextLang {


    public static final LangString COMMAND_TEXT_DESC = LangString.of("CustomText.Command.Text.Desc", "View a custom text.");

    public static final LangText ERROR_COMMAND_INVALID_TEXT_ARGUMENT = LangText.of("CustomText.Error.Command.Argument.InvalidCustomText",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid custom text!")
    );
}
