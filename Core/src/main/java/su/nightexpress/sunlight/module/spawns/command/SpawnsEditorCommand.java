package su.nightexpress.sunlight.module.spawns.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.util.SpawnsPerms;

public class SpawnsEditorCommand extends ModuleCommand<SpawnsModule> {

    public static final String NAME = "editor";

    public SpawnsEditorCommand(@NotNull SpawnsModule module) {
        super(module, new String[]{NAME}, SpawnsPerms.COMMAND_SPAWNS_EDITOR);
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_EDITOR_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.module.getEditor().open(player, 1);
    }
}
