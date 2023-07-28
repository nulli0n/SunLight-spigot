package su.nightexpress.sunlight.module.kits.command.kits.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;

public class EditorSubCommand extends ModuleCommand<KitsModule> {

    public EditorSubCommand(@NotNull KitsModule module) {
        super(module, new String[]{"editor"}, KitsPerms.COMMAND_KITS_EDITOR);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_EDITOR_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.module.getEditor().open(player, 1);
    }
}
