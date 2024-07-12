package su.nightexpress.sunlight.module.scoreboard.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedNumberFormat;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.hook.impl.ProtocolLibHook;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.utils.DynamicText;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Board {

    public static final int OBJECTIVE_ADD    = 0;
    public static final int OBJECTIVE_REMOVE = 1;
    public static final int OBJECTIVE_CHANGE = 2;

    public static final int TEAM_CREATE     = 0;
    public static final int TEAM_REMOVE     = 1;
    public static final int TEAM_UPDATE     = 2;

    private final ScoreboardModule     module;
    private final BoardConfig          boardConfig;
    private final Player               player;
    private final String               identifier;
    private final Map<Integer, String> scores;

    private boolean lock;
    private long nextUpdateTicks;

    public Board(@NotNull Player player, @NotNull ScoreboardModule module, @NotNull BoardConfig boardConfig) {
        this.module = module;
        this.boardConfig = boardConfig;
        this.player = player;
        this.identifier = SunUtils.createIdentifier(player).substring(0, 16);
        this.scores = new ConcurrentHashMap<>();
        this.nextUpdateTicks = 0L;
    }

    @NotNull
    public BoardConfig getBoardConfig() {
        return boardConfig;
    }

    @NotNull
    private String getPlayerIdentifier() {
        return this.identifier;
    }

    @NotNull
    private String getScoreIdentifier(int score) {
        if (Version.isBehind(Version.V1_20_R3)) {
            return ChatColor.COLOR_CHAR + String.join(String.valueOf(ChatColor.COLOR_CHAR), String.valueOf(score).split(""));
        }
        return "line_" + score;
        //return ChatColor.COLOR_CHAR + String.join(String.valueOf(ChatColor.COLOR_CHAR), String.valueOf(score).split(""));
    }

    @NotNull
    private PacketContainer createObjectivePacket(int mode, @NotNull String displayName) {
        PacketContainer objectivePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        objectivePacket.getModifier().writeDefaults();
        objectivePacket.getStrings().write(0, this.identifier); // 'objectiveName'
        objectivePacket.getIntegers().write(0, mode); // 'method'
        objectivePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(NightMessage.asJson(displayName))); // 'displayName'
        if (Version.isAtLeast(Version.MC_1_20_6)) {
            objectivePacket.getOptionals(BukkitConverters.getWrappedNumberFormatConverter()).write(0, Optional.of(WrappedNumberFormat.blank()));
        }
        else if (Version.isAtLeast(Version.V1_20_R3)) {
            objectivePacket.getNumberFormats().write(0, WrappedNumberFormat.blank());
        }
        return objectivePacket;
    }

    @NotNull
    private PacketContainer createResetScorePacket(@NotNull String scoreId) {
        PacketContainer scorePacket;
        if (Version.isAtLeast(Version.V1_20_R3)) {
            scorePacket = new PacketContainer(PacketType.Play.Server.RESET_SCORE);
            scorePacket.getModifier().writeDefaults();
            scorePacket.getStrings().write(0, scoreId);
            scorePacket.getStrings().write(1, this.identifier);
        }
        else {
            scorePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            scorePacket.getModifier().writeDefaults();
            scorePacket.getStrings().write(0, scoreId);
            scorePacket.getStrings().write(1, this.identifier);
            scorePacket.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE);
        }
        return scorePacket;
    }

    public void create() {
        ProtocolLibHook.sendPacketServer(this.player, this.createObjectivePacket(OBJECTIVE_ADD, ""));

        PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        displayPacket.getModifier().writeDefaults();
        if (Version.isAtLeast(Version.MC_1_20_6)) {
            displayPacket.getDisplaySlots().write(0, EnumWrappers.DisplaySlot.SIDEBAR);
        }
        else if (Version.isAtLeast(Version.V1_20_R2)) {
            displayPacket.getEnumModifier(DisplaySlot.class, 0).write(0, DisplaySlot.SIDEBAR);
        }
        else {
            displayPacket.getIntegers().write(0, 1); // Position 1: Sidebar
        }
        displayPacket.getStrings().write(0, this.identifier); // Objective Name
        ProtocolLibHook.sendPacketServer(this.player, displayPacket);
    }

    public void remove() {
        ProtocolLibHook.sendPacketServer(this.player, this.createObjectivePacket(OBJECTIVE_REMOVE, ""));

        this.scores.forEach((score, text) -> {
            if (Version.isBehind(Version.V1_20_R3)) {
                PacketContainer teamPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                teamPacket.getModifier().writeDefaults();
                teamPacket.getStrings().write(0, this.getScoreIdentifier(score));
                teamPacket.getIntegers().write(0, TEAM_REMOVE);
                ProtocolLibHook.sendPacketServer(this.player, teamPacket);
            }
            else {
                ProtocolLibHook.sendPacketServer(this.player, this.createResetScorePacket(this.getScoreIdentifier(score)));
            }
        });

        this.scores.clear();
        this.lock = false;
    }

    @NotNull
    private String replacePlaceholders(@NotNull String string) {
        string = Placeholders.forPlayer(this.player).apply(string);
        for (DynamicText animation : this.module.getAnimations()) {
            string = animation.replacePlaceholders().apply(string);
        }
        if (Plugins.hasPlaceholderAPI()) {
            string = PlaceholderAPI.setPlaceholders(this.player, string);
        }
        return string;
    }

    public void updateIfReady() {
        if (--this.nextUpdateTicks <= 0L) {
            this.update();
        }
    }

    public void update() {
        // Fixes waterfall kick issue where scoreboard tries to be registered twice on login for whatever reason.
        if (this.lock) return;

        this.lock = true;
        String title = this.boardConfig.getTitle();
        List<String> lines = this.boardConfig.getLines();

        Collection<DynamicText> animations = this.module.getAnimations();
        Map<Integer, String> scores = new HashMap<>();
        int index = lines.size();

        for (String line : lines) {
            scores.put(index--, this.replacePlaceholders(line));
        }
        title = this.replacePlaceholders(title);


        ProtocolLibHook.sendPacketServer(this.player, this.createObjectivePacket(OBJECTIVE_CHANGE, title));

        scores.forEach((score, text) -> {
            String scoreId = this.getScoreIdentifier(score);

            if (Version.isBehind(Version.V1_20_R3)) {
                PacketContainer teamPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                teamPacket.getModifier().writeDefaults();
                teamPacket.getStrings().write(0, scoreId); // Team Name

                Optional<InternalStructure> optStruct = teamPacket.getOptionalStructures().read(0);
                if (optStruct.isPresent()) {
                    InternalStructure structure = optStruct.get();
                    structure.getChatComponents().write(0, WrappedChatComponent.fromText("")); // Display Name
                    structure.getChatComponents().write(1, WrappedChatComponent.fromJson(NightMessage.asJson(text))); // Prefix

                    teamPacket.getOptionalStructures().write(0, Optional.of(structure));
                }

                // there's no need to create the team again if this line already exists
                if (this.scores.containsKey(score)) {
                    teamPacket.getIntegers().write(0, TEAM_UPDATE);
                    ProtocolLibHook.sendPacketServer(this.player, teamPacket);
                    return;
                }
                teamPacket.getIntegers().write(0, TEAM_CREATE);
                teamPacket.getSpecificModifier(Collection.class).write(0, Lists.newList(scoreId)); // Entities
                ProtocolLibHook.sendPacketServer(this.player, teamPacket);
            }

            PacketContainer scorePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            scorePacket.getModifier().writeDefaults();
            scorePacket.getStrings().write(0, scoreId); // 'owner'
            scorePacket.getStrings().write(1, this.identifier); // 'objectiveName'
            scorePacket.getIntegers().write(0, score); // 'score'
            if (Version.isAtLeast(Version.MC_1_20_6)) {
                scorePacket.getOptionals(BukkitConverters.getWrappedChatComponentConverter()).write(0, Optional.of(WrappedChatComponent.fromJson(NightMessage.asJson(text))));
                scorePacket.getOptionals(BukkitConverters.getWrappedNumberFormatConverter()).write(1, Optional.of(WrappedNumberFormat.blank()));
            }
            else if (Version.isAtLeast(Version.V1_20_R3)) {
                scorePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(NightMessage.asJson(text)));
                scorePacket.getNumberFormats().write(0, WrappedNumberFormat.blank());
            }
            else {
                scorePacket.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE); // Action
            }
            ProtocolLibHook.sendPacketServer(this.player, scorePacket);
        });

        this.scores.entrySet().stream().filter(entry -> !scores.containsKey(entry.getKey())).forEach(entry -> {
            int score = entry.getKey();
            String scoreId = this.getScoreIdentifier(score);

            if (Version.isBehind(Version.V1_20_R3)) {
                PacketContainer teamPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
                teamPacket.getModifier().writeDefaults();
                teamPacket.getStrings().write(0, scoreId);
                teamPacket.getIntegers().write(0, TEAM_REMOVE);
                ProtocolLibHook.sendPacketServer(this.player, teamPacket);
            }
            ProtocolLibHook.sendPacketServer(this.player, this.createResetScorePacket(scoreId));
        });

        this.scores.clear();
        this.scores.putAll(scores);
        this.lock = false;
        this.nextUpdateTicks = this.boardConfig.getUpdateInterval();
    }
}
