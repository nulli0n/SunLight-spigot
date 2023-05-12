package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class DisposalCommand extends TargetCommand {

    public static final String NAME = "disposal";

    private final String title;
    private final int size;

    public DisposalCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_DISPOSAL, Perms.COMMAND_DISPOSAL_OTHERS, 0);
        this.setDescription(plugin.getMessage(Lang.COMMAND_DISPOSAL_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_DISPOSAL_USAGE));
        this.addFlag(CommandFlags.SILENT);

        this.title = JOption.create("Disposal.Menu.Title", "Disposal",
            "Sets the inventory title for disposal GUI.").mapReader(Colorizer::apply).read(cfg);
        this.size = JOption.create("Disposal.Menu.Size", 36,
            "Sets the inventory size for disposal GUI. Must be multiply of 9 up to 54.").read(cfg);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Inventory inventory = this.plugin.getServer().createInventory(null, this.size, this.title);
        target.openInventory(inventory);

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_DISPOSAL_NOTIFY).send(target);
        }
        if (target != sender) {
            plugin.getMessage(Lang.COMMAND_DISPOSAL_TARGET).replace(Placeholders.Player.replacer(target)).send(sender);
        }
    }
}
