package su.nightexpress.sunlight.module.afk.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.afk.config.AfkPerms;
import su.nightexpress.sunlight.module.afk.config.AfkLang;

public class AfkCommand extends ToggleCommand {

    public static final String NAME = "afk";

    private final AfkModule module;

    public AfkCommand(@NotNull AfkModule module, @NotNull String[] aliases) {
        super(module.plugin(), aliases, AfkPerms.COMMAND_AFK, AfkPerms.COMMAND_AFK_OTHERS, 0);
        this.module = module;
        this.setDescription(this.plugin.getMessage(AfkLang.COMMAND_AFK_DESC));
        this.setUsage(this.plugin.getMessage(AfkLang.COMMAND_AFK_USAGE));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Mode mode = this.getMode(sender, result);
        boolean state = mode.apply(this.module.isAfk(target));
        if (state) {
            this.module.enterAfk(target);
        }
        else {
            this.module.exitAfk(target);
        }
    }
}
