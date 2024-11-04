package su.nightexpress.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

public class SkullCommand {

    public static final String NODE_CUSTOM = "skull_custom";
    public static final String NODE_PLAYER = "skull_player";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_CUSTOM, (template, config) -> builderCustom(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_PLAYER, (template, config) -> builderPlayer(plugin, template, config));

        CommandRegistry.addTemplate("skull", CommandTemplate.group(new String[]{"skull"},
            "Skull commands.",
            CommandPerms.PREFIX + "skull",
            CommandTemplate.direct(new String[]{"custom"}, NODE_CUSTOM),
            CommandTemplate.direct(new String[]{"player"}, NODE_PLAYER)
        ));
        CommandRegistry.addTemplate("playerhead", CommandTemplate.direct(new String[]{"playerhead"}, NODE_PLAYER));
        CommandRegistry.addTemplate("customhead", CommandTemplate.direct(new String[]{"customhead"}, NODE_CUSTOM));
    }

    @NotNull
    public static DirectNodeBuilder builderPlayer(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_SKULL_PLAYER_DESC)
            .permission(CommandPerms.SKULL_PLAYER)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.SKULL_PLAYER_OTHERS))
            .executes((context, arguments) -> executePlayer(plugin, context, arguments))
            ;
    }

    public static boolean executePlayer(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        ItemUtil.editMeta(skull, meta -> {
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(target);
            }
        });
        Players.addItem(player, skull);

        Lang.COMMAND_SKULL_PLAYER_DONE.getMessage().replace(Placeholders.forPlayer(target)).send(context.getSender());
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderCustom(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_SKULL_CUSTOM_DESC)
            .permission(CommandPerms.SKULL_CUSTOM)
            .withArgument(ArgumentTypes.string(CommandArguments.VALUE).required())
            .executes((context, arguments) -> executeCustom(plugin, context, arguments))
            ;
    }

    public static boolean executeCustom(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        String textureURL = arguments.getStringArgument(CommandArguments.VALUE);
        ItemStack itemStack = ItemUtil.getSkinHead(textureURL);
        Players.addItem(player, itemStack);

        Lang.COMMAND_SKULL_CUSTOM_DONE.getMessage().send(context.getSender());
        return true;
    }
}
