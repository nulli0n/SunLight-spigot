package su.nightexpress.sunlight.module.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.menu.command.MenuCommand;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager extends Module {

    private Map<String, SunMenu> menus;

    public MenuManager(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
    }

    @Override
    public void onLoad() {
        this.plugin.getLangManager().loadMissing(MenuLang.class);
        this.plugin.getLang().saveChanges();

        this.menus = new HashMap<>();
        this.plugin.getConfigManager().extractResources(this.getLocalPath() + "/menus/");

        for (JYML cfg : JYML.loadAll(this.getAbsolutePath() + "/menus/", true)) {
            try {
                SunMenu gui = new SunMenu(this, cfg);
                this.menus.put(gui.getId(), gui);
            } catch (Exception ex) {
                plugin.error("Could not load menu: '" + cfg.getFile().getName() + "' !");
                ex.printStackTrace();
            }
        }
        this.info("Loaded " + this.menus.size() + " GUIs!");

        this.plugin.getCommandRegulator().register(MenuCommand.NAME, (cfg1, aliases) -> new MenuCommand(this, aliases));
    }

    @Override
    public void onShutdown() {
        if (this.menus != null) {
            this.menus.values().forEach(SunMenu::clear);
            this.menus.clear();
            this.menus = null;
        }
    }

    @Nullable
    public SunMenu getMenuByCommand(@NotNull String cmd) {
        return this.menus.values().stream().filter(menu -> menu.getAliases().contains(cmd)).findFirst().orElse(null);
    }

    @NotNull
    public List<SunMenu> getMenus(@NotNull Player player) {
        return this.menus.values().stream().filter(sunMenu -> sunMenu.hasPermission(player)).toList();
    }

    @Nullable
    public SunMenu getMenuById(@NotNull String id) {
        return this.menus.get(id.toLowerCase());
    }
}
