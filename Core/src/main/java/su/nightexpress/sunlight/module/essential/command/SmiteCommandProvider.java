package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SOFT_YELLOW;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_NAME;

public class SmiteCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("smite");
    private static final Permission PERMISSION_OTHERS = EssentialPerms.COMMAND.permission("smite.others");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Smite.Desc").text("Smite player with lightning.");

    private static final MessageLocale COMMAND_SMITE_TARGET = LangEntry.builder("Command.Smite.Target").chatMessage(
        GRAY.wrap("You have smited " + SOFT_YELLOW.wrap(PLAYER_NAME) + "!")
    );

    private static final MessageLocale COMMAND_SMITE_NOTIFY = LangEntry.builder("Command.Smite.Notify").chatMessage(
        GRAY.wrap("You have been smited!")
    );

    private final EssentialModule module;

    public SmiteCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("smite", true, new String[]{"smite"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(Arguments.player(CommandArguments.PLAYER).permission(PERMISSION_OTHERS))
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::execute)
        );
    }

    private boolean execute(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.runForOnlinePlayer(context, arguments, this.module, target -> {
            target.getWorld().strikeLightning(target.getLocation());

            if (context.getSender() != target) {
                this.module.sendPrefixed(COMMAND_SMITE_TARGET, context.getSender(), replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
            }
            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(COMMAND_SMITE_NOTIFY, target);
            }
            return true;
        });
    }
}
