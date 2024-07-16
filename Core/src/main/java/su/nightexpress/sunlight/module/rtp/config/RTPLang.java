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
        LIGHT_RED.enclose("You're already in RTP!")
    );

    public static final LangText TELEPORT_ERROR_INVALID_RANGE = LangText.of("RTP.Teleport.Error.InvalidRange",
        LIGHT_RED.enclose("Random teleport is not supported for this world or have invalid range settings.")
    );

    public static final LangText TELEPORT_NOTIFY_DONE = LangText.of("RTP.Teleport.Notify.Done",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_GREEN.enclose(BOLD.enclose("Successful Teleport!")),
        LIGHT_GRAY.enclose("Location: " + LIGHT_GREEN.enclose(LOCATION_X) + ", " + LIGHT_GREEN.enclose(LOCATION_Y) + ", " + LIGHT_GREEN.enclose(LOCATION_Z))
    );

    public static final LangText TELEPORT_NOTIFY_SEARCH = LangText.of("RTP.Teleport.Notify.Search",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.BLOCK_LAVA_POP),
        LIGHT_YELLOW.enclose(BOLD.enclose("Search for location...")),
        LIGHT_GRAY.enclose("Step: " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + "/" + LIGHT_YELLOW.enclose(GENERIC_MAX))
    );

    public static final LangText TELEPORT_NOTIFY_FAILURE = LangText.of("RTP.Teleport.Notify.Failure",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Teleport Failed")),
        LIGHT_GRAY.enclose("Could not find a valid location.")
    );
}
