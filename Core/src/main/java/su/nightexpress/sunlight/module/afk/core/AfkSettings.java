package su.nightexpress.sunlight.module.afk.core;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.sunlight.SLConfigTypes;
import su.nightexpress.sunlight.module.afk.ActivityType;

import java.util.*;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class AfkSettings extends AbstractConfig {

    public final ConfigProperty<Integer> wakeUpThreshold = this.addProperty(ConfigTypes.INT, "WakeUp.Threshold", 3,
        "Sets the activity threshold that a player must reach to lose the AFK mode."
    );

    public final ConfigProperty<Integer> wakeUpTimer = this.addProperty(ConfigTypes.INT, "WakeUp.Timeout", 5,
        "The duration (in seconds) a player has to meet the activity threshold to leave AFK mode.",
        "If the player does not meet the threshold within this time, the timer and activity points reset, and they remain AFK until the next cycle."
    );

    public final ConfigProperty<Map<ActivityType, Integer>> wakeUpActivityPoints = this.addProperty(
        ConfigTypes.forMap(str -> Enums.get(str, ActivityType.class), Enum::name, ConfigTypes.INT),
        "WakeUp.Activity-Points",
        Map.of(
            ActivityType.MOVEMENT, 1,
            ActivityType.CHAT, 5,
            ActivityType.COMMAND, 3,
            ActivityType.INTERACT, 1
        ),
        "Sets how much activity points produced by certain player actions."
    );

    // TODO Whitelist commands to ignore activity

    public final ConfigProperty<List<String>> wakeUpCommands = this.addProperty(ConfigTypes.STRING_LIST, "WakeUp.Commands",
        List.of(),
        "The following commands will be dispatched right before player leave AFK mode.",
        "[*] Placeholders: '%s', %s".formatted(PLAYER_NAME, Plugins.PLACEHOLDER_API)
    );

    public final ConfigProperty<Boolean> afkStatusBarEnabled = this.addProperty(ConfigTypes.BOOLEAN, "AFK.Status-Bar.Enabled",
        true,
        "Controls whether AFK status bar is enabled for AFK players."
    );

    public final ConfigProperty<String> afkStatusBarText = this.addProperty(ConfigTypes.STRING, "AFK.Status-Bar.Text",
        DARK_GRAY.wrap("[" + ORANGE.wrap("AFK") + "]") + " " + GRAY.wrap("You're being AFK for ") + ORANGE.wrap(GENERIC_TIME),
        "Set AFK status bar message for AFK players.",
        "[*] Placeholders: '%s', %s".formatted(GENERIC_TIME, Plugins.PLACEHOLDER_API)
    );

    public final ConfigProperty<Integer> idleThreshold = this.addProperty(ConfigTypes.INT, "AFK.Cooldown",
        60,
        "The cooldown period (in seconds) after a player leaves AFK mode.",
        "During this time, the player will be immune to being marked as inactive, even if they produce no activity."
    );

    public final ConfigProperty<RankTable> idleAfkTimes = this.addProperty(SLConfigTypes.RANK_TABLE, "AFK.Idle_Time",
        RankTable.builder(RankTable.Mode.RANK, 600)
            .addRankValue("vip", 900)
            .addRankValue("admin", -1)
            .build(),
        "Here you can set the inactivity time (in seconds) after which a player will be placed into AFK mode.",
        "You can set different times for different ranks or permissions (read comments).",
        "If multiple times are available, the greatest (or negative) one will be used.",
        "Use '-1' to make players immune to the auto-AFK mode."
    );

    public final ConfigProperty<RankTable> idleKickTimes = this.addProperty(SLConfigTypes.RANK_TABLE, "AFK.Kick_Time",
        RankTable.builder(RankTable.Mode.RANK, 1200)
            .addRankValue("vip", -1)
            .addRankValue("admin", -1)
            .build(),
        "Here you can set the total inactivity time (in seconds) after which a player will be kicked from the server.",
        "[*] IMPORTANT: This value sets the TOTAL time (Time before AFK + Time after AFK), not just the time starting from when the player entered AFK mode.",
        "If multiple times are available, the greatest (or negative) one will be used.",
        "Use '-1' to make players immune from being kicked due to AFK."
    );

    public final ConfigProperty<List<String>> kickText = this.addProperty(ConfigTypes.STRING_LIST, "AFK.Kick_Message",
        List.of(
            SOFT_RED.wrap("You have been kicked for being AFK too long: " + SOFT_YELLOW.wrap(GENERIC_TIME)),
            "",
            GREEN.and(UNDERLINED).wrap("You can join back now.")
        ),
        "Kick message for players kicked for being AFK long enough.",
        "[*] Placeholders: '%s', %s".formatted(GENERIC_TIME, Plugins.PLACEHOLDER_API)
    );

    public final ConfigProperty<List<String>> afkCommands = this.addProperty(ConfigTypes.STRING_LIST, "AFK.Commands",
        List.of(),
        "The following commands will be dispatched right before player enter AFK mode.",
        "[*] Placeholders: '%s', %s".formatted(PLAYER_NAME, Plugins.PLACEHOLDER_API)
    );

    public int getActivityPoints(@NotNull ActivityType type) {
        return this.wakeUpActivityPoints.get().getOrDefault(type, 0);
    }
}
