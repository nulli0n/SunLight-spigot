package su.nightexpress.sunlight.module.essential;

import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class EssentialLang implements LangContainer {

    public static final TextLocale COMMAND_INVULNERABILITY_DESC = LangEntry.builder("Essential.Command.Invulnerability.Desc").text("Toggle Invulnerability.");

    public static final MessageLocale INVULNERABILITY_TOGGLE_NOTIFY = LangEntry.builder("Essential.Invulnerability.Toggle.Notify").chatMessage(
        GRAY.wrap("Your " + ORANGE.wrap("Invulnerability") + " has been set to " + WHITE.wrap(GENERIC_STATE) + ".")
    );

    public static final MessageLocale INVULNERABILITY_TOGGLE_FEEDBACK = LangEntry.builder("Essential.Invulnerability.Toggle.Feedback").chatMessage(
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + ORANGE.wrap("Invulnerability") + " to " + WHITE.wrap(GENERIC_STATE) + ".")
    );


    public static final MessageLocale INVULNERABILITY_RESTRICTED_MOB_DAMAGE = LangEntry.builder("Essential.Invulnerability.RestrictedMobDamage").actionBarMessage(
        GRAY.wrap("You can't damage " + RED.wrap("mobs") + " while invulnerable!")
    );

    public static final MessageLocale INVULNERABILITY_RESTRICTED_PLAYER_DAMAGE = LangEntry.builder("Essential.Invulnerability.RestrictedPlayerDamage").actionBarMessage(
        GRAY.wrap("You can't damage " + RED.wrap("players") + " while invulnerable!")
    );



    public static final MessageLocale INVULNERABILITY_RESTRICTED_WORLD_USAGE = LangEntry.builder("Essential.Invulnerability.RestrictedWorld.Usage").chatMessage(
        GRAY.wrap("You can't use " + ORANGE.wrap("Invulnerability") + " in this world.")
    );

    public static final MessageLocale INVULNERABILITY_RESTRICTED_WORLD_NOTIFY = LangEntry.builder("Essential.Invulnerability.RestrictedWorld.Notify").chatMessage(
        GRAY.wrap("Your " + ORANGE.wrap("Invulnerability") + " has been toggled " + RED.wrap("OFF") + ", because it's not allowed in this world.")
    );
}
