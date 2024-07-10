package su.nightexpress.sunlight.module.godmode.config;

import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.OutputType;

import static su.nightexpress.nightcore.language.tag.MessageTags.OUTPUT;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class GodLang {

    public static final LangString COMMAND_GOD_DESC      = LangString.of("GodMode.Command.God.Desc", "Toggle God Mode.");
    public static final LangString COMMAND_FOOD_GOD_DESC = LangString.of("Command.FoodGod.Desc", "Toggle Food God.");

    public static final LangText COMMAND_FOOD_GOD_TARGET = LangText.of("GodMode.Command.FoodGod.Target",
        LIGHT_GRAY.enclose("Set Food God on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_FOOD_GOD_NOTIFY = LangText.of("GodMode.Command.FoodGod.Notify",
        LIGHT_GRAY.enclose("Food God has been set on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );


    public static final LangText COMMAND_GOD_TOGGLE_NOTIFY = LangText.of("GodMode.Command.God.Notify",
        LIGHT_GRAY.enclose("God Mode has been set on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_GOD_TOGGLE_TARGET = LangText.of("GodMode.Command.God.Target",
        LIGHT_GRAY.enclose("Set God Mode on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + " for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );


    public static final LangText NOTIFY_DISABLED_DAMAGE = LangText.of("GodMode.Notify.DamageDisabled",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_RED.enclose("You can't inflict damage in God Mode!")
    );

    public static final LangText NOTIFY_BAD_WORLD = LangText.of("GodMode.Notify.BadWorld",
        LIGHT_RED.enclose(BOLD.enclose("God Mode Info:")),
        LIGHT_GRAY.enclose("God Mode has no effect in this world!")
    );
}
