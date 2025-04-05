package su.nightexpress.sunlight.module.backlocation.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.OutputType;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;
import static su.nightexpress.sunlight.Placeholders.*;

public class BackLocationLang extends CoreLang {

    public static final LangString COMMAND_BACK_DESC = LangString.of("BackLocation.Command.Back.Desc",
        "Return to previous location.");

    public static final LangText COMMAND_BACK_OTHERS_DONE = LangText.of("BackLocation.Command.Back.Others.Done",
        LIGHT_GRAY.wrap("Player " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " teleported to previous location.")
    );

    public static final LangText COMMAND_BACK_OTHERS_NOTHING = LangText.of("BackLocation.Command.Back.Others.Nothing",
        LIGHT_GRAY.wrap("Player " + LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + " don't have a previous location.")
    );

    public static final LangString COMMAND_DEATH_BACK_DESC = LangString.of("BackLocation.Command.DeathBack.Desc",
        "Return to death location.");

    public static final LangText COMMAND_DEATH_BACK_OTHERS_DONE = LangText.of("BackLocation.Command.DeathBack.Others.Done",
        LIGHT_GRAY.wrap("Player " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " teleported to death location.")
    );

    public static final LangText COMMAND_DEATH_BACK_OTHERS_NOTHING = LangText.of("BackLocation.Command.DeathBack.Others.Nothing",
        LIGHT_GRAY.wrap("Player " + LIGHT_RED.wrap(PLAYER_DISPLAY_NAME) + " don't have a death location.")
    );



    public static final LangText PREVIOUS_TELEPORT_NOTHING = LangText.of("BackLocation.Previous.Teleport.Nothing",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You don't have a previous location.")
    );

    public static final LangText PREVIOUS_TELEPORT_NOTIFY = LangText.of("BackLocation.Previous.Teleport.Notify",
        OUTPUT.wrap(OutputType.ACTION_BAR) + SOUND.wrap(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_YELLOW.wrap("Return to previous location...")
    );



    public static final LangText DEATH_TELEPORT_NOTHING = LangText.of("BackLocation.Death.Teleport.Nothing",
        SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_GRAY.wrap("You don't have a death location.")
    );

    public static final LangText DEATH_TELEPORT_NOTIFY = LangText.of("BackLocation.Death.Teleport.Notify",
        OUTPUT.wrap(OutputType.ACTION_BAR) + SOUND.wrap(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_YELLOW.wrap("Return to death location...")
    );
}
