package su.nightexpress.sunlight.module.kits.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.item.AdaptedItem;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.item.impl.AdaptedVanillaStack;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolvable;
import su.nightexpress.nightcore.util.placeholder.PlaceholderResolver;
import su.nightexpress.sunlight.module.kits.KitsPlaceholders;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kit implements PlaceholderResolvable {

    private final Path          path;
    private final String        id;
    private final KitDefinition definition;

    private boolean dirty;

    public Kit(@NotNull Path path, @NotNull String id, @NotNull KitDefinition definition) {
        this.path = path;
        this.id = id;
        this.definition = definition;
    }

    @NotNull
    public static Kit fromFile(@NotNull Path file) {
        FileConfig config = FileConfig.load(file);
        String id = LowerCase.INTERNAL.apply(FileUtil.getNameWithoutExtension(file));

        if (config.contains("Items")) {
            ItemStack[] items = config.getItemsEncoded("Items"); // 0-35
            ItemStack[] armors = config.getItemsEncoded("Armor"); // 36-39
            ItemStack[] extas = config.getItemsEncoded("Extras"); // 40

            ItemStack[] combined = new ItemStack[41];

            System.arraycopy(items, 0, combined, 0, items.length);
            System.arraycopy(armors, 0, combined, 36, armors.length);
            System.arraycopy(extas, 0, combined, 40, extas.length);

            Map<Integer, AdaptedItem> itemMap = new HashMap<>();
            for (int slot = 0; slot < combined.length; slot++) {
                ItemStack itemStack = combined[slot];
                if (itemStack == null || itemStack.getType().isAir()) continue;

                AdaptedItem adaptedItem = AdaptedVanillaStack.of(itemStack);
                itemMap.put(slot, adaptedItem);
            }

            KitSchema.CONTENT.writeValue(config, new KitContent(itemMap));

            config.remove("Items");
            config.remove("Armor");
            config.remove("Extras");
        }

        String name = KitSchema.NAME.resolveWithDefaults(config);
        List<String> description = KitSchema.DESCRIPTION.resolveWithDefaults(config);
        boolean permissionRequired = KitSchema.PERMISSION_REQUIRED.resolveWithDefaults(config);
        int cooldown = KitSchema.COOLDOWN.resolveWithDefaults(config);
        double cost = KitSchema.COST.resolveWithDefaults(config);
        int priority = KitSchema.PRIORITY.resolveWithDefaults(config);
        NightItem icon = KitSchema.ICON.resolveWithDefaults(config);
        List<String> commands = KitSchema.COMMANDS.resolveWithDefaults(config);
        KitContent content = KitSchema.CONTENT.resolveWithDefaults(config);

        KitDefinition definition = new KitDefinition(name, description, permissionRequired, cooldown, cost, priority, icon, commands, content);

        return new Kit(file, id, definition);
    }

    public void write(@NotNull FileConfig config) {
        KitSchema.NAME.writeValue(config, this.definition.getName());
        KitSchema.DESCRIPTION.writeValue(config, this.definition.getDescription());
        KitSchema.COOLDOWN.writeValue(config, this.definition.getCooldown());
        KitSchema.PERMISSION_REQUIRED.writeValue(config, this.definition.isPermissionRequired());
        KitSchema.COST.writeValue(config, this.definition.getCost());
        KitSchema.PRIORITY.writeValue(config, this.definition.getPriority());
        KitSchema.ICON.writeValue(config, this.definition.getIcon());
        KitSchema.COMMANDS.writeValue(config, this.definition.getCommands());
        KitSchema.CONTENT.writeValue(config, this.definition.getContent());
    }

    @Override
    @NotNull
    public PlaceholderResolver placeholders() {
        return KitsPlaceholders.KIT.resolver(this);
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

    @NotNull
    public Path getPath() {
        return this.path;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public KitDefinition definition() {
        return this.definition;
    }

    @NotNull
    public String getPermission() {
        return KitsPerms.KIT.childrenNode(this.id);
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.definition.isPermissionRequired()) return true;

        return KitsPerms.KIT.hasChildAccess(player, this.id);
    }

    public boolean isCooldownExpirable() {
        return !this.isOneTimed();
    }

    public boolean isOneTimed() {
        return this.definition.getCooldown() < 0;
    }

    public boolean hasCooldown() {
        return this.definition.getCooldown() != 0;
    }

    public boolean hasCost() {
        return this.definition.getCost() > 0D;
    }
}