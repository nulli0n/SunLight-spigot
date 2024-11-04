package su.nightexpress.sunlight.command.list;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.MenuType;
import su.nightexpress.sunlight.command.*;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;

import java.util.function.Function;

public class ContainerCommands {

    private static final Function<MenuType, String> NODE_TYPE = menuType -> "container_" + menuType.name().toLowerCase();

    public static void load(@NotNull SunLightPlugin plugin) {
        for (MenuType menuType : MenuType.values()) {
            String nodeId = NODE_TYPE.apply(menuType);

            CommandRegistry.addTemplate(nodeId, CommandTemplate.direct(new String[]{fineAlias(menuType)}, nodeId));
            CommandRegistry.registerDirectExecutor(nodeId, (template, config) -> builder(plugin, template, menuType));
        }

        CommandRegistry.addTemplate("container", CommandTemplate.group(new String[]{"container", "portable"},
            "Portable container commands.",
            CommandPerms.PREFIX + "container",
            templates -> {
                for (MenuType menuType : MenuType.values()) {
                    templates.add(CommandTemplate.direct(new String[]{fineAlias(menuType)}, NODE_TYPE.apply(menuType)));
                }
            }
        ));
    }

    @NotNull
    private static String fineAlias(@NotNull MenuType menuType) {
        return switch (menuType) {
            case CRAFTING -> "workbench";
            case ENCHANTMENT -> "enchanting";
            default -> menuType.name().toLowerCase();
        };
    }

    @NotNull
    private static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull MenuType menuType) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_CONTAINER_DESC.getString().replace(Placeholders.GENERIC_TYPE, Lang.MENU_TYPE.getLocalized(menuType)))
            .permission(CommandPerms.CONTAINER_TYPE.apply(menuType))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(CommandPerms.CONTAINER_TYPE_OTHERS.apply(menuType)))
            .withFlag(CommandFlags.silent().permission(CommandPerms.CONTAINER_TYPE_OTHERS.apply(menuType)))
            .executes((context, arguments) -> open(plugin, context, arguments, menuType))
            ;
    }

    private static boolean open(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull MenuType menuType) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        plugin.getSunNMS().openContainer(target, menuType);

        if (context.getSender() != target) {
            Lang.COMMAND_CONTAINER_TARGET.getMessage().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_TYPE, Lang.MENU_TYPE.getLocalized(menuType))
            );
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            Lang.COMMAND_CONTAINER_NOTIFY.getMessage().send(target, replacer -> replacer
                .replace(Placeholders.GENERIC_TYPE, Lang.MENU_TYPE.getLocalized(menuType))
            );
        }

        return true;
    }
}
