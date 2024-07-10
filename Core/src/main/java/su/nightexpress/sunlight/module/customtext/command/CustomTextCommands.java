package su.nightexpress.sunlight.module.customtext.command;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.customtext.CustomText;
import su.nightexpress.sunlight.module.customtext.CustomTextModule;
import su.nightexpress.sunlight.module.customtext.config.CTextLang;
import su.nightexpress.sunlight.module.customtext.config.CTextPerms;

public class CustomTextCommands {

    public static final String NODE = "custom_text";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull CustomTextModule module) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, module, template, config));

        CommandRegistry.addTemplate("customtext", CommandTemplate.direct(new String[]{"customtext", "ctext"}, NODE));
    }

    public static void register(@NotNull SunLightPlugin plugin, @NotNull CustomText text) {
        ServerCommand command = RootCommand.direct(plugin, text.getId(), builder -> builder
            .description(text.getDescription())
            .permission(text.getPermission())
            .executes((context, arguments) -> execute(plugin, context, arguments, text))
        );
        CommandRegistry.register(plugin, command);
    }

    public static void unregister(@NotNull SunLightPlugin plugin, @NotNull CustomText text) {
        CommandRegistry.unregister(plugin, text.getId());
    }



    @NotNull
    private static ArgumentBuilder<CustomText> textArgument(@NotNull CustomTextModule module) {
        return CommandArgument.builder(CommandArguments.NAME, (str, context) -> module.getCustomText(str))
            .localized(Lang.COMMAND_ARGUMENT_NAME_NAME)
            .customFailure(CTextLang.ERROR_COMMAND_INVALID_TEXT_ARGUMENT)
            .withSamples(context -> module.getCustomTexts().stream().filter(text -> text.hasPermission(context.getSender())).map(CustomText::getId).toList())
            ;
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CustomTextModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(CTextLang.COMMAND_TEXT_DESC)
            .permission(CTextPerms.COMMAND_TEXT)
            .withArgument(textArgument(module).required())
            .executes((context, arguments) -> execute(plugin, module, context, arguments))
            ;
    }

    private static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CustomTextModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        CustomText text = arguments.getArgument(CommandArguments.NAME, CustomText.class);
        return execute(plugin, context, arguments, text);
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull CustomText text) {
        Player player = context.getExecutor();
        boolean papi = Plugins.hasPlaceholderAPI();

        text.getText().forEach(line -> {
            if (player != null && papi) {
                line = PlaceholderAPI.setPlaceholders(player, line);
            }
            Players.sendModernMessage(context.getSender(), Placeholders.forSender(context.getSender()).apply(line));
        });

        return true;
    }
}
