package su.nightexpress.sunlight.module.extras.config;

import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class ExtrasLang {

    public static final LangString COMMAND_CHAIRS_DESC     = LangString.of("Extras.Command.Chairs.Desc", "Toggle Chairs.");
    public static final LangString COMMAND_SIT_DESC        = LangString.of("Extras.Command.Sit.Desc", "Sit on a block.");
    public static final LangString COMMAND_CHEST_SORT_DESC = LangString.of("Extras.Command.ChestSort.Desc", "Toggle ChestSort.");

    public static final LangText COMMAND_CHAIRS_NOTIFY = LangText.of("Extras.Command.Chairs.Notify",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap("Chairs") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_CHAIRS_TARGET = LangText.of("Extras.Command.Chairs.Target",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.wrap("Chairs") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );


    public static final LangText COMMAND_SIT_NOTIFY = LangText.of("Extras.Command.Sit.Notify",
        LIGHT_GRAY.wrap("You are sitting now.")
    );

    public static final LangText COMMAND_SIT_TARGET = LangText.of("Extras.Command.Sit.Target",
        LIGHT_GRAY.wrap("Made " + LIGHT_YELLOW.wrap(PLAYER_NAME) + " to sit.")
    );


    public static final LangText COMMAND_CHEST_SORT_NOTIFY = LangText.of("Extras.Command.ChestSort.Notify",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap("Chest Sort") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_CHEST_SORT_TARGET = LangText.of("Extras.Command.ChestSort.Target",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.wrap("Chest Sort") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

}
