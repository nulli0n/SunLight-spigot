package su.nightexpress.sunlight.module.tab.handler;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.retrooper.packetevents.adventure.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.SunLightPlugin;

import java.util.Collection;

public class PacketsTagHandler extends NametagHandler {

    public PacketsTagHandler(@NotNull SunLightPlugin plugin) {
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

        WrapperPlayServerTeams.ScoreBoardTeamInfo info = null;
        if (mode == TeamMode.CREATE) {
            info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text(teamId),
                GsonComponentSerializer.gson().deserialize(NightMessage.asJson(teamPrefix)),
                GsonComponentSerializer.gson().deserialize(NightMessage.asJson(teamSuffix)),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                WrapperPlayServerTeams.CollisionRule.ALWAYS,
                NamedTextColor.NAMES.valueOr(teamColorRaw.toLowerCase(), NamedTextColor.GRAY),
                WrapperPlayServerTeams.OptionData.NONE
            );
        }

        WrapperPlayServerTeams.TeamMode teamMode = switch (mode) {
            case REMOVE -> WrapperPlayServerTeams.TeamMode.REMOVE;
            case CREATE -> WrapperPlayServerTeams.TeamMode.CREATE;
        };

        Collection<String> entities = Lists.newList(playerOfTeam.getName());

        WrapperPlayServerTeams teams = new WrapperPlayServerTeams(teamId, teamMode, info, entities);

        for (Player playerReceiver : receivers) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(playerReceiver, teams);
        }
    }
}
