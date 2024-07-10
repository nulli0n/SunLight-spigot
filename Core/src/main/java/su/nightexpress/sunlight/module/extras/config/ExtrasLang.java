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
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose("Chairs") + " on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_CHAIRS_TARGET = LangText.of("Extras.Command.Chairs.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.enclose("Chairs") + " on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );


    public static final LangText COMMAND_SIT_NOTIFY = LangText.of("Extras.Command.Sit.Notify",
        LIGHT_GRAY.enclose("You are sitting now.")
    );

    public static final LangText COMMAND_SIT_TARGET = LangText.of("Extras.Command.Sit.Target",
        LIGHT_GRAY.enclose("Made " + LIGHT_YELLOW.enclose(PLAYER_NAME) + " to sit.")
    );


    public static final LangText COMMAND_CHEST_SORT_NOTIFY = LangText.of("Extras.Command.ChestSort.Notify",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose("Chest Sort") + " on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );

    public static final LangText COMMAND_CHEST_SORT_TARGET = LangText.of("Extras.Command.ChestSort.Target",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.enclose("Chest Sort") + " on " + LIGHT_YELLOW.enclose(GENERIC_STATE) + ".")
    );

}
