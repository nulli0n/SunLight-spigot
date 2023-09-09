package su.nightexpress.sunlight.module.tab.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.hook.impl.ProtocolLibHook;
import su.nightexpress.sunlight.module.tab.impl.NametagFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

public class PacketUtils {

    public static void sendTeamPacket(@NotNull Player playerOfTeam, @NotNull NametagFormat tag) {
        String teamId = tag.getIndex() + tag.getId() + playerOfTeam.getName();
        if (teamId.length() > 16) teamId = teamId.substring(0, 16);

        String teamPrefix = tag.getPrefix();
        String teamSuffix = tag.getSuffix();
        String teamColorRaw = tag.getColor();

        if (EngineUtils.hasPlaceholderAPI()) {
            teamPrefix = Colorizer.apply(PlaceholderAPI.setPlaceholders(playerOfTeam, teamPrefix));
            teamSuffix = Colorizer.apply(PlaceholderAPI.setPlaceholders(playerOfTeam, teamSuffix));
            teamColorRaw = PlaceholderAPI.setPlaceholders(playerOfTeam, teamColorRaw);
        }

        ChatColor teamColor = StringUtil.getEnum(teamColorRaw, ChatColor.class).orElse(ChatColor.GRAY);

        Collection<? extends Player> online = Bukkit.getServer().getOnlinePlayers();
        for (int mode : new int[]{1,0}) {
            PacketContainer packetTeam = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            Collection<String> entities = new ArrayList<>(Stream.of(playerOfTeam).map(HumanEntity::getName).toList());
            packetTeam.getStrings().write(0, teamId); // Name
            packetTeam.getIntegers().write(0, mode); // Mode. 1 - Remove, 0 - Create
            packetTeam.getSpecificModifier(Collection.class).write(0, entities);

            if (mode == 0) {
                Optional<InternalStructure> opt = packetTeam.getOptionalStructures().read(0);
                if (opt.isPresent()) {
                    InternalStructure structure = opt.get();
                    structure.getChatComponents().write(0, WrappedChatComponent.fromText(teamId));
                    structure.getChatComponents().write(1, WrappedChatComponent.fromLegacyText(teamPrefix));
                    structure.getChatComponents().write(2, WrappedChatComponent.fromLegacyText(teamSuffix));
                    structure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, teamColor);
                    structure.getStrings().write(0, Team.OptionStatus.ALWAYS.name());
                    structure.getStrings().write(1, Team.OptionStatus.ALWAYS.name());
                    structure.getIntegers().write(0, 0);
                    packetTeam.getOptionalStructures().write(0, Optional.of(structure));
                }
            }

            for (Player playerReceiver : online) {
                ProtocolLibHook.sendPacketServer(playerReceiver, packetTeam);
            }
        }
    }
}
