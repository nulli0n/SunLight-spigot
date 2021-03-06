package su.nexmedia.sunlight.modules.menu;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.manager.AbstractLoadableItem;
import su.nexmedia.engine.api.menu.AbstractMenu;
import su.nexmedia.engine.api.menu.IMenuItem;
import su.nexmedia.engine.config.api.JYML;
import su.nexmedia.sunlight.SunLight;

import java.util.ArrayList;
import java.util.Set;

public class SunMenu extends AbstractLoadableItem<SunLight> {

    private final MenuManager menuManager;

    private final boolean     isPermissionRequired;
    private final Set<String> aliases;

    private SunMenu.GUI         gui;
    private SunMenu.OpenCommand command;

    public SunMenu(@NotNull MenuManager menuManager, @NotNull JYML cfg) {
        super(menuManager.plugin(), cfg);
        this.menuManager = menuManager;

        this.isPermissionRequired = cfg.getBoolean("Permission_Required");
        this.aliases = cfg.getStringSet("Command_Aliases");

        this.gui = new SunMenu.GUI(plugin);
        this.command = new SunMenu.OpenCommand(plugin);
        plugin.getCommandRegulator().register(this.command);
    }

    public boolean isPermissionRequired() {
        return this.isPermissionRequired;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (!this.isPermissionRequired()) return true;
        return player.hasPermission(MenuPerms.MENU + this.getId());
    }

    @NotNull
    public Set<String> getAliases() {
        return this.aliases;
    }

    @Override
    public void onSave() {

    }

    public boolean open(@NotNull Player player, boolean isForce) {
        if (!isForce) {
            if (!this.hasPermission(player)) {
                this.menuManager.getLang().Menu_Error_NoPermission.send(player);
                return false;
            }
        }
        this.gui.open(player, 1);
        return true;
    }

    public void clear() {
        if (this.gui != null) {
            this.gui.clear();
            this.gui = null;
        }
        if (this.command != null) {
            this.plugin.getNewCommandManager().unregisterCommand(this.command);
            this.command = null;
        }
        this.aliases.clear();
    }

    class GUI extends AbstractMenu<SunLight> {

        public GUI(@NotNull SunLight plugin) {
            super(plugin, getConfig(), "");

            for (String sId : cfg.getSection("Content")) {
                IMenuItem menuItem = cfg.getMenuItem("Content." + sId);
                this.addItem(menuItem);
            }
        }

        @Override
        public void onPrepare(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public void onReady(@NotNull Player player, @NotNull Inventory inventory) {

        }

        @Override
        public boolean cancelClick(@NotNull SlotType slotType, int slot) {
            return true;
        }
    }

    class OpenCommand extends GeneralCommand<SunLight> {

        OpenCommand(@NotNull SunLight plugin) {
            super(plugin, new ArrayList<>(SunMenu.this.aliases));
        }

        @Override
        public boolean isPlayerOnly() {
            return true;
        }

        @Override
        @NotNull
        public String getUsage() {
            return "";
        }

        @Override
        @NotNull
        public String getDescription() {
            return "";
        }

        @Override
        protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String @NotNull [] args) {
            Player player = (Player) sender;
            open(player, false);
        }
    }
}
