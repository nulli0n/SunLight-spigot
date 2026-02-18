package su.nightexpress.sunlight.module.rtp.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class RTPLang implements LangContainer {

    public static final TextLocale COMMAND_RTP_DESC = LangEntry.builder("RTP.Command.RTP.Desc").text("Teleport to a random place.");

    public static final MessageLocale TELEPORT_ERROR_INVALID_RANGE = LangEntry.builder("RTP.Teleport.Error.InvalidRange").chatMessage(
        GRAY.wrap("Random teleport is not supported for this world or have invalid range settings.")
    );

    public static final MessageLocale RANDOM_LOCATION_TELEPORT_SUCCESS = LangEntry.builder("RTP.Teleport.Notify.Done").titleMessage(
        GREEN.wrap(BOLD.wrap("Random Location")),
        GRAY.wrap("You're here: " + RED.wrap(LOCATION_X) + ", " + GREEN.wrap(LOCATION_Y) + ", " + BLUE.wrap(LOCATION_Z)),
        Sound.ENTITY_ENDERMAN_TELEPORT
    );

    public static final MessageLocale RANDOM_LOCATION_TELEPORT_FAILURE = LangEntry.builder("RTP.Teleport.Notify.Failure").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("Could not find a valid location. Please try again.")
    );
}
