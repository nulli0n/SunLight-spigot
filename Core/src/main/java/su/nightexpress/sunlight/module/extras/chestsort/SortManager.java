package su.nightexpress.sunlight.module.extras.chestsort;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.core.user.settings.SettingRegistry;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.core.user.settings.Setting;
import su.nightexpress.sunlight.module.extras.ExtrasModule;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SortManager extends AbstractManager<SunLightPlugin> {

    public static final Setting<Boolean> SETTING_CHEST_SORT = SettingRegistry.register(Setting.create("chest_sort", false, true));

    //private final ExtrasModule module;

    public SortManager(@NotNull SunLightPlugin plugin, @NotNull ExtrasModule module) {
        super(plugin);
        //this.module = module;
    }

    @Override
    protected void onLoad() {
        this.loadCommands();

        this.addListener(new SortListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {

    }

    private void loadCommands() {
        SortCommand.load(this.plugin, this);
    }

    public static boolean isChestSortEnabled(@NotNull SunUser user) {
        return user.getSettings().get(SETTING_CHEST_SORT);
    }

    @NotNull
    private String getItemSortedId(@NotNull ItemStack item) {
        StringBuilder rules = new StringBuilder();
        ExtrasConfig.CHEST_SORT_RULES.get().forEach(rule -> {
            rules.append(rule.getRule(item));
        });
        return rules.toString();
    }

    public void sortInventory(@NotNull Inventory inventory) {
        List<ItemStack> sorted = Arrays.stream(inventory.getContents())
            .filter(item -> item != null && !item.getType().isAir())
            .sorted(Comparator.comparing(this::getItemSortedId)).toList();

        inventory.clear();

        for (int slot = 0; slot < sorted.size(); slot++) {
            inventory.setItem(slot, sorted.get(slot));
        }
    }
}
