package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class AnvilCommand extends TargetCommand {

    public static final String NAME = "anvil";

    public AnvilCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_ANVIL, Perms.COMMAND_ANVIL_OTHERS, 0);
        this.setUsage(plugin.getMessage(Lang.COMMAND_ANVIL_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_ANVIL_DESC));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        plugin.getSunNMS().openAnvil(target);
        if (target != sender) {
            plugin.getMessage(Lang.COMMAND_ANVIL_TARGET).replace(Placeholders.forPlayer(target)).send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_ANVIL_NOTIFY).send(target);
        }
    }
}
