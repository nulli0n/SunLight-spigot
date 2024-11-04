package su.nightexpress.sunlight.module.homes.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.homes.util.Placeholders;

import java.util.HashSet;
import java.util.Set;

public class HomeCommands {

    private static final String DELETE_NODE   = "home_delete";
    private static final String LIST_NODE     = "home_list";
    private static final String SET_NODE      = "home_set";
    private static final String TELEPORT_NODE = "home_teleport";
    private static final String VISIT_NODE    = "home_visit";
    private static final String INVITE_NODE   = "home_invite";

    public static final String DEF_LIST_ALIAS     = "homelist";
    public static final String DEF_TELEPORT_ALIAS = "home";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        CommandRegistry.registerDirectExecutor(DELETE_NODE, (template, config) -> builderDelete(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(LIST_NODE, (template, config) -> builderList(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(SET_NODE, (template, config) -> builderSet(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(TELEPORT_NODE, (template, config) -> builderTeleport(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(VISIT_NODE, (template, config) -> builderVisit(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(INVITE_NODE, (template, config) -> builderInvite(plugin, module, template, config));

        CommandRegistry.addTemplate("homes", CommandTemplate.group(new String[]{"homes"},
            "Homes commands.",
            HomesPerms.PREFIX_COMMAND + "homes",
            CommandTemplate.direct(new String[]{"delete"}, DELETE_NODE),
            CommandTemplate.direct(new String[]{"list"}, LIST_NODE),
            CommandTemplate.direct(new String[]{"set"}, SET_NODE),
            CommandTemplate.direct(new String[]{"teleport"}, TELEPORT_NODE),
            CommandTemplate.direct(new String[]{"visit"}, VISIT_NODE),
            CommandTemplate.direct(new String[]{"invite"}, INVITE_NODE)
        ));

        CommandRegistry.addTemplate("delhome", CommandTemplate.direct(new String[]{"delhome"}, DELETE_NODE));
        CommandRegistry.addTemplate(DEF_LIST_ALIAS, CommandTemplate.direct(new String[]{DEF_LIST_ALIAS}, LIST_NODE));
        CommandRegistry.addTemplate("sethome", CommandTemplate.direct(new String[]{"sethome"}, SET_NODE));
        CommandRegistry.addTemplate(DEF_TELEPORT_ALIAS, CommandTemplate.direct(new String[]{DEF_TELEPORT_ALIAS}, TELEPORT_NODE));
        CommandRegistry.addTemplate("visithome", CommandTemplate.direct(new String[]{"visithome"}, VISIT_NODE));
        CommandRegistry.addTemplate("homeinvite", CommandTemplate.direct(new String[]{"homeinvite"}, INVITE_NODE));
    }



    @NotNull
    private static ArgumentBuilder<Home> homeArgument(@NotNull HomesModule module) {
        return CommandArgument.builder(CommandArguments.NAME, (str, context) -> module.getHome(context.getPlayerOrThrow(), str))
            .localized(HomesLang.COMMAND_ARGUMENT_NAME_HOME)
            .customFailure(HomesLang.COMMAND_ERROR_INVALID_HOME_ARGUMENT)
            .withSamples(context -> module.getHomes(context.getPlayerOrThrow()).values().stream().map(Home::getId).toList())
            ;
    }

    @Nullable
    private static Home getHomeOrDefault(@NotNull HomesModule module, @NotNull Player player, @NotNull ParsedArguments arguments, @NotNull CommandContext context) {
        Home home;
        if (arguments.hasArgument(CommandArguments.NAME)) {
            home = arguments.getArgument(CommandArguments.NAME, Home.class);
        }
        else {
            home = module.getDefaultHome(player);
            if (home == null) {
                context.send(HomesLang.ERROR_NO_HOMES.getMessage());
                return null;
            }
        }
        return home;
    }



    @NotNull
    public static DirectNodeBuilder builderDelete(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(HomesLang.COMMAND_DELETE_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_DELETE)
            .withArgument(homeArgument(module))
            .executes((context, arguments) -> deleteHome(plugin, module, context, arguments))
            ;
    }

    public static boolean deleteHome(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Home home = getHomeOrDefault(module, player, arguments, context);
        if (home == null) return false;

        module.removeHome(player, home);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderList(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(HomesLang.COMMAND_HOME_LIST_DESC)
            .permission(HomesPerms.COMMAND_HOMES_LIST)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).permission(HomesPerms.COMMAND_HOMES_LIST_OTHERS))
            .executes((context, arguments) -> listHomes(plugin, module, context, arguments))
            ;
    }

    public static boolean listHomes(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String userName = arguments.getStringArgument(CommandArguments.PLAYER, player.getName());

        plugin.getUserManager().manageUser(userName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            module.loadHomesIfAbsent(user.getId());
            plugin.runTask(task -> module.openHomes(player, user.getId()));
        });
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSet(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(HomesLang.COMMAND_SET_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_SET)
            .withArgument(ArgumentTypes.string(CommandArguments.NAME)
                .localized(HomesLang.COMMAND_ARGUMENT_NAME_NAME)
                .withSamples(context -> module.getHomes(context.getPlayerOrThrow()).values().stream().map(Home::getId).toList())
            )
            .executes((context, arguments) -> setHome(plugin, module, context, arguments))
            ;
    }

    public static boolean setHome(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String homeId = arguments.getStringArgument(CommandArguments.NAME, Placeholders.DEFAULT);
        module.setHome(player, homeId, false);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderTeleport(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(HomesLang.COMMAND_TELEPORT_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_TELEPORT)
            .withArgument(homeArgument(module))
            .executes((context, arguments) -> toHome(plugin, module, context, arguments))
            ;
    }

    public static boolean toHome(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Home home = getHomeOrDefault(module, player, arguments, context);
        if (home == null) return false;

        home.teleport(player);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderVisit(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(HomesLang.COMMAND_VISIT_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_VISIT)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required().withSamples(context -> {
                Player player = context.getPlayerOrThrow();
                Set<String> invitesAndPublics = new HashSet<>();
                if (player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL)) {
                    invitesAndPublics.addAll(module.getCache().getUserHomes().keySet());
                }
                else {
                    invitesAndPublics.addAll(module.getCache().getPublicHomes().keySet());
                    invitesAndPublics.addAll(module.getCache().getInviteOwnersHomes(player.getName()));
                }
                invitesAndPublics.remove(player.getName());
                return invitesAndPublics.stream().sorted().toList();
            }))
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).localized(HomesLang.COMMAND_ARGUMENT_NAME_HOME).withSamples(context -> {
                Player player = context.getPlayerOrThrow();
                String ownerName = context.getArgs()[context.getArgs().length - 2];
                Set<String> invitesAndPublics = new HashSet<>();
                if (player.hasPermission(HomesPerms.COMMAND_HOMES_VISIT_ALL)) {
                    invitesAndPublics.addAll(module.getCache().getUserHomes(ownerName));
                }
                else {
                    invitesAndPublics.addAll(module.getCache().getPublicHomes(ownerName));
                    invitesAndPublics.addAll(module.getCache().getInvitesHomes(ownerName, player.getName()));
                }
                return invitesAndPublics.stream().sorted().toList();
            }))
            .executes((context, arguments) -> visitHome(plugin, module, context, arguments))
            ;
    }

    public static boolean visitHome(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String ownerName = arguments.getStringArgument(CommandArguments.PLAYER);
        String homeName = arguments.getStringArgument(CommandArguments.NAME, Placeholders.DEFAULT);

        plugin.getUserManager().manageUser(ownerName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            module.loadHomesIfAbsent(user.getId());

            Home home = module.getHome(user.getId(), homeName);
            if (home == null) {
                context.send(HomesLang.COMMAND_ERROR_INVALID_HOME_ARGUMENT.getMessage().replace(Placeholders.GENERIC_VALUE, homeName));
                return;
            }

            if (!home.canVisit(player)) {
                HomesLang.HOME_VISIT_ERROR_NOT_PERMITTED.getMessage()
                    .replace(home.replacePlaceholders())
                    .send(context.getSender());
                return;
            }

            // Async teleport is illegal.
            plugin.runTask(task -> home.teleport(player));
        });
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderInvite(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(HomesLang.COMMAND_HOME_INVITE_DESC)
            .permission(HomesPerms.COMMAND_HOMES_INVITE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(homeArgument(module).required())
            .executes((context, arguments) -> inviteHome(plugin, module, context, arguments))
            ;
    }

    public static boolean inviteHome(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Home home = getHomeOrDefault(module, player, arguments, context);
        if (home == null) return false;

        String userName = arguments.getStringArgument(CommandArguments.PLAYER);

        module.addHomeInvite(player, home, userName);
        return true;
    }
}
