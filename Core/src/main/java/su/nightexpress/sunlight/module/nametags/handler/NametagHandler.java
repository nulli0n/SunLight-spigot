package su.nightexpress.sunlight.module.nametags.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLUtils;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.nametags.NameTagFormat;

import java.util.Collection;

public abstract class NametagHandler extends SimpleManager<SunLightPlugin> {

    public NametagHandler(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }
    
    public void sendTeamPacket(@NotNull Player playerOfTeam, @NotNull NameTagFormat tag, @NotNull PlaceholderContext placeholderContext) {
        String uuid = SLUtils.createIdentifier(playerOfTeam);
        String teamId = "sl_" + uuid;
        if (teamId.length() > 16) teamId = teamId.substring(0, 16);

        String teamPrefix = placeholderContext.apply(tag.getPrefix());
        String teamSuffix = placeholderContext.apply(tag.getSuffix());
        String teamColorRaw = placeholderContext.apply(tag.getColor());

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
