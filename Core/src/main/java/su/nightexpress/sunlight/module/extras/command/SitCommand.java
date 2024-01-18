package su.nightexpress.sunlight.module.extras.command;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.module.extras.config.ExtrasLang;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;
import su.nightexpress.sunlight.module.extras.impl.chairs.ChairsManager;

public class SitCommand extends TargetCommand {

    public static final String NAME = "sit";

    private final ChairsManager manager;

    public SitCommand(@NotNull ChairsManager manager, @NotNull String[] aliases) {
        super(manager.plugin(), aliases, ExtrasPerms.COMMAND_SIT, ExtrasPerms.COMMAND_SIT_OTHERS, 0);
        this.manager = manager;
        this.setDescription(this.plugin.getMessage(ExtrasLang.COMMAND_SIT_DESC));
        this.setUsage(this.plugin.getMessage(ExtrasLang.COMMAND_SIT_USAGE));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Block block = target.getLocation().getBlock().getRelative(BlockFace.DOWN);
        if (block.isEmpty() || !block.getType().isSolid()) return;

        this.manager.sitPlayer(target, block);
    }
}
