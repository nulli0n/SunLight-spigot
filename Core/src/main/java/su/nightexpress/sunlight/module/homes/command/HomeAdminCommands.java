package su.nightexpress.sunlight.module.homes.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
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
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeAdminCommands {

    public static final String NAME = "homesadmin";

    private static final String CREATE_NODE = "home_admin_create";
    private static final String DELETE_NODE = "home_admin_delete";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull HomesModule module) {
        CommandRegistry.registerDirectExecutor(CREATE_NODE, (template, config) -> builderCreate(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(DELETE_NODE, (template, config) -> builderDelete(plugin, module, template, config));

        CommandRegistry.addTemplate("homesadmin", CommandTemplate.group(new String[]{"homesadmin"},
            "Home admin commands.",
            HomesPerms.PREFIX_COMMAND + "homesadmin",
            CommandTemplate.direct(new String[]{"create"}, CREATE_NODE),
            CommandTemplate.direct(new String[]{"delete"}, DELETE_NODE)
        ));

        CommandRegistry.addTemplate("setplayerhome", CommandTemplate.direct(new String[]{"setplayerhome"}, CREATE_NODE));
        CommandRegistry.addTemplate("delplayerhome", CommandTemplate.direct(new String[]{"delplayerhome"}, DELETE_NODE));
    }

    @NotNull
    public static DirectNodeBuilder builderCreate(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(HomesLang.COMMAND_ADMIN_CREATE_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_SET_OTHERS)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).localized(HomesLang.COMMAND_ARGUMENT_NAME_HOME))
            .executes((context, arguments) -> createHome(plugin, module, context, arguments))
            ;
    }

    public static boolean createHome(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String userName = arguments.getStringArgument(CommandArguments.PLAYER);
        String homeId = arguments.getStringArgument(CommandArguments.NAME, Placeholders.DEFAULT);

        plugin.getUserManager().manageUser(userName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            UUID userId = user.getId();
            module.loadHomesIfAbsent(userId); // TODO NOT NEEDED

            //Home home = this.plugin.getData().getHome(userId, homeId);
            Home home = module.getHome(userId, homeId); // TODO CHECK IN CACHE INSTEAD
            if (home == null) {
                plugin.runTask(task -> {
                    module.createHome(homeId, new UserInfo(user), player.getLocation());
                    HomesLang.COMMAND_ADMIN_CREATE_HOME_DONE_FRESH.getMessage()
                        .replace(Placeholders.HOME_ID, homeId)
                        .replace(Placeholders.PLAYER_NAME, user.getName())
                        .send(player);
                });
                return;
            }

            home.setLocation(player.getLocation());
            module.saveHome(home);
            HomesLang.COMMAND_ADMIN_CREATE_HOME_DONE_EDITED.getMessage()
                .replace(Placeholders.HOME_ID, homeId)
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .send(player);
        });
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderDelete(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(HomesLang.COMMAND_ADMIN_DELETE_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_DELETE_OTHERS)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(ArgumentTypes.string(CommandArguments.NAME).localized(HomesLang.COMMAND_ARGUMENT_NAME_HOME).withSamples(context -> {
                List<String> homeIds = new ArrayList<>();
                homeIds.add(Placeholders.WILDCARD);
                homeIds.addAll(module.getCache().getUserHomes(context.getArgs()[context.getArgs().length - 2]).stream().sorted().toList());
                return homeIds;
            }))
            .executes((context, arguments) -> deleteHome(plugin, module, context, arguments))
            ;
    }

    public static boolean deleteHome(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String userName = arguments.getStringArgument(CommandArguments.PLAYER);
        String homeId = arguments.getStringArgument(CommandArguments.NAME, Placeholders.DEFAULT);

        // TODO Also ID cache
        plugin.getUserManager().manageUser(userName, user -> {
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            UUID userId = user.getId();
            if (homeId.equalsIgnoreCase(Placeholders.WILDCARD)) { // TODO Command deleteall instead
                module.getCache().getUserHomes(user.getName()).stream().toList().forEach(homeId2 -> {
                    module.getCache().remove(user.getName(), homeId2);
                });
                module.unloadHomes(userId);
                plugin.getData().deleteHomes(userId);
                context.send(HomesLang.COMMAND_ADMIN_DELETE_HOME_DONE_ALL.getMessage()
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                );
            }
            else {
                if (!module.getCache().getUserHomes(user.getName()).contains(homeId)) {
                    context.send(HomesLang.COMMAND_ERROR_INVALID_HOME_ARGUMENT.getMessage().replace(Placeholders.GENERIC_VALUE, homeId));
                    return;
                }

                module.deleteHome(new UserInfo(user), homeId);
                context.send(HomesLang.COMMAND_ADMIN_DELETE_HOME_DONE_SINGLE.getMessage()
                    .replace(Placeholders.HOME_ID, homeId)
                    .replace(Placeholders.PLAYER_NAME, user.getName())
                );
            }
        });
        return true;
    }
}
