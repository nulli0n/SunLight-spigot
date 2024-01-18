package su.nightexpress.sunlight.module.worlds.commands.main;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;

public class WorldsCommand extends GeneralModuleCommand<WorldsModule> {

    public static final String NAME = "worlds";

    public WorldsCommand(@NotNull WorldsModule worldsModule, @NotNull String[] aliases) {
        super(worldsModule, aliases, WorldsPerms.COMMAND_WORLDS);
        this.setDescription(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_DESC));

        this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
        this.addChildren(new CreateSubCommand(worldsModule));
        this.addChildren(new DeleteSubCommand(worldsModule));
        this.addChildren(new EditorSubCommand(worldsModule));
        this.addChildren(new LoadSubCommand(worldsModule));
        this.addChildren(new UnloadSubCommand(worldsModule));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
