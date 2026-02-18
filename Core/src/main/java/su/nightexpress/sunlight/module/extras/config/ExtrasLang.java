package su.nightexpress.sunlight.module.extras.config;

import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class ExtrasLang implements LangContainer {

    public static final TextLocale COMMAND_CHAIRS_DESC = LangEntry.builder("Extras.Command.Chairs.Desc").text("Toggle Chairs.");
    public static final TextLocale COMMAND_SIT_DESC    = LangEntry.builder("Extras.Command.Sit.Desc").text("Sit on a block.");
    public static final TextLocale COMMAND_CHEST_SORT_DESC = LangEntry.builder("Extras.Command.ChestSort.Desc").text("Toggle ChestSort.");

    public static final MessageLocale COMMAND_CHAIRS_NOTIFY = LangEntry.builder("Extras.Command.Chairs.Notify").chatMessage(
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap("Chairs") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale COMMAND_CHAIRS_TARGET = LangEntry.builder("Extras.Command.Chairs.Target").chatMessage(
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.wrap("Chairs") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );


    public static final MessageLocale COMMAND_SIT_NOTIFY = LangEntry.builder("Extras.Command.Sit.Notify").chatMessage(
        LIGHT_GRAY.wrap("You are sitting now.")
    );

    public static final MessageLocale COMMAND_SIT_TARGET = LangEntry.builder("Extras.Command.Sit.Target").chatMessage(
        LIGHT_GRAY.wrap("Made " + LIGHT_YELLOW.wrap(PLAYER_NAME) + " to sit.")
    );


    public static final MessageLocale COMMAND_CHEST_SORT_NOTIFY = LangEntry.builder("Extras.Command.ChestSort.Notify").chatMessage(
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap("Chest Sort") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale COMMAND_CHEST_SORT_TARGET = LangEntry.builder("Extras.Command.ChestSort.Target").chatMessage(
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "'s " + LIGHT_YELLOW.wrap("Chest Sort") + " on " + LIGHT_YELLOW.wrap(GENERIC_STATE) + ".")
    );

}
