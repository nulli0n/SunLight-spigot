package su.nightexpress.sunlight.module.tab.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.InternalStructure;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedTeamParameters;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.SunLightPlugin;

import java.util.Collection;
import java.util.Optional;

public class ProtocolTagHandler extends NametagHandler {

    public ProtocolTagHandler(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onShutdown() {

    }

    @Override
    protected void sendPacket(@NotNull NametagHandler.TeamMode mode,
                              @NotNull String teamId,
                              @NotNull String teamPrefix,
                              @NotNull String teamSuffix,
                              @NotNull String teamColorRaw,
                              @NotNull Player playerOfTeam,
                              @NotNull Collection<? extends Player> receivers) {

        PacketContainer packetTeam = new PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM);
        Collection<String> entities = Lists.newList(playerOfTeam.getName());
        ChatColor teamColor = StringUtil.getEnum(teamColorRaw, ChatColor.class).orElse(ChatColor.GRAY);

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
            ProtocolLibrary.getProtocolManager().sendServerPacket(playerReceiver, packetTeam);
        }
    }
}
