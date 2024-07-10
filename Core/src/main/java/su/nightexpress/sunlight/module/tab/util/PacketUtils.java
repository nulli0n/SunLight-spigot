package su.nightexpress.sunlight.module.tab.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedTeamParameters;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.hook.impl.ProtocolLibHook;
import su.nightexpress.sunlight.module.tab.impl.NameTagFormat;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.Collection;
import java.util.Optional;

public class PacketUtils {

    public static void sendTeamPacket(@NotNull Player playerOfTeam, @NotNull NameTagFormat tag) {
        String uuid = SunUtils.createIdentifier(playerOfTeam);
        String teamId = tag.getIndex() + tag.getId() + uuid;
        if (teamId.length() > 16) teamId = teamId.substring(0, 16);

        String teamPrefix = tag.getPrefix();
        String teamSuffix = tag.getSuffix();
        String teamColorRaw = tag.getColor();

        if (Plugins.hasPlaceholderAPI()) {
            teamPrefix = PlaceholderAPI.setPlaceholders(playerOfTeam, teamPrefix);
            teamSuffix = PlaceholderAPI.setPlaceholders(playerOfTeam, teamSuffix);
            teamColorRaw = PlaceholderAPI.setPlaceholders(playerOfTeam, teamColorRaw);
        }

        ChatColor teamColor = StringUtil.getEnum(teamColorRaw, ChatColor.class).orElse(ChatColor.GRAY);

        Collection<? extends Player> receivers = Bukkit.getServer().getOnlinePlayers();

        for (TeamMode mode : TeamMode.values()) {
            PacketContainer packetTeam = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
            Collection<String> entities = Lists.newList(playerOfTeam.getName());
            packetTeam.getStrings().write(0, teamId); // Name
            packetTeam.getIntegers().write(0, mode.index); // Mode. 1 - Remove, 0 - Create
            packetTeam.getSpecificModifier(Collection.class).write(0, entities);

            if (mode == TeamMode.CREATE) {
                if (Version.isAtLeast(Version.V1_20_R3)) {
                    WrappedTeamParameters parameters = WrappedTeamParameters.newBuilder()
                        .displayName(WrappedChatComponent.fromText(teamId))
                        .prefix(WrappedChatComponent.fromJson(NightMessage.asJson(teamPrefix)))
                        .suffix(WrappedChatComponent.fromJson(NightMessage.asJson(teamSuffix)))
                        .color(EnumWrappers.ChatFormatting.fromBukkit(teamColor))
                        .nametagVisibility(Team.OptionStatus.ALWAYS.name())
                        .collisionRule(Team.OptionStatus.ALWAYS.name())
                        .options(0)
                        .build();
                    packetTeam.getOptionalTeamParameters().write(0, Optional.of(parameters));
                }
                else {
                    Optional<InternalStructure> opt = packetTeam.getOptionalStructures().read(0);
                    if (opt.isPresent()) {
                        InternalStructure structure = opt.get();
                        structure.getChatComponents().write(0, WrappedChatComponent.fromText(teamId)); // 'displayName'
                        structure.getChatComponents().write(1, WrappedChatComponent.fromJson(NightMessage.asJson(teamPrefix))); // 'playerPrefix'
                        structure.getChatComponents().write(2, WrappedChatComponent.fromJson(NightMessage.asJson(teamSuffix))); // 'playerSuffix'
                        structure.getEnumModifier(ChatColor.class, MinecraftReflection.getMinecraftClass("EnumChatFormat")).write(0, teamColor); // 'color'
                        structure.getStrings().write(0, Team.OptionStatus.ALWAYS.name()); // 'nametagVisibility'
                        structure.getStrings().write(1, Team.OptionStatus.ALWAYS.name()); // 'collisionRule'
                        structure.getIntegers().write(0, 0); // 'options'
                        packetTeam.getOptionalStructures().write(0, Optional.of(structure));
                    }
                }
            }

            for (Player playerReceiver : receivers) {
                ProtocolLibHook.sendPacketServer(playerReceiver, packetTeam);
            }
        }
    }

    public enum TeamMode {
        REMOVE(1),
        CREATE(0);

        public final int index;

        TeamMode(int index) {
            this.index = index;
        }
    }
}
