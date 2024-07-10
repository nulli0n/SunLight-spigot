package su.nightexpress.sunlight.module.warps.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.cooldown.CooldownType;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.config.WarpsLang;
import su.nightexpress.sunlight.module.warps.config.WarpsPerms;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.warps.util.Placeholders;

import java.util.ArrayList;
import java.util.function.Function;

public class WarpCommands {

    public static final String NODE_CREATE         = "warp_create";
    public static final String NODE_DELETE         = "warp_delete";
    public static final String NODE_LIST           = "warp_list";
    public static final String NODE_RESET_COOLDOWN = "warp_reset_cooldown";
    public static final String NODE_SET_COOLDOWN   = "warp_set_cooldown";
    public static final String NODE_TELEPORT       = "warp_teleport";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module) {
        CommandRegistry.registerDirectExecutor(NODE_CREATE, (template, config) -> builderCreate(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_DELETE, (template, config) -> builderDelete(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_LIST, (template, config) -> builderList(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_RESET_COOLDOWN, (template, config) -> builderResetCD(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SET_COOLDOWN, (template, config) -> builderSetCD(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_TELEPORT, (template, config) -> builderTeleport(plugin, module, template, config));

        CommandRegistry.addTemplate("warps", CommandTemplate.group(new String[]{"warps"},
            "Warp commands.",
            WarpsPerms.PREFIX_COMMAND + "warps",
            CommandTemplate.direct(new String[]{"create"}, NODE_CREATE),
            CommandTemplate.direct(new String[]{"delete"}, NODE_DELETE),
            CommandTemplate.direct(new String[]{"list"}, NODE_LIST),
            CommandTemplate.direct(new String[]{"resetcooldown"}, NODE_RESET_COOLDOWN),
            CommandTemplate.direct(new String[]{"setcooldown"}, NODE_SET_COOLDOWN),
            CommandTemplate.direct(new String[]{"teleport"}, NODE_TELEPORT)
        ));

        CommandRegistry.addTemplate("setwarp", CommandTemplate.direct(new String[]{"setwarp"}, NODE_CREATE));
        CommandRegistry.addTemplate("delwarp", CommandTemplate.direct(new String[]{"delwarp"}, NODE_DELETE));
        CommandRegistry.addTemplate("warplist", CommandTemplate.direct(new String[]{"warplist"}, NODE_LIST));
        CommandRegistry.addTemplate("warp", CommandTemplate.direct(new String[]{"warp"}, NODE_TELEPORT));
    }


    @NotNull
    private static ArgumentBuilder<Warp> warpArgument(@NotNull WarpsModule module) {
        return warpArgument(module, module::getWarp);
    }

    @NotNull
    private static ArgumentBuilder<Warp> activeWarpArgument(@NotNull WarpsModule module) {
        return warpArgument(module, module::getActiveWarp);
    }

    @NotNull
    private static ArgumentBuilder<Warp> warpArgument(@NotNull WarpsModule module, @NotNull Function<String, Warp> function) {
        return CommandArgument.builder(CommandArguments.NAME, function)
            .localized(WarpsLang.COMMAND_ARGUMENT_NAME_NAME)
            .customFailure(WarpsLang.ERROR_COMMAND_INVALID_WARP_ARGUMENT)
            ;
    }



    @NotNull
    public static DirectNodeBuilder builderCreate(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(WarpsLang.COMMAND_CREATE_WARP_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_CREATE)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).localized(WarpsLang.COMMAND_ARGUMENT_NAME_NAME).required())
            .executes((context, arguments) -> createWarp(plugin, module, context, arguments))
            ;
    }

    public static boolean createWarp(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        return module.create(player, arguments.getStringArgument(CommandArguments.NAME), false);
    }



    @NotNull
    public static DirectNodeBuilder builderDelete(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WarpsLang.COMMAND_DELETE_WARP_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_DELETE)
            .withArgument(warpArgument(module).required().withSamples(context -> {
                    if (context.getPlayer() == null || context.getSender().hasPermission(WarpsPerms.COMMAND_WARPS_DELETE_OTHERS)) {
                        return module.getWarps().stream().map(Warp::getId).toList();
                    }
                    else {
                        return module.getOwnedWarps(context.getPlayer()).stream().map(Warp::getId).toList();
                    }
                })
            )
            .executes((context, arguments) -> deleteWarp(plugin, module, context, arguments))
            ;
    }

    public static boolean deleteWarp(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Warp warp = arguments.getArgument(CommandArguments.NAME, Warp.class);

        Player player = context.getExecutor();
        if (player != null) {
            if (!warp.isOwner(player) && !player.hasPermission(WarpsPerms.COMMAND_WARPS_DELETE_OTHERS)) {
                context.errorPermission();
                return false;
            }
        }

        module.delete(warp);
        context.send(WarpsLang.WARP_DELETE_DONE.getMessage().replace(warp.replacePlaceholders()));
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderList(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WarpsLang.COMMAND_WARP_LIST_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_LIST)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(WarpsPerms.COMMAND_WARPS_LIST_OTHERS))
            .executes((context, arguments) -> listWarps(plugin, module, context, arguments))
            ;
    }

    public static boolean listWarps(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        module.openServerWarps(target);

        if (context.getSender() != target) {
            context.send(WarpsLang.COMMAND_LIST_OTHERS.getMessage().replace(Placeholders.forPlayer(target)));
        }
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderResetCD(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WarpsLang.COMMAND_RESET_WARP_COOLDOWN_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_RESET_COOLDOWN)
            .withArgument(warpArgument(module).required().withSamples(context -> new ArrayList<>(module.getWarpsMap().keySet())))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> resetCooldown(plugin, module, context, arguments))
            ;
    }

    public static boolean resetCooldown(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Warp warp = arguments.getArgument(CommandArguments.NAME, Warp.class);

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            if (user.removeCooldown(warp)) {
                plugin.getUserManager().scheduleSave(user);
            }

            if (!user.getName().equalsIgnoreCase(context.getSender().getName())) {
                WarpsLang.COMMAND_WARPS_SET_COOLDOWN_DONE.getMessage()
                    .replace(warp.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(context.getSender());
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                WarpsLang.COMMAND_WARPS_SET_COOLDOWN_NOTIFY.getMessage()
                    .replace(warp.replacePlaceholders())
                    .send(target);
            }
        });
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSetCD(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WarpsLang.COMMAND_SET_WARP_COOLDOWN_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_SET_COOLDOWN)
            .withArgument(activeWarpArgument(module).required().withSamples(context -> new ArrayList<>(module.getWarpsMap().keySet())))
            .withArgument(ArgumentTypes.integer(CommandArguments.TIME).required().withSamples(context -> Lists.newList("300", "3600", "7200")))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> setCooldown(plugin, module, context, arguments))
            ;
    }

    public static boolean setCooldown(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Warp warp = arguments.getArgument(CommandArguments.NAME, Warp.class);

        int amount = arguments.getIntArgument(CommandArguments.TIME);
        if (amount == 0) return false;

        long expireDate = amount < 0 ? -1L : System.currentTimeMillis() + amount * 1000L;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            user.addCooldown(new CooldownInfo(CooldownType.WARP, warp.getId(), expireDate));
            plugin.getUserManager().scheduleSave(user);

            String time = amount < 0 ? Lang.OTHER_INFINITY.getLegacy() : TimeUtil.formatDuration(expireDate + 100L);

            if (!user.getName().equalsIgnoreCase(context.getSender().getName())) {
                WarpsLang.COMMAND_WARPS_SET_COOLDOWN_DONE.getMessage()
                    .replace(warp.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, time)
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(context.getSender());
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                WarpsLang.COMMAND_WARPS_SET_COOLDOWN_NOTIFY.getMessage()
                    .replace(Placeholders.GENERIC_AMOUNT, time)
                    .replace(warp.replacePlaceholders())
                    .send(target);
            }
        });
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderTeleport(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(WarpsLang.COMMAND_WARP_DESC)
            .permission(WarpsPerms.COMMAND_WARPS_TELEPORT)
            .withArgument(activeWarpArgument(module).required().withSamples(context -> new ArrayList<>(module.getWarpsMap().keySet())))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(WarpsPerms.COMMAND_WARPS_TELEPORT_OTHERS))
            .executes((context, arguments) -> teleport(plugin, module, context, arguments))
            ;
    }

    public static boolean teleport(@NotNull SunLightPlugin plugin, @NotNull WarpsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Warp warp = arguments.getArgument(CommandArguments.NAME, Warp.class);

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        boolean isForced = context.getSender() != target;
        if (isForced) {
            WarpsLang.COMMAND_TELEPORT_OTHERS.getMessage()
                .replace(Placeholders.forPlayer(target))
                .replace(warp.replacePlaceholders())
                .send(context.getSender());
        }

        return warp.teleport(target, isForced);
    }
}
