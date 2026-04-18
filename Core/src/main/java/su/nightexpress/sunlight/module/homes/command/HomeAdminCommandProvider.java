package su.nightexpress.sunlight.module.homes.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.homes.HomeDefaults;
import su.nightexpress.sunlight.module.homes.HomePlaceholders;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public class HomeAdminCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_CREATE = "create";
    private static final String COMMAND_DELETE = "delete";

    private final HomesModule module;
    private final UserManager userManager;

    public HomeAdminCommandProvider(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_DELETE, false, new String[]{"delplayerhome"}, builder -> builder
            .description(HomesLang.COMMAND_ADMIN_DELETE_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_DELETE_OTHERS)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.string(CommandArguments.NAME).localized(HomesLang.COMMAND_ARGUMENT_NAME_HOME).suggestions((
                                                                                                                     reader,
                                                                                                                     context) -> {
                    String playerName = context.getArguments().getString(CommandArguments.PLAYER);
                    UUID playerId = this.userManager.getRepository().getAssociatedId(playerName);
                    if (playerId == null) return Collections.emptyList();

                    return this.module.getUserRepository(playerId).getAll().stream().map(Home::getId).toList();
                })
            )
            .executes(this::deleteHome)
        );

        this.registerLiteral(COMMAND_CREATE, false, new String[]{"setplayerhome"}, builder -> builder
            .playerOnly()
            .description(HomesLang.COMMAND_ADMIN_CREATE_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_SET_OTHERS)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.string(CommandArguments.NAME).optional().localized(HomesLang.COMMAND_ARGUMENT_NAME_HOME)
                    .suggestions((reader, context) -> {
                        String playerName = context.getArguments().getString(CommandArguments.PLAYER);
                        UUID playerId = this.userManager.getRepository().getAssociatedId(playerName);
                        if (playerId == null) return Collections.emptyList();

                        return this.module.getUserRepository(playerId).getAll().stream().map(Home::getId).toList();
                    })
            )
            .executes(this::createHome)
        );

        this.registerRoot("homesadmin", true, new String[]{"homes-admin"},
            Map.of(
                COMMAND_CREATE, "create",
                COMMAND_DELETE, "delete"
            ),
            builder -> builder.description(HomesLang.COMMAND_ADMIN_ROOT_DESC).permission(
                HomesPerms.COMMAND_HOMES_ADMIN_ROOT));
    }

    private boolean createHome(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String userName = arguments.getString(CommandArguments.PLAYER);
        String homeId = arguments.getString(CommandArguments.NAME, HomeDefaults.DEFAULT_HOME_ID); // TODO Favorite

        UUID playerId = this.userManager.getRepository().getAssociatedId(userName);
        if (playerId == null) {
            context.errorBadPlayer();
            return false;
        }

        Home home = this.module.getHome(playerId, homeId);
        if (home == null) {
            this.module.createHome(homeId, new UserInfo(playerId, userName), player.getLocation());
            this.module.sendPrefixed(HomesLang.ADMIN_HOME_CREATE_FEEDBACK, player, builder -> builder
                .with(HomePlaceholders.HOME_ID, () -> homeId)
                .with(CommonPlaceholders.PLAYER_NAME, () -> userName)
            );
            return true;
        }

        home.updateLocation(player.getLocation());
        home.markDirty();

        this.module.sendPrefixed(HomesLang.ADMIN_HOME_MOVE_FEEDBACK, player, builder -> builder
            .with(HomePlaceholders.HOME_ID, () -> homeId)
            .with(CommonPlaceholders.PLAYER_NAME, () -> userName)
        );

        return true;
    }

    private boolean deleteHome(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String userName = arguments.getString(CommandArguments.PLAYER);
        String homeId = arguments.getString(CommandArguments.NAME); // TODO Favorite

        this.userManager.loadByNameAsync(userName).thenAccept(userOptional -> {
            SunUser user = userOptional.orElse(null);
            if (user == null) {
                context.errorBadPlayer();
                return;
            }

            Home home = this.module.getHome(user.getId(), homeId);
            if (home == null) {
                this.module.sendPrefixed(HomesLang.ADMIN_HOME_DELETE_ERROR_NO_HOME, context.getSender(),
                    builder -> builder
                        .with(HomePlaceholders.HOME_ID, () -> homeId)
                );
                return;
            }

            this.module.deleteHome(home);

            this.module.sendPrefixed(HomesLang.ADMIN_HOME_DELETE_FEEDBACK, context.getSender(), replacer -> replacer
                .with(home.placeholders())
                .with(CommonPlaceholders.PLAYER_NAME, user::getName)
            );
        }).whenComplete(FutureUtils::printStacktrace);

        return true;
    }
}
