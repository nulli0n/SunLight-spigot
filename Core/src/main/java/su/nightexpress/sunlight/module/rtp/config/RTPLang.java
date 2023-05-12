package su.nightexpress.sunlight.module.rtp.config;

import org.bukkit.Sound;
import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.LangColors;

public class RTPLang implements LangColors {

    public static final LangKey COMMAND_RTP_DESC = LangKey.of("RTP.Command.RTP.Desc", "Teleport to a random place.");

    public static final LangKey TELEPORT_ERROR_ALREADY_IN = LangKey.of("RTP.Teleport.Error.AlreadyIn", RED + "You're already in RTP!");

    public static final LangKey TELEPORT_NOTIFY_DONE   = LangKey.of("RTP.Teleport.Notify.Done",
        "<! type:\"titles:20:80:20\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" +
            "\n" + GREEN + "&lSuccessful Teleport!" +
            "\n" + GRAY + "Location: " + GREEN + Placeholders.Location.X + GRAY  + ", " + GREEN + Placeholders.Location.Y + GRAY + ", " + GREEN + Placeholders.Location.Z);

    public static final LangKey TELEPORT_NOTIFY_SEARCH = LangKey.of("RTP.Teleport.Notify.Search",
        "<! type:\"titles:20:100:20\" sound:\"" + Sound.BLOCK_LAVA_POP.name() + "\" !>" +
            "\n" + LIGHT_YELLOW + "&lSearch for location..." +
            "\n" + GRAY + "Process: " + ORANGE + Placeholders.GENERIC_CURRENT + GRAY + "/" + ORANGE + Placeholders.GENERIC_MAX);

    public static final LangKey TELEPORT_NOTIFY_FAILURE = LangKey.of("RTP.Teleport.Notify.Failure",
        "<! type:\"titles:20:80:20\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED + "&lTeleport Failed" +
            "\n" + GRAY + "Unable to find a safe location.");
}
