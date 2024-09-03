package su.nightexpress.sunlight.module.tab.handler;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.tab.impl.NameTagFormat;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.Collection;

public abstract class NametagHandler extends SimpleManager<SunLightPlugin> {

    public NametagHandler(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }
    
    public void sendTeamPacket(@NotNull Player playerOfTeam, @NotNull NameTagFormat tag) {
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

        Collection<? extends Player> receivers = Bukkit.getServer().getOnlinePlayers();

        for (TeamMode mode : TeamMode.values()) {
            this.sendPacket(mode, teamId, teamPrefix, teamSuffix, teamColorRaw, playerOfTeam, receivers);
        }
    }

    protected abstract void sendPacket(@NotNull TeamMode mode,
                                       @NotNull String teamId,
                                       @NotNull String teamPrefix,
                                       @NotNull String teamSuffix,
                                       @NotNull String teamColorRaw,
                                       @NotNull Player playerOfTeam,
                                       @NotNull Collection<? extends Player> receivers);

    public enum TeamMode {
        REMOVE(1),
        CREATE(0);

        public final int index;

        TeamMode(int index) {
            this.index = index;
        }
    }
}
