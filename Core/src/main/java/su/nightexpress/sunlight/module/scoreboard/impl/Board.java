package su.nightexpress.sunlight.module.scoreboard.impl;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EngineUtils;
import su.nightexpress.sunlight.hook.impl.ProtocolLibHook;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.utils.SimpleTextAnimator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Board {

    private final ScoreboardModule module;
    private final BoardConfig      boardConfig;
    private final Player            player;
    private final String               playerId;
    private final Map<Integer, String> scores;

    public Board(@NotNull Player player, @NotNull ScoreboardModule module, @NotNull BoardConfig boardConfig) {
        this.module = module;
        this.boardConfig = boardConfig;
        this.player = player;
        this.playerId = this.player.getUniqueId().toString().replace("-", "").substring(0, 15);
        this.scores = new ConcurrentHashMap<>();
    }

    @NotNull
    public BoardConfig getBoardConfig() {
        return boardConfig;
    }

    /*public int getScore(@NotNull String str) {
        return this.scores.entrySet().stream().filter(e -> e.getValue().equals(str))
            .findFirst().map(Map.Entry::getKey).orElse(0);
    }*/

    @NotNull
    private String getPlayerIdentifier() {
        return this.playerId;
    }

    @NotNull
    private String getScoreIdentifier(int score) {
        return ChatColor.COLOR_CHAR + String.join(String.valueOf(ChatColor.COLOR_CHAR), String.valueOf(score).split(""));
    }

    public void create() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getModifier().writeDefaults();
        packet.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        packet.getIntegers().write(0, 0); // Mode 0: Created Scoreboard
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(this.getPlayerIdentifier())); // Display Name
        ProtocolLibHook.sendPacketServer(this.player, packet);

        packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE);
        packet.getModifier().writeDefaults();
        packet.getIntegers().write(0, 1); // Position 1: Sidebar
        packet.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        ProtocolLibHook.sendPacketServer(this.player, packet);
    }

    public void remove() {
        PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet.getModifier().writeDefaults();
        packet.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        packet.getIntegers().write(0, 1); // Mode 1: Remove Scoreboard
        ProtocolLibHook.sendPacketServer(this.player, packet);

        this.scores.forEach((score, val) -> {
            PacketContainer packet2 = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet2.getModifier().writeDefaults();
            packet2.getStrings().write(0, this.getScoreIdentifier(score)); // Team Name
            packet2.getIntegers().write(0, 1); // Mode - remove team
            ProtocolLibHook.sendPacketServer(this.player, packet2);
        });
    }

    public void clear() {
        this.scores.forEach((score, val) -> {
            String scoreId = this.getScoreIdentifier(score);

            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Team Name
            packet.getIntegers().write(0, 1); // Mode - remove team
            ProtocolLibHook.sendPacketServer(this.player, packet);

            packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Score Name
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE); // Action
            packet.getStrings().write(1, this.getPlayerIdentifier()); // Objective Name
            ProtocolLibHook.sendPacketServer(this.player, packet);
        });
        this.scores.clear();
    }

    public void update() {
        String title = this.getBoardConfig().getTitle();
        List<String> lines = this.getBoardConfig().getLines();

        Map<Integer, String> scores = new HashMap<>();
        int index = lines.size();

        for (String line : lines) {
            for (SimpleTextAnimator animation : this.module.animationMap.values()) {
                line = animation.replace(line);
            }
            if (EngineUtils.hasPlaceholderAPI()) {
                line = PlaceholderAPI.setPlaceholders(this.player, line);
            }
            scores.put(index--, Colorizer.apply(line));
        }
        for (SimpleTextAnimator animation : this.module.animationMap.values()) {
            title = animation.replace(title);
        }

        PacketContainer packet2 = new PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE);
        packet2.getModifier().writeDefaults();
        packet2.getStrings().write(0, this.getPlayerIdentifier()); // Objective Name
        packet2.getIntegers().write(0, 2); // Mode 2: Update Display Name
        packet2.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(title)); // Display Name
        ProtocolLibHook.sendPacketServer(this.player, packet2);

        scores.forEach((score, text) -> {
            String scoreId = this.getScoreIdentifier(score);

            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Team Name

            Optional<InternalStructure> optStruct = packet.getOptionalStructures().read(0);
            if (optStruct.isPresent()) {
                InternalStructure structure = optStruct.get();
                structure.getChatComponents().write(0, WrappedChatComponent.fromText(scoreId)); // Display Name
                structure.getChatComponents().write(1, WrappedChatComponent.fromLegacyText(text/*.first*/)); // Prefix
                //struct.getChatComponents().write(2, WrappedChatComponent.fromLegacyText(splitText/*.second*/)); // Suffix

                packet.getOptionalStructures().write(0, Optional.of(structure));
            }

            // there's no need to create the team again if this line already exists
            if (this.scores.containsKey(score)) {
                packet.getIntegers().write(0, 2); // Mode - update team info
                ProtocolLibHook.sendPacketServer(this.player, packet);
                return;
            }

            packet.getIntegers().write(0, 0); // Mode - create team
            packet.getSpecificModifier(Collection.class).write(0, Collections.singletonList(scoreId)); // Entities
            ProtocolLibHook.sendPacketServer(this.player, packet);

            packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Score Name
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.CHANGE); // Action
            packet.getStrings().write(1, this.getPlayerIdentifier()); // Objective Name
            packet.getIntegers().write(0, score); // Score Value
            ProtocolLibHook.sendPacketServer(this.player, packet);
        });

        this.scores.entrySet().stream().filter(e -> !scores.containsKey(e.getKey())).forEach(e -> {
            int score = e.getKey();
            String scoreId = this.getScoreIdentifier(score);

            PacketContainer packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Team Name
            packet.getIntegers().write(0, 1); // Mode - remove team
            ProtocolLibHook.sendPacketServer(this.player, packet);

            packet = new PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE);
            packet.getModifier().writeDefaults();
            packet.getStrings().write(0, scoreId); // Score Name
            packet.getScoreboardActions().write(0, EnumWrappers.ScoreboardAction.REMOVE); // Action
            packet.getStrings().write(1, this.getPlayerIdentifier()); // Objective Name
            ProtocolLibHook.sendPacketServer(this.player, packet);
        });

        this.scores.clear();
        this.scores.putAll(scores);
    }

    /*public boolean hasScoreboard(Player player) {
        return playerBoards.containsKey(player.getUniqueId());
    }*/
}
