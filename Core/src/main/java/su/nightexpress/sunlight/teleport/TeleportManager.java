package su.nightexpress.sunlight.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.event.SunlightPlayerTeleportEvent;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.nms.SunNMS;

public class TeleportManager extends SimpleManager<SunLightPlugin> {

    private final SunNMS internals;

    public TeleportManager(@NonNull SunLightPlugin plugin, @Nullable SunNMS internals) {
        super(plugin);
        this.internals = internals;
    }

    @Override
    protected void onLoad() {

    }

    @Override
    protected void onShutdown() {

    }

    public boolean teleport(@NonNull TeleportContext context, @NonNull TeleportType type) {
        SunlightPlayerTeleportEvent event = new SunlightPlayerTeleportEvent(context, type);
        this.plugin.getPluginManager().callEvent(event);
        if (event.isIntercepted()) return true;
        if (event.isCancelled()) return false;

        return this.move(context);
    }

    public boolean move(@NonNull TeleportContext context) {
        Location destination = this.getDestination(context);
        if (destination == null) {
            context.getModule().sendPrefixed(context.hasSender() ? Lang.TELEPORT_UNSAFE_FEEDBACK : Lang.TELEPORT_UNSAFE_NOTIFY, context.getExecutor(), builder -> builder
                .with(CommonPlaceholders.PLAYER.resolver(context.getTarget()))
            );
            return false;
        }

        context.setDestination(destination);

        return this.moveExact(context);
    }

    public boolean moveExact(@NonNull TeleportContext context) {
        Player player = context.getTarget();
        Location location = context.getDestination();

        if (player.isOnline()) {
            if (!player.teleport(location)) {
                return false;
            }
        }
        else {
            if (this.internals == null) {
                context.getModule().sendPrefixed(Lang.TELEPORT_NO_OFFLINE_HANDLER_FEEDBACK, context.getExecutor());
                return false;
            }

            this.internals.teleport(player, location);
        }

        context.runCallback();

        return true;
    }

    @Nullable
    private Location getDestination(@NonNull TeleportContext context) {
        Location destination = context.getDestination();
        if (!context.hasFlags()) return destination;

        World world = destination.getWorld();
        if (world == null) return null;

        Location location = destination.clone();

        if (context.hasFlag(TeleportFlag.LOOK_FOR_SURFACE)) {
            Block block = location.getBlock();
            BlockFace face = isSolidBlock(block) ? BlockFace.UP : BlockFace.DOWN;
            boolean needSolid = face == BlockFace.DOWN;

            while (true) {
                Block relative = block.getRelative(face);
                if (isSolidBlock(relative) == needSolid) break;

                int y = relative.getY();
                if (y < world.getMinHeight() || y > world.getMaxHeight()) return null;

                block = relative;
            }

            double delta = location.getY() - block.getY();
            if (delta > 0) {
                location.setY(location.getY() - delta);
            }
        }

        if (context.hasFlag(TeleportFlag.AVOID_LAVA)) {
            if (location.getBlock().getType() == Material.LAVA) {
                return null;
            }
        }

        if (context.hasFlag(TeleportFlag.CENTERED)) {
            location = LocationUtil.setCenter2D(location);
        }

        if (context.hasFlag(TeleportFlag.KEEP_DIRECTION)) {
            Location source = context.getTarget().getLocation();
            location.setYaw(source.getYaw());
            location.setPitch(source.getPitch());
        }

        return location;
    }

    private static boolean isSolidBlock(@NonNull Block block) {
        return !block.isEmpty() && block.getType().isSolid();
    }
}
