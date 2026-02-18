package su.nightexpress.sunlight.module.spawners;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class SpawnersLang implements LangContainer {

    public static final TextLocale COMMAND_SPAWNER_DESC = LangEntry.builder("Command.Spawner.Desc").text("Change spawner type.");

    public static final MessageLocale COMMAND_SPAWNER_DONE = LangEntry.builder("Command.Spawner.Done").chatMessage(
        GRAY.wrap("Spawner type changed to " + SOFT_YELLOW.wrap(GENERIC_TYPE) + ".")
    );

    public static final MessageLocale COMMAND_SPAWNER_ERROR_TYPE = LangEntry.builder("Command.Spawner.Error.Type").chatMessage(
        GRAY.wrap("This type can not be set for spawner.")
    );

    public static final MessageLocale COMMAND_SPAWNER_ERROR_BLOCK = LangEntry.builder("Command.Spawner.Error.Block").chatMessage(
        GRAY.wrap("You must look at Spawner block!")
    );
}
