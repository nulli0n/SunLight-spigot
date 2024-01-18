package su.nightexpress.sunlight.module.worlds.commands.main;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;

public class EditorSubCommand extends ModuleCommand<WorldsModule> {

    public static final String NAME = "editor";

    public EditorSubCommand(@NotNull WorldsModule worldsModule) {
        super(worldsModule, new String[]{NAME}, WorldsPerms.COMMAND_WORLDS_EDITOR);
        this.setDescription(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_EDITOR_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;
        this.module.getEditor().open(player, 1);
    }
}
