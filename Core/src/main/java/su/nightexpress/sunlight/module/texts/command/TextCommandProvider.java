package su.nightexpress.sunlight.module.texts.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.texts.TextsLang;
import su.nightexpress.sunlight.module.texts.TextsModule;
import su.nightexpress.sunlight.module.texts.TextsPerms;
import su.nightexpress.sunlight.module.texts.text.Text;

import java.util.Optional;

public class TextCommandProvider extends AbstractCommandProvider {

    private final TextsModule module;

    public TextCommandProvider(@NotNull SunLightPlugin plugin, TextsModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("customtext", true, new String[]{"customtext", "ctext"}, builder -> builder
            .description(TextsLang.COMMAND_TEXT_DESC)
            .permission(TextsPerms.COMMAND_TEXT)
            .withArguments(textArgument(this.module))
            .executes(this::showText)
        );
    }

    @NotNull
    private static ArgumentNodeBuilder<Text> textArgument(@NotNull TextsModule module) {
        return Commands.argument(CommandArguments.NAME, (context, str) ->
                Optional.ofNullable(module.getTextById(str)).orElseThrow(() -> CommandSyntaxException.custom(TextsLang.COMMAND_SYNTAX_INVALID_TEXT))
            )
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
            .suggestions((reader, context) -> module.getCustomTexts().stream().filter(text -> text.hasPermission(context.getSender())).map(Text::getId).toList());
    }

    private boolean showText(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Text text = arguments.get(CommandArguments.NAME, Text.class);
        if (!text.hasPermission(context.getSender())) {
            this.module.sendPrefixed(CoreLang.ERROR_NO_PERMISSION, context.getSender());
            return false;
        }

        this.module.showText(context.getSender(), text);

        return true;
    }
}
