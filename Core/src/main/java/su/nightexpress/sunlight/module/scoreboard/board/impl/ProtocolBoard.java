package su.nightexpress.sunlight.module.scoreboard.board.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedNumberFormat;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.scoreboard.board.BoardConfig;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProtocolBoard extends AbstractBoard<PacketContainer> {

    public static final int OBJECTIVE_ADD    = 0;
    public static final int OBJECTIVE_REMOVE = 1;
    public static final int OBJECTIVE_CHANGE = 2;

    public static final int TEAM_CREATE     = 0;
    public static final int TEAM_REMOVE     = 1;
    public static final int TEAM_UPDATE     = 2;

    public ProtocolBoard(@NotNull Player player, @NotNull ScoreboardModule module, @NotNull BoardConfig boardConfig) {
        super(player, module, boardConfig);
    }

    @Override
    protected void sendPacket(@NotNull Player player, @NotNull PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    protected @NotNull PacketContainer createObjectivePacket(ObjectiveMode mode, @NotNull String displayName) {
        int method = switch (mode) {
            case CREATE -> 0;
            case REMOVE -> 1;
            case UPDATE -> 2;
        };

        PacketContainer objectivePacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        objectivePacket.getModifier().writeDefaults();
        objectivePacket.getRenderTypes().write(0, EnumWrappers.RenderType.INTEGER);
        objectivePacket.getStrings().write(0, this.identifier); // 'objectiveName'
        objectivePacket.getIntegers().write(0, method); // 'method'
        objectivePacket.getChatComponents().write(0, WrappedChatComponent.fromJson(NightMessage.asJson(displayName))); // 'displayName'
        if (Version.isAtLeast(Version.MC_1_20_6)) {
            objectivePacket.getOptionals(BukkitConverters.getWrappedNumberFormatConverter()).write(0, Optional.of(WrappedNumberFormat.blank()));
        }
        else if (Version.isAtLeast(Version.V1_20_R3)) {
            objectivePacket.getNumberFormats().write(0, WrappedNumberFormat.blank());
        }
        return objectivePacket;
    }

    @Override
    @NotNull
    protected PacketContainer createResetScorePacket(@NotNull String scoreId) {
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

    @Override
    @NotNull
    protected PacketContainer createScorePacket(@NotNull String scoreId, int score, @NotNull String text) {
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

        return scorePacket;
    }

    @Override
    @NotNull
    protected PacketContainer createDisplayPacket() {
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

        return displayPacket;
    }

    @Override
    @NotNull
    protected PacketContainer createLegacyTeamRemovePacket(@NotNull String scoreId) {
        PacketContainer teamPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        teamPacket.getModifier().writeDefaults();
        teamPacket.getStrings().write(0, scoreId);
        teamPacket.getIntegers().write(0, TEAM_REMOVE);
        return teamPacket;
    }

    @Override
    @NotNull
    protected PacketContainer createLegacyTeamPacket(@NotNull String scoreId, int score, @NotNull String text, @NotNull AtomicBoolean result) {
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
            result.set(false);
            return teamPacket;
        }

        teamPacket.getIntegers().write(0, TEAM_CREATE);
        teamPacket.getSpecificModifier(Collection.class).write(0, Lists.newList(scoreId)); // Entities

        return teamPacket;
    }
}
