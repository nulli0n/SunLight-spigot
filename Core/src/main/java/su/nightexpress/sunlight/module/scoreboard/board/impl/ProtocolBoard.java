package su.nightexpress.sunlight.module.scoreboard.board.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BukkitConverters;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedNumberFormat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.sunlight.module.scoreboard.board.BoardDefinition;

import java.util.Optional;

public class ProtocolBoard extends AbstractBoard<PacketContainer> {

    public static final int OBJECTIVE_ADD    = 0;
    public static final int OBJECTIVE_REMOVE = 1;
    public static final int OBJECTIVE_CHANGE = 2;

    public static final int TEAM_CREATE     = 0;
    public static final int TEAM_REMOVE     = 1;
    public static final int TEAM_UPDATE     = 2;

    public ProtocolBoard(@NotNull Player player, @NotNull PlaceholderContext placeholderContext, @NotNull BoardDefinition boardDefinition) {
        super(player, placeholderContext, boardDefinition);
    }

    @Override
    protected void sendPacket(@NotNull Player player, @NotNull PacketContainer packet) {
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
    }

    @Override
    @NotNull
    protected PacketContainer createObjectivePacket(ObjectiveMode mode, @NotNull String displayName) {
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
        objectivePacket.getOptionals(BukkitConverters.getWrappedNumberFormatConverter()).write(0, Optional.of(WrappedNumberFormat.blank()));
        return objectivePacket;
    }

    @Override
    @NotNull
    protected PacketContainer createResetScorePacket(@NotNull String scoreId) {
        PacketContainer scorePacket = new PacketContainer(PacketType.Play.Server.RESET_SCORE);
        scorePacket.getModifier().writeDefaults();
        scorePacket.getStrings().write(0, scoreId);
        scorePacket.getStrings().write(1, this.identifier);
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
        scorePacket.getOptionals(BukkitConverters.getWrappedChatComponentConverter()).write(0, Optional.of(WrappedChatComponent.fromJson(NightMessage.asJson(text))));
        scorePacket.getOptionals(BukkitConverters.getWrappedNumberFormatConverter()).write(1, Optional.of(WrappedNumberFormat.blank()));

        return scorePacket;
    }

    @Override
    @NotNull
    protected PacketContainer createDisplayPacket() {
        PacketContainer displayPacket = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        displayPacket.getModifier().writeDefaults();
        displayPacket.getDisplaySlots().write(0, EnumWrappers.DisplaySlot.SIDEBAR);
        displayPacket.getStrings().write(0, this.identifier); // Objective Name

        return displayPacket;
    }
}
