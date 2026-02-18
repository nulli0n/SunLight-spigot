package su.nightexpress.sunlight.module.extras.chairs;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
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
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;

public class ChairsCommands extends AbstractCommandProvider {

    public static final String NODE_TOGGLE = "chairs_toggle";
    public static final String NODE_SIT = "chairs_sit";

    private static final String COMMAND_OFF    = "off";
    private static final String COMMAND_ON     = "on";
    private static final String COMMAND_TOGGLE = "toggle";

    private final ChairsManager manager;
    private final UserManager userManager;

    public ChairsCommands(@NonNull SunLightPlugin plugin, @NonNull ChairsManager manager, @NonNull UserManager userManager) {
        super(plugin);
        this.manager = manager;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("chairs", true, new String[]{"chairs"}, builder -> builder
            .description(ExtrasLang.COMMAND_CHAIRS_DESC)
            .permission(ExtrasPerms.COMMAND_CHAIRS)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(ExtrasPerms.COMMAND_CHAIRS_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.toggleChairs(context, arguments, ToggleMode.TOGGLE))
        );

        this.registerLiteral("sit", true, new String[]{"sit"}, builder -> builder
            .description(ExtrasLang.COMMAND_SIT_DESC)
            .permission(ExtrasPerms.COMMAND_SIT)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(ExtrasPerms.COMMAND_SIT_OTHERS))
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::sit)
        );

        this.registerRoot("mode", false, new String[]{"chairsmode"},
            Map.of(
                COMMAND_OFF, "off",
                COMMAND_ON, "on",
                COMMAND_TOGGLE, "toggle"
            ),
            builder -> builder.description("TODO").permission("TODO") // TODO
        );
    }

    private boolean toggleChairs(@NonNull CommandContext context, @NonNull ParsedArguments arguments, @NonNull ToggleMode mode) {
        // TODO
        /*return this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.manager, this.userManager, (user, target) -> {
            boolean state = mode.apply(ChairsManager.isChairsEnabled(user));

            user.setProperty(ChairsManager.SETTING_CHAIRS, state);
            //this.plugin.getUserManager().save(user);
            user.markDirty();

            if (context.getSender() != target) {
                context.send(ExtrasLang.COMMAND_CHAIRS_TARGET, replacer -> replacer
                    .replace(SLPlaceholders.forPlayer(target))
                    .replace(SLPlaceholders.GENERIC_STATE, CoreLang.getEnabledOrDisabled(state))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                ExtrasLang.COMMAND_CHAIRS_NOTIFY.message().send(target, replacer -> replacer
                    .replace(SLPlaceholders.GENERIC_STATE, CoreLang.getEnabledOrDisabled(state))
                );
            }
        });*/
        return true;
    }

    private boolean sit(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        // TODO
        /*Player target = this.getTargetOrSender(context, arguments, CommandArguments.PLAYER, true);
        if (target == null || this.manager.isSit(target)) return false;

        Block block = target.getLocation().getBlock();
        if (block.isEmpty() || !block.getType().isSolid()) block = block.getRelative(BlockFace.DOWN);
        if (block.isEmpty() || !block.getType().isSolid()) return false;

        manager.sitPlayer(target, block);*/
        return true;
    }
}
