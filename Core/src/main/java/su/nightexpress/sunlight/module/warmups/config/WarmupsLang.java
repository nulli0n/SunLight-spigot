package su.nightexpress.sunlight.module.warmups.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangText;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.nightcore.util.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

public class WarmupsLang extends CoreLang {

    public static final LangText WARMUP_TELEPORT_NOTIFY = LangText.of("Warmup.Teleport.Notify",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_LAVA_POP),
        LIGHT_GREEN.enclose(BOLD.enclose("Teleport Warmup")),
        LIGHT_GRAY.enclose("Don't move, you'll be teleported soon...")
    );

    public static final LangText WARMUP_TELEPORT_CANCEL = LangText.of("Warmup.Teleport.Cancel",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Teleport Cancelled")),
        LIGHT_GRAY.enclose("Teleport cancelled due to movement.")
    );
}
