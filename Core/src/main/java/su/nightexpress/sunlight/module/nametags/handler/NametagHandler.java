package su.nightexpress.sunlight.module.nametags.handler;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.SLUtils;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.nametags.NameTagFormat;

public abstract class NametagHandler extends SimpleManager<SunLightPlugin> {

    protected NametagHandler(@NonNull SunLightPlugin plugin) {
        super(plugin);
    }

    public void sendTeamPacket(@NonNull Player playerOfTeam, @NonNull NameTagFormat tag, @NonNull PlaceholderContext placeholderContext) {
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

    protected abstract void sendPacket(@NonNull TeamMode mode, @NonNull String teamId, @NonNull String teamPrefix, @NonNull String teamSuffix, @NonNull String teamColorRaw, @NonNull Player playerOfTeam, @NonNull Collection<? extends Player> receivers);

    public enum TeamMode {

        REMOVE(1),
        CREATE(0);

        public final int index;

        TeamMode(int index) {
            this.index = index;
        }
    }
}
