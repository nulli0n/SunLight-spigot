package su.nightexpress.sunlight.module.spawns.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.util.SpawnsPerms;

import java.util.Map;

public class SpawnsCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "spawns";

    public SpawnsCommand(@NotNull SpawnsModule module, @NotNull String[] aliases) {
        super(module.plugin(), aliases, SpawnsPerms.COMMAND_SPAWNS);
        this.addDefaultCommand(new HelpSubCommand<>(module.plugin()));
        this.addChildren(new SpawnsCreateCommand(module));
        this.addChildren(new SpawnsDeleteCommand(module));
        this.addChildren(new SpawnsEditorCommand(module));
        this.addChildren(new SpawnsTeleportCommand(module));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_DESC).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_USAGE).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {

    }
}
