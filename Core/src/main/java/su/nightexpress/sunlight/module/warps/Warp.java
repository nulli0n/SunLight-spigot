package su.nightexpress.sunlight.module.warps;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.configuration.ConfigTypes;
import su.nightexpress.nightcore.util.Strings;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.geodata.pos.ExactPos;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.warps.core.WarpsPerms;
import su.nightexpress.sunlight.module.warps.exception.WarpLoadException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Warp implements PlaceholderResolvable {

    private final Path   file;
    private final String id;

    private String       name;
    private List<String> description;
    private String       worldName;
    private ExactPos     blockPos;
    private NightItem    icon;

    private int     menuPage;
    private int[]   menuSlots;
    private boolean permissionRequired;

    private boolean commandEnabled;
    private String  commandLabel;

    private World   world;
    private boolean dirty;

    private NightCommand command;

    public Warp(@NonNull Path file, @NonNull String id) {
        this.file = file;
        this.id = id;
    }

    @Override
    @NotNull
    public PlaceholderResolver placeholders() {
        return WarpsPlaceholders.WARP.resolver(this);
    }

    public void load() throws WarpLoadException {
        this.loadConfig().edit(this::loadFromConfig);
    }

    public void loadFromConfig(@NonNull FileConfig config) {
        this.blockPos = ExactPos.read(config, "BlockPos");
        this.worldName = config.getString("World");

        this.setIcon(config.getCosmeticItem("Icon"));
        this.setName(config.getString("Name", this.getId()));
        this.setDescription(config.getStringList("Description"));
        this.setPermissionRequired(config.getBoolean("Permission_Required"));

        this.setMenuPage(config.get(ConfigTypes.INT, "Menu.Page", 1));
        this.setMenuSlots(config.get(ConfigTypes.INT_ARRAY, "Menu.Slots", new int[0]));

        this.setCommandEnabled(config.get(ConfigTypes.BOOLEAN, "Command.Enabled", false));
        this.setCommandLabel(config.get(ConfigTypes.STRING, "Command.Label", this.id));
    }

    public void saveIfDirty() {
        if (this.dirty) {
            this.save();
            this.markClean();
        }
    }

    public void save() {
        this.loadConfig().edit(this::writeToConfig);
    }

    private void writeToConfig(@NonNull FileConfig config) {
        config.set("World", this.worldName);
        config.set("BlockPos", this.blockPos);
        config.set("Name", this.name);
        config.set("Description", this.description);
        config.set("Icon", this.icon);
        config.set("Permission_Required", this.permissionRequired);

        config.set("Menu.Page", this.menuPage);
        config.setArray("Menu.Slots", this.menuSlots);

        config.set("Command.Enabled", this.commandEnabled);
        config.set("Command.Label", this.commandLabel);
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void markClean() {
        this.dirty = false;
    }

    public boolean isActive() {
        return this.world != null;
    }

    public boolean isInactive() {
        return !this.isActive();
    }

    public boolean isWorld(@NonNull World world) {
        return this.worldName.equalsIgnoreCase(world.getName());
    }

    public boolean hasPermission(@NonNull Player player) {
        return !this.permissionRequired || player.hasPermission(this.getPermission());
    }

    public boolean canUse(@NonNull Player player) {
        return this.hasPermission(player);
    }

    public boolean canEdit(@NonNull Player player) {
        return player.hasPermission(WarpsPerms.EDITOR);
    }

    public void activate() {
        World world = Bukkit.getWorld(this.worldName);
        if (world != null) {
            this.activate(world);
        }
    }

    public void activate(@NonNull World world) {
        if (this.worldName.equalsIgnoreCase(world.getName())) {
            this.world = world;
        }
    }

    public void deactivate() {
        this.world = null;
    }

    public void clearCommand() {
        if (this.command != null && this.command.unregister()) {
            this.command = null;
        }
    }

    @NonNull
    public World getWorld() {
        if (this.world == null) throw new IllegalStateException("Warp's world is not loaded");

        return this.world;
    }

    @NonNull
    public Location getLocation() {
        return this.blockPos.toLocation(this.getWorld());
    }

    public void setLocation(@NonNull Location location) {
        World locWorld = location.getWorld();
        if (locWorld == null) return;

        this.worldName = locWorld.getName();
        this.blockPos = ExactPos.from(location);
    }

    public void setCommand(@Nullable NightCommand command) {
        this.command = command;
    }

    @Nullable
    public NightCommand getCommand() {
        return this.command;
    }

    @NonNull
    public String getPermission() {
        return WarpsPerms.WARP.childrenNode(this.getId());
    }

    @NonNull
    public Path getFile() {
        return this.file;
    }

    @NonNull
    public FileConfig loadConfig() {
        return FileConfig.load(this.file);
    }

    @NonNull
    public String getId() {
        return this.id;
    }

    @NonNull
    public String getWorldName() {
        return this.worldName;
    }

    @NonNull
    public ExactPos getBlockPos() {
        return this.blockPos;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public List<String> getDescription() {
        return List.copyOf(this.description);
    }

    public void setDescription(@NonNull List<String> description) {
        this.description = new ArrayList<>(description);
    }

    @NonNull
    public NightItem getIcon() {
        return this.icon.copy();
    }

    public void setIcon(@NonNull NightItem icon) {
        this.icon = icon.copy();
    }

    public int getMenuPage() {
        return this.menuPage;
    }

    public void setMenuPage(int menuPage) {
        this.menuPage = menuPage;
    }

    public int[] getMenuSlots() {
        return this.menuSlots;
    }

    public void setMenuSlots(int... menuSlots) {
        this.menuSlots = menuSlots;
    }

    public boolean isPermissionRequired() {
        return this.permissionRequired;
    }

    public void setPermissionRequired(boolean isPermission) {
        this.permissionRequired = isPermission;
    }

    public boolean isCommandEnabled() {
        return this.commandEnabled;
    }

    public void setCommandEnabled(boolean commandEnabled) {
        this.commandEnabled = commandEnabled;
    }

    @NonNull
    public String getCommandLabel() {
        return this.commandLabel;
    }

    public void setCommandLabel(@NonNull String commandLabel) {
        this.commandLabel = Strings.varStyle(commandLabel).orElse(this.id);
    }
}
