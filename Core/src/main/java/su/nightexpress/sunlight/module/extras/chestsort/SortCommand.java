package su.nightexpress.sunlight.module.extras.chestsort;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.mode.ToggleMode;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.extras.config.ExtrasLang;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;
import su.nightexpress.sunlight.user.SunUser;

import java.util.Map;

public class SortCommand extends AbstractCommandProvider {

    private static final String COMMAND_OFF    = "off";
    private static final String COMMAND_ON     = "on";
    private static final String COMMAND_TOGGLE = "toggle";

    private final SortManager manager;

    public SortCommand(@NotNull SunLightPlugin plugin, @NotNull SortManager manager) {
        super(plugin);
        this.manager = manager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("chestsort", true, new String[]{"chestsort"}, builder -> builder
            .description(ExtrasLang.COMMAND_CHEST_SORT_DESC)
            .permission(ExtrasPerms.COMMAND_CHEST_SORT)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(ExtrasPerms.COMMAND_CHEST_SORT_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleSorting(context, arguments, ToggleMode.TOGGLE))
        );

        this.registerRoot("mode", true, new String[]{"sortmode"},
            Map.of(
                COMMAND_OFF, "off",
                COMMAND_ON, "on",
                COMMAND_TOGGLE, "toggle"
            ),
            builder -> builder.description("TODO").permission("TODO") // TODO
        );
    }

    private boolean toggleSorting(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull ToggleMode mode) {
        // TODO
        /*Player target = this.getTargetOrSender(context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        SunUser user = plugin.getUserManager().getOrFetch(target);
        boolean state = mode.apply(SortManager.isChestSortEnabled(user));

        user.setProperty(SortManager.SETTING_CHEST_SORT, state);
        //this.plugin.getUserManager().save(user);
        user.markDirty();

        if (context.getSender() != target) {
            context.send(ExtrasLang.COMMAND_CHEST_SORT_TARGET, replacer -> replacer
                .replace(SLPlaceholders.forPlayer(target))
                .replace(SLPlaceholders.GENERIC_STATE, CoreLang.getEnabledOrDisabled(state))
            );
        }

        if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
            ExtrasLang.COMMAND_CHEST_SORT_NOTIFY.message().send(target, replacer -> replacer
                .replace(SLPlaceholders.GENERIC_STATE, CoreLang.getEnabledOrDisabled(state))
            );
        }*/

        return true;
    }
}
