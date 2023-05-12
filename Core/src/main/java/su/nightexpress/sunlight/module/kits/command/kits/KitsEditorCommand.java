package su.nightexpress.sunlight.module.kits.command.kits;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.util.KitsPerms;
import su.nightexpress.sunlight.module.kits.config.KitsLang;

import java.util.Map;

public class KitsEditorCommand extends ModuleCommand<KitsModule> {

    public KitsEditorCommand(@NotNull KitsModule module) {
        super(module, new String[]{"editor"}, KitsPerms.COMMAND_KITS_EDITOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(KitsLang.COMMAND_KITS_EDITOR_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        Player player = (Player) sender;
        this.module.getEditor().open(player, 1);
    }
}
