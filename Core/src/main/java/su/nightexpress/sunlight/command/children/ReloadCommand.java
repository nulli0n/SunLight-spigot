package su.nightexpress.sunlight.command.children;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.command.list.ReloadSubCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;

import java.util.List;
import java.util.Map;

@Deprecated // Draft
public class ReloadCommand extends ReloadSubCommand<SunLight> {

    public ReloadCommand(@NotNull SunLight plugin) {
        super(plugin, Perms.COMMAND_RELOAD);
    }

    @Override
    public @NotNull List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return plugin.getModuleManager().getModules().stream().map(Module::getId).toList();
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length <= 1) {
            super.onExecute(sender, label, args, flags);
            return;
        }
        String moduleId = args[1];
        Module module = plugin.getModuleManager().getModule(moduleId);
        if (module != null) {
            this.plugin.getLang().reload();
            module.getConfig().reload();
            module.reload();
            sender.sendMessage(module.getName() + " reloaded.");
        }
        else if (moduleId.equalsIgnoreCase("config")) {
            this.plugin.getConfig().reload();
            this.plugin.getConfigManager().reload();
            this.plugin.loadConfig();
            this.plugin.getLang().reload();
            this.plugin.getLangManager().reload();
            this.plugin.loadLang();
            sender.sendMessage("Configuration reloaded.");
        }
    }
}
