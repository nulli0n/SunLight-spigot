package su.nightexpress.sunlight.module.worlds.commands.main;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.config.WorldsLang;
import su.nightexpress.sunlight.module.worlds.config.WorldsPerms;
import su.nightexpress.sunlight.module.worlds.impl.WorldConfig;

public class CreateSubCommand extends ModuleCommand<WorldsModule> {

    public static final String NAME = "create";

    public CreateSubCommand(@NotNull WorldsModule worldsModule) {
        super(worldsModule, new String[]{NAME}, WorldsPerms.COMMAND_WORLDS_CREATE);
        this.setDescription(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_CREATE_DESC));
        this.setUsage(this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_CREATE_USAGE));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        String name = result.getArg(1);
        if (!this.module.createWorldConfig(name)) {
            this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_CREATE_ERROR).send(sender);
            return;
        }

        WorldConfig worldConfig = this.module.getWorldById(name);
        if (worldConfig == null) return;

        if (sender instanceof Player player) {
            worldConfig.getEditor().open(player, 1);
        }

        this.plugin.getMessage(WorldsLang.COMMAND_WORLDS_CREATE_DONE).replace(worldConfig.replacePlaceholders()).send(sender);
    }
}
