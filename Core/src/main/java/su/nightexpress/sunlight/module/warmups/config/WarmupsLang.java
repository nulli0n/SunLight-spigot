package su.nightexpress.sunlight.module.warmups.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class WarmupsLang implements LangContainer {

    public static final MessageLocale WARMUP_TELEPORT_NOTIFY = LangEntry.builder("Warmup.Teleport.Notify").titleMessage(
        GREEN.wrap(BOLD.wrap("Teleport Warmup")),
        GRAY.wrap("Don't move, you'll be teleported soon..."),
        Sound.BLOCK_LAVA_POP
    );

    public static final MessageLocale WARMUP_TELEPORT_CANCEL = LangEntry.builder("Warmup.Teleport.Cancel").titleMessage(
        RED.wrap(BOLD.wrap("Teleport Cancelled")),
        GRAY.wrap("Teleport cancelled due to movement."),
        Sound.ENTITY_VILLAGER_NO
    );
}
