package su.nightexpress.sunlight.module.kits.command;

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
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

public class KitCommands {

    public static final String NODE_EDITOR         = "kit_editor";
    public static final String NODE_GET            = "kit_get";
    public static final String NODE_LIST           = "kit_list";
    public static final String NODE_RESET_COOLDOWN = "kit_reset_cooldown";
    public static final String NODE_SET_COOLDOWN   = "kit_set_cooldown";
    public static final String NODE_PREVIEW        = "kit_preview";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull KitsModule module) {
        CommandRegistry.registerDirectExecutor(NODE_EDITOR, (template, config) -> builderEditor(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_GET, (template, config) -> builderKit(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_LIST, (template, config) -> builderList(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_RESET_COOLDOWN, (template, config) -> builderResetCD(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SET_COOLDOWN, (template, config) -> builderSetCD(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_PREVIEW, (template, config) -> builderPreview(plugin, module, template, config));

        CommandRegistry.addTemplate("kits", CommandTemplate.group(new String[]{"kits"},
            "Kit commands.",
            KitsPerms.PREFIX_COMMAND + "kits",
            CommandTemplate.direct(new String[]{"editor"}, NODE_EDITOR),
            CommandTemplate.direct(new String[]{"give"}, NODE_GET),
            CommandTemplate.direct(new String[]{"list"}, NODE_LIST),
            CommandTemplate.direct(new String[]{"resetcooldown"}, NODE_RESET_COOLDOWN),
            CommandTemplate.direct(new String[]{"setcooldown"}, NODE_SET_COOLDOWN),
            CommandTemplate.direct(new String[]{"preview"}, NODE_PREVIEW)
        ));

        CommandRegistry.addTemplate("editkit", CommandTemplate.direct(new String[]{"editkit"}, NODE_EDITOR));
        CommandRegistry.addTemplate("viewkit", CommandTemplate.direct(new String[]{"viewkit"}, NODE_PREVIEW));
        CommandRegistry.addTemplate("kitlist", CommandTemplate.direct(new String[]{"kitlist"}, NODE_LIST));
        CommandRegistry.addTemplate("kit", CommandTemplate.direct(new String[]{"kit"}, NODE_GET));
    }



    @NotNull
    private static ArgumentBuilder<Kit> kitArgument(@NotNull KitsModule module) {
        return CommandArgument.builder(CommandArguments.NAME, module::getKitById)
            .required()
            .withSamples(context -> context.getPlayer() != null ? module.getKitIds(context.getPlayer()) : module.getKitIds())
            .localized(KitsLang.COMMAND_ARGUMENT_NAME_NAME)
            .customFailure(KitsLang.COMMAND_ERROR_INVALID_KIT_ARGUMENT);
    }



    @NotNull
    public static DirectNodeBuilder builderEditor(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(KitsLang.COMMAND_KITS_EDITOR_DESC)
            .permission(KitsPerms.COMMAND_EDIT_KIT)
            .executes((context, arguments) -> openEditor(plugin, module, context, arguments))
            ;
    }

    public static boolean openEditor(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        module.openEditor(player);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderKit(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(KitsLang.COMMAND_KITS_GIVE_DESC)
            .permission(KitsPerms.COMMAND_KIT)
            .withArgument(kitArgument(module))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(KitsPerms.COMMAND_KIT_OTHERS))
            .withFlag(CommandFlags.silent().permission(KitsPerms.COMMAND_KIT_OTHERS))
            .executes((context, arguments) -> getKit(plugin, module, context, arguments))
            ;
    }

    public static boolean getKit(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.getArgument(CommandArguments.NAME, Kit.class);

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        boolean force = context.getSender() != target;
        kit.give(target, force);

        if (force) {
            KitsLang.COMMAND_KIT_OTHERS.getMessage()
                .replace(kit.replacePlaceholders())
                .replace(Placeholders.forPlayer(target))
                .send(context.getSender());
        }

        if (!arguments.hasFlag(CommandFlags.SILENT)) {
            KitsLang.COMMAND_KIT_DONE.getMessage()
                .replace(kit.replacePlaceholders())
                .send(target);
        }

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderList(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(KitsLang.COMMAND_KITS_LIST_DESC)
            .permission(KitsPerms.COMMAND_KIT_LIST)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(KitsPerms.COMMAND_KIT_LIST_OTHERS))
            .executes((context, arguments) -> listKits(plugin, module, context, arguments))
            ;
    }

    public static boolean listKits(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        module.openKitsMenu(target);

        if (context.getSender() != target) {
            context.send(KitsLang.COMMAND_KIT_LIST_OTHERS.getMessage().replace(Placeholders.forPlayer(target)));
        }
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderPreview(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(KitsLang.COMMAND_KITS_PREVIEW_DESC)
            .permission(KitsPerms.COMMAND_PREVIEW_KIT)
            .withArgument(kitArgument(module))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(KitsPerms.COMMAND_PREVIEW_KIT_OTHERS))
            .executes((context, arguments) -> previewKit(plugin, module, context, arguments))
            ;
    }

    public static boolean previewKit(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.getArgument(CommandArguments.NAME, Kit.class);

        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, false);
        if (target == null) return false;

        module.openKitPreview(target, kit);

        if (context.getSender() != target) {
            context.send(KitsLang.COMMAND_PREVIEW_KIT_OTHERS.getMessage().replace(kit.replacePlaceholders()).replace(Placeholders.forPlayer(target)));
        }
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderResetCD(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(KitsLang.COMMAND_KITS_RESET_COOLDOWN_DESC)
            .permission(KitsPerms.COMMAND_RESET_KIT_COOLDOWN)
            .withArgument(kitArgument(module))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> resetCooldown(plugin, module, context, arguments))
            ;
    }

    public static boolean resetCooldown(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.getArgument(CommandArguments.NAME, Kit.class);

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            if (user.removeCooldown(kit)) {
                plugin.getUserManager().scheduleSave(user);
            }

            if (!user.getName().equalsIgnoreCase(context.getSender().getName())) {
                context.send(KitsLang.COMMAND_KITS_RESET_COOLDOWN_DONE.getMessage()
                    .replace(kit.replacePlaceholders())
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                );
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                KitsLang.COMMAND_KITS_RESET_COOLDOWN_NOTIFY.getMessage()
                    .replace(kit.replacePlaceholders())
                    .send(target);
            }
        });
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSetCD(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(KitsLang.COMMAND_KITS_SET_COOLDOWN_DESC)
            .permission(KitsPerms.COMMAND_SET_KIT_COOLDOWN)
            .withArgument(kitArgument(module))
            .withArgument(ArgumentTypes.integer(CommandArguments.TIME)
                .localized(KitsLang.COMMAND_ARGUMENT_NAME_TIME)
                .required().withSamples(context -> Lists.newList("300", "3600", "86400")))
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withFlag(CommandFlags.silent())
            .executes((context, arguments) -> setCooldown(plugin, module, context, arguments))
            ;
    }

    public static boolean setCooldown(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.getArgument(CommandArguments.NAME, Kit.class);

        int amount = arguments.getIntArgument(CommandArguments.TIME);
        if (amount == 0) return false;

        long expireDate = amount < 0 ? -1L : System.currentTimeMillis() + amount * 1000L;

        plugin.getUserManager().manageUser(arguments.getStringArgument(CommandArguments.PLAYER), user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            user.addCooldown(new CooldownInfo(CooldownType.KIT, kit.getId(), expireDate));
            plugin.getUserManager().scheduleSave(user);

            String time = amount < 0 ? Lang.OTHER_INFINITY.getLegacy() : TimeUtil.formatDuration(expireDate + 100L);

            if (!user.getName().equalsIgnoreCase(context.getSender().getName())) {
                KitsLang.COMMAND_KITS_SET_COOLDOWN_DONE.getMessage()
                    .replace(kit.replacePlaceholders())
                    .replace(Placeholders.GENERIC_AMOUNT, time)
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                    .send(context.getSender());
            }

            Player target = user.getPlayer();
            if (!arguments.hasFlag(CommandFlags.SILENT) && target != null) {
                KitsLang.COMMAND_KITS_SET_COOLDOWN_NOTIFY.getMessage()
                    .replace(su.nightexpress.sunlight.Placeholders.GENERIC_AMOUNT, time)
                    .replace(kit.replacePlaceholders())
                    .send(target);
            }
        });
        return true;
    }
}
