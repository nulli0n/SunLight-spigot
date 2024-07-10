package su.nightexpress.sunlight.command.list;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.ChainedNode;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.Module;

public class ReloadCommand {

    // TODO Better fields + locale

    public static void inject(@NotNull SunLightPlugin plugin, @NotNull ChainedNode node) {
        node.addChildren(DirectNode.builder(plugin, "reload")
            .permission(CommandPerms.RELOAD)
            .description("Reload the plugin or module.")
            .withArgument(ArgumentTypes.string("module").withSamples(tabContext -> plugin.getModuleManager().getModules().stream().map(Module::getId).toList()))
            .executes((context, arguments) -> execute(plugin, context, arguments))
        );
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        if (arguments.hasArgument("module")) {
            String moduleId = arguments.getStringArgument("module");
            Module module = plugin.getModuleManager().getModule(moduleId);
            if (module != null) {
                plugin.getLang().reload();
                module.getConfig().reload();
                module.reload();
                context.send(module.getName() + " reloaded.");
            }
        }
        else {
            plugin.reload();
            context.send(Lang.COMMAND_RELOAD_DONE.getMessage(plugin));
        }

        return true;
    }
}
