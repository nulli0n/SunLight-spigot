package su.nightexpress.sunlight.module.backlocation.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class BackLocationLang implements LangContainer {

    public static final TextLocale COMMAND_BACK_DESC       = LangEntry.builder("BackLocation.Command.Back.Desc").text("Teleport to previous location.");
    public static final TextLocale COMMAND_DEATH_BACK_DESC = LangEntry.builder("BackLocation.Command.DeathBack.Desc").text("Teleport to death location.");

    public static final MessageLocale PREVIOUS_ERROR_NOTHING_FEEDBACK = LangEntry.builder("BackLocation.Command.Back.Others.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(ORANGE.wrap(PLAYER_DISPLAY_NAME) + " don't have a location to return to.")
    );

    public static final MessageLocale PREVIOUS_ERROR_NOTHING_NOTIFY = LangEntry.builder("BackLocation.Previous.Teleport.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have a location to return to.")
    );

    public static final MessageLocale PREVIOUS_TELEPORT_FEEDBACK = LangEntry.builder("BackLocation.Command.Back.Others.Done").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You have teleported " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " to their previous location.")
    );

    public static final MessageLocale PREVIOUS_TELEPORT_NOTIFY = LangEntry.builder("BackLocation.Previous.Teleport.Notify").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You have teleported to your previous location.")
    );



    public static final MessageLocale DEATH_ERROR_NOTHING_FEEDBACK = LangEntry.builder("BackLocation.Command.DeathBack.Others.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap(ORANGE.wrap(PLAYER_DISPLAY_NAME) + " don't have a death location.")
    );

    public static final MessageLocale DEATH_ERROR_NOTHING_NOTIFY = LangEntry.builder("BackLocation.Death.Teleport.Nothing").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You don't have a death location.")
    );

    public static final MessageLocale DEATH_TELEPORT_FEEDBACK = LangEntry.builder("BackLocation.Command.DeathBack.Others.Done").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You have teleported " + ORANGE.wrap(PLAYER_DISPLAY_NAME) + " to their death location.")
    );

    public static final MessageLocale DEATH_TELEPORT_NOTIFY = LangEntry.builder("BackLocation.Death.Teleport.Notify").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You have teleported to your death location.")
    );
}
