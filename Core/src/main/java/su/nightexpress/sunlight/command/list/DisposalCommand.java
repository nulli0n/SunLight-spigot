package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.tag.Tags;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;

public class DisposalCommand {

    public static final String NAME = "disposal";

    private static String title;
    private static int size;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NAME, (template, config) -> builderRoot(plugin, template, config));
        CommandRegistry.addSimpleTemplate(NAME);
    }

    @NotNull
    public static DirectNodeBuilder builderRoot(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        title = ConfigValue.create("Settings.Disposal.Menu.Title",
            Tags.BLACK.enclose("Disposal"),
            "Sets the inventory title for disposal GUI."
        ).read(config);

        size = ConfigValue.create("Settings.Disposal.Menu.Size",
            36,
            "Sets the inventory size for disposal GUI. Must be multiply of 9 up to 54."
        ).read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_DISPOSAL_DESC)
            .permission(CommandPerms.DISPOSAL)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.DIMENSION_OTHERS))
            .withFlag(CommandFlags.silent().permission(CommandPerms.DIMENSION_OTHERS))
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        Inventory inventory = plugin.getServer().createInventory(null, size, NightMessage.asLegacy(title));
        target.openInventory(inventory);

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_DISPOSAL_NOTIFY.getMessage().send(target);
        }
        if (target != context.getSender()) {
            Lang.COMMAND_DISPOSAL_TARGET.getMessage().replace(Placeholders.forPlayer(target)).send(context.getSender());
        }
        return true;
    }
}
