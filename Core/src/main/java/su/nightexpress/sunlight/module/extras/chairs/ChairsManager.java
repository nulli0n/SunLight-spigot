package su.nightexpress.sunlight.module.extras.chairs;

import org.bukkit.Location;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.nightcore.util.Version;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.module.extras.ExtrasModule;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;

import java.util.HashMap;
import java.util.Map;

public class ChairsManager extends AbstractManager<SunLightPlugin> {

    public static final Setting<Boolean> SETTING_CHAIRS = SettingRegistry.register(Setting.create("chairs.enabled", true, true));

    private static final String SEAT_META = "seat";

    //private final ExtrasModule           module;
    private final Map<Block, ArmorStand> chairHolders;

    public ChairsManager(@NotNull SunLightPlugin plugin, @NotNull ExtrasModule module) {
        super(plugin);
        //this.module = module;
        this.chairHolders = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadCommands();

        this.addListener(new ChairsListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            this.standUp(player, true);
        }
        this.chairHolders.values().forEach(Entity::remove);
        this.chairHolders.clear();
    }

    private void loadCommands() {
        ChairsCommands.load(this.plugin, this);
    }

    public static boolean isChairsEnabled(@NotNull SunUser user) {
        return user.getSettings().get(SETTING_CHAIRS);
    }

    @NotNull
    private ArmorStand createChairHolder(@NotNull Player player, @NotNull Block chair) {
        Vector vector;
        BlockData blockData = chair.getBlockData();
        if (blockData instanceof Directional directional) {
            BlockFace facing = directional.getFacing();
            Location blockLoc = chair.getLocation();
            Location faceLoc = chair.getRelative(facing.getOppositeFace()).getLocation();
            vector = faceLoc.clone().subtract(blockLoc.toVector()).toVector();
        }
        else {
            vector = player.getLocation().getDirection();
        }

        Location holderLoc = this.getHolderLocation(chair).setDirection(vector);
        ArmorStand armorStand = player.getWorld().spawn(holderLoc, ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setMarker(true);
        armorStand.setCollidable(false);
        armorStand.setRemoveWhenFarAway(true);
        armorStand.setPersistent(false);
        armorStand.setMetadata(SEAT_META, new FixedMetadataValue(plugin, chair.getLocation()));
        this.chairHolders.put(chair, armorStand);

        return armorStand;
    }

    @NotNull
    private Location getHolderLocation(@NotNull Block chair) {
        Location seat = LocationUtil.setCenter2D(chair.getLocation());
        double dY = 0.0D;
        double height = chair.getBoundingBox().getHeight();

        BlockData data = chair.getBlockData();
        if (data instanceof Slab slab) {
            if (slab.getType() == Slab.Type.TOP) {
                height *= 2;
            }
        }
        else if (data instanceof Stairs stairs) {
            height = 0.5D;
        }

        double normalized = -(dY - height);
        return seat.add(0, normalized, 0);
    }

    @Nullable
    private ArmorStand getChairHolder(@NotNull Player player) {
        if (!this.isSit(player)) return null;
        return (ArmorStand) player.getVehicle();
    }

    public void standUp(@NotNull Player player, boolean force) {
        this.standUp(player, null, force);
    }

    public void standUp(@NotNull Player player, @Nullable ArmorStand stand, boolean force) {
        if (stand == null) stand = this.getChairHolder(player);
        if (stand == null || !stand.hasMetadata(SEAT_META)) return;

        Location chairBlockLocation = (Location) stand.getMetadata(SEAT_META).getFirst().value();
        if (chairBlockLocation == null) return;

        Block chairBlock = chairBlockLocation.getBlock();
        if (!this.chairHolders.containsKey(chairBlock)) return;

        if (!player.isDead() && !force) {
            Location holderLocation = LocationUtil.setCenter2D(chairBlock.getRelative(BlockFace.UP).getLocation());
            //this.plugin.runTask(task -> {
                holderLocation.setDirection(player.getLocation().getDirection());
                player.teleport(holderLocation);
            //});
        }

        stand.remove();
        this.chairHolders.remove(chairBlock);
    }

    public void sitPlayer(@NotNull Player player, @NotNull Block chair) {
        if (this.isOccupied(chair)) return;

        ArmorStand seat = this.createChairHolder(player, chair);
        seat.addPassenger(player);
    }

    public boolean isSit(@NotNull Player player) {
        Entity holder = player.getVehicle();
        return holder != null && holder.hasMetadata(SEAT_META);
    }

    public boolean isOccupied(@NotNull Block chair) {
        return this.chairHolders.containsKey(chair);
    }

    public static boolean isChair(@NotNull Block block) {
        if (!block.getRelative(BlockFace.UP).isEmpty()) {
            return false;
        }

        BlockData data = block.getBlockData();
        if (data instanceof Stairs) {
            return ExtrasConfig.CHAIRS_ALLOW_STAIRS.get();
        }
        if (data instanceof Slab) {
            return ExtrasConfig.CHAIRS_ALLOW_SLABS.get();
        }
        if (Version.isAtLeast(Version.V1_19_R3) && Tag.WOOL_CARPETS.isTagged(block.getType())) {
            return ExtrasConfig.CHAIRS_ALLOW_CARPETS.get();
        }

        return ExtrasConfig.CHAIRS_CUSTOM_BLOCKS.get().contains(block.getType());
    }
}
