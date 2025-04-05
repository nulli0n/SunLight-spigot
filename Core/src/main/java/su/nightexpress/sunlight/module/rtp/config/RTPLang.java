package su.nightexpress.sunlight.module.rtp.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

public class RTPLang {

    public static final LangString COMMAND_RTP_DESC = LangString.of("RTP.Command.RTP.Desc", "Teleport to a random place.");

    public static final LangText ERROR_ALREADY_IN = LangText.of("RTP.Teleport.Error.AlreadyIn",
        LIGHT_RED.wrap("You're already in RTP!")
    );

    public static final LangText TELEPORT_ERROR_INVALID_RANGE = LangText.of("RTP.Teleport.Error.InvalidRange",
        LIGHT_RED.wrap("Random teleport is not supported for this world or have invalid range settings.")
    );

    public static final LangText TELEPORT_NOTIFY_DONE = LangText.of("RTP.Teleport.Notify.Done",
        OUTPUT.wrap(20, 80) + SOUND.wrap(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GREEN.wrap(BOLD.wrap("Successful Teleport!")),
        LIGHT_GRAY.wrap("Location: " + LIGHT_GREEN.wrap(LOCATION_X) + ", " + LIGHT_GREEN.wrap(LOCATION_Y) + ", " + LIGHT_GREEN.wrap(LOCATION_Z))
    );

    public static final LangText TELEPORT_NOTIFY_SEARCH = LangText.of("RTP.Teleport.Notify.Search",
        OUTPUT.wrap(20, 80) + SOUND.wrap(Sound.BLOCK_LAVA_POP),
        LIGHT_YELLOW.wrap(BOLD.wrap("Search for location...")),
        LIGHT_GRAY.wrap("Step: " + LIGHT_YELLOW.wrap(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.wrap(GENERIC_MAX))
    );

    public static final LangText TELEPORT_NOTIFY_FAILURE = LangText.of("RTP.Teleport.Notify.Failure",
        OUTPUT.wrap(20, 80) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Teleport Failed")),
        LIGHT_GRAY.wrap("Could not find a valid location.")
    );
}
