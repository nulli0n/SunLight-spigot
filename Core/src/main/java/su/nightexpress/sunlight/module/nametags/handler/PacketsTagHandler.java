package su.nightexpress.sunlight.module.nametags.handler;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import io.github.retrooper.packetevents.adventure.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.paper.PaperBridge;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.nightcore.util.bridge.Software;
import su.nightexpress.nightcore.util.text.night.NightMessage;
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

        Scoreboard scoreboard = playerOfTeam.getScoreboard();
        Team team = scoreboard.getEntryTeam(playerOfTeam.getName());
        Team.OptionStatus status = team == null ? null : team.getOption(Team.Option.COLLISION_RULE);

        WrapperPlayServerTeams.CollisionRule collisionRule = status == null ? WrapperPlayServerTeams.CollisionRule.ALWAYS : switch (status) {
            case NEVER -> WrapperPlayServerTeams.CollisionRule.NEVER;
            case ALWAYS -> WrapperPlayServerTeams.CollisionRule.ALWAYS;
            case FOR_OWN_TEAM -> WrapperPlayServerTeams.CollisionRule.PUSH_OWN_TEAM;
            case FOR_OTHER_TEAMS -> WrapperPlayServerTeams.CollisionRule.PUSH_OTHER_TEAMS;
        };

        WrapperPlayServerTeams.ScoreBoardTeamInfo info = null;
        if (mode == TeamMode.CREATE) {
            info = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
                Component.text(teamId),
                adaptComponent(teamPrefix),
                adaptComponent(teamSuffix),
                WrapperPlayServerTeams.NameTagVisibility.ALWAYS,
                collisionRule,
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

    @NotNull
    private static Component adaptComponent(@NotNull String string) {
        if (Version.isPaper()) {
            return ((PaperBridge) Software.get()).getTextComponentAdapter().adaptComponent(NightMessage.parse(string));
        }
        else {
            return GsonComponentSerializer.gson().deserialize(NightMessage.asJson(string));
        }
    }
}
