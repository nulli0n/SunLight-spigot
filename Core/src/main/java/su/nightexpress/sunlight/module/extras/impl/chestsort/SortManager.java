package su.nightexpress.sunlight.module.extras.impl.chestsort;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.module.extras.ExtrasModule;
import su.nightexpress.sunlight.module.extras.command.ChestSortCommand;
import su.nightexpress.sunlight.module.extras.config.ExtrasConfig;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SortManager extends AbstractManager<SunLight> {

    public static final UserSetting<Boolean> SETTING_CHEST_SORT = UserSetting.asBoolean("chest_sort", false, true);

    //private final ExtrasModule module;

    public SortManager(@NotNull ExtrasModule module) {
        super(module.plugin());
        //this.module = module;
    }

    @Override
    protected void onLoad() {
        this.addListener(new SortListener(this));
        this.plugin.getCommandRegulator().register(ChestSortCommand.NAME, (cfg1, aliases) -> new ChestSortCommand(this, aliases));
    }

    @Override
    protected void onShutdown() {

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
