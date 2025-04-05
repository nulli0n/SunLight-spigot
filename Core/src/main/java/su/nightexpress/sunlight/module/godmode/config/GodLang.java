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
        LIGHT_GRAY.wrap("Set Food God on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " for " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_FOOD_GOD_NOTIFY = LangText.of("GodMode.Command.FoodGod.Notify",
        LIGHT_GRAY.wrap("Food God has been set on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );


    public static final LangText COMMAND_GOD_TOGGLE_NOTIFY = LangText.of("GodMode.Command.God.Notify",
        LIGHT_GRAY.wrap("God Mode has been set on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_GOD_TOGGLE_TARGET = LangText.of("GodMode.Command.God.Target",
        LIGHT_GRAY.wrap("Set God Mode on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " for " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );


    public static final LangText NOTIFY_DISABLED_DAMAGE = LangText.of("GodMode.Notify.DamageDisabled",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        LIGHT_RED.wrap("You can't inflict damage in God Mode!")
    );

    public static final LangText NOTIFY_BAD_WORLD = LangText.of("GodMode.Notify.BadWorld",
        LIGHT_RED.wrap(BOLD.wrap("God Mode Info:")),
        LIGHT_GRAY.wrap("God Mode has no effect in this world!")
    );
}
