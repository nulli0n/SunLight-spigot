package su.nightexpress.sunlight.module.homes.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.user.UserInfo;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.homes.HomeDefaults;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;
import su.nightexpress.sunlight.module.homes.config.HomesPerms;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class HomeCommonCommandProvider extends AbstractCommandProvider {

    private static final String ARG_HOME = "home";

    private static final String COMMAND_DELETE   = "delete";
    private static final String COMMAND_LIST     = "list";
    private static final String COMMAND_SET      = "set";
    private static final String COMMAND_TELEPORT = "teleport";
    private static final String COMMAND_VISIT    = "visit";
    private static final String COMMAND_INVITE   = "invite";

    private final HomesModule module;
    private final UserManager userManager;

    public HomeCommonCommandProvider(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_DELETE, true, new String[]{"delhome"}, builder -> builder
            .playerOnly()
            .description(HomesLang.COMMAND_DELETE_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_DELETE)
            .withArguments(this.homeArgument(false).optional())
            .executes(this::deleteHome)
        );

        this.registerLiteral(COMMAND_LIST, true, new String[]{HomeDefaults.DEFAULT_LIST_ALIAS}, builder -> builder
            .playerOnly()
            .description(HomesLang.COMMAND_HOME_LIST_DESC)
            .permission(HomesPerms.COMMAND_HOMES_LIST)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).optional().permission(HomesPerms.COMMAND_HOMES_LIST_OTHERS))
            .executes(this::listHomes)
        );

        this.registerLiteral(COMMAND_SET, true, new String[]{"sethome"}, builder -> builder
            .playerOnly()
            .description(HomesLang.COMMAND_SET_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_SET)
            .withArguments(Arguments.string(CommandArguments.NAME)
                .optional()
                .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
                .suggestions((reader, context) -> this.module.getHomes(context.getPlayerOrThrow()).stream().map(Home::getId).toList())
            )
            .executes(this::setHome)
        );

        this.registerLiteral(COMMAND_TELEPORT, true, new String[]{HomeDefaults.DEFAULT_TELEPORT_ALIAS}, builder -> builder
            .playerOnly()
            .description(HomesLang.COMMAND_TELEPORT_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_TELEPORT)
            .withArguments(this.homeArgument(false).optional())
            .executes(this::teleportToHome)
        );

        this.registerLiteral(COMMAND_VISIT, true, new String[]{"visithome"}, builder -> builder
            .playerOnly()
            .description(HomesLang.COMMAND_VISIT_HOME_DESC)
            .permission(HomesPerms.COMMAND_HOMES_VISIT)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER).suggestions((reader, context) -> {
                    Player player = context.getPlayer();
                    if (player == null) return Collections.emptyList();

                    return this.module.getRepository().getAll(home -> home.canVisit(player)).stream().map(Home::getOwner).map(UserInfo::name).distinct().toList();
                }),
                this.homeArgument(true)
            )
            .executes(this::visitHome)
        );

        this.registerLiteral(COMMAND_INVITE, false, new String[]{"homeinvite"}, builder -> builder
            .playerOnly()
            .description(HomesLang.COMMAND_HOME_INVITE_DESC)
            .permission(HomesPerms.COMMAND_HOMES_INVITE)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER), this.homeArgument(false))
            .executes(this::inviteHome)
        );

        this.registerRoot("homes", true, new String[]{"homes"},
            map -> {
                map.put(COMMAND_DELETE, "delete");
                map.put(COMMAND_LIST, "list");
                map.put(COMMAND_SET, "set");
                map.put(COMMAND_TELEPORT, "teleport");
                map.put(COMMAND_VISIT, "visit");
                map.put(COMMAND_INVITE, "invite");
            },
            builder -> builder.description(HomesLang.COMMAND_HOMES_ROOT_DESC).permission(HomesPerms.COMMAND_HOMES_ROOT)
        );
    }

    @NotNull
    private ArgumentNodeBuilder<Home> homeArgument(boolean ofPlayer) {
        return Commands.argument(ARG_HOME, (context, string) ->
                Optional.of(ofPlayer ? context.getArguments().getString(CommandArguments.PLAYER) : context.getSender().getName())
                    .map(name -> this.userManager.getRepository().getAssociatedId(name))
                    .map(playerId -> this.module.getHome(playerId, string)).orElseThrow(() -> CommandSyntaxException.custom(HomesLang.COMMAND_SYNTAX_INVALID_HOME))
            )
            .localized(HomesLang.COMMAND_ARGUMENT_NAME_HOME)
            .suggestions((reader, context) -> {
                Player player = context.getPlayer();
                if (player == null) return Collections.emptyList();

                String ownerName = ofPlayer ? context.getArguments().getString(CommandArguments.PLAYER) : player.getName();
                UUID ownerId = this.userManager.getRepository().getAssociatedId(ownerName);
                if (ownerId == null) return Collections.emptyList();

                return this.module.getUserRepository(ownerId).getAll(home -> home.canVisit(player)).stream().map(Home::getId).toList();
            });
    }

    @Nullable
    private Home getHomeOrDefault(@NotNull Player player, @NotNull ParsedArguments arguments, @NotNull CommandContext context) {
        if (arguments.contains(ARG_HOME)) {
            return arguments.get(ARG_HOME, Home.class);
        }

        Home home = this.module.getFavoriteHome(player);
        if (home == null) {
            this.module.sendPrefixed(HomesLang.ERROR_NO_HOMES, context.getSender());
            return null;
        }

        return home;
    }

    private boolean deleteHome(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Home home = this.getHomeOrDefault(player, arguments, context);
        if (home == null) return false;

        return this.module.removeHome(player, home);
    }

    private boolean listHomes(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String targetName = arguments.getString(CommandArguments.PLAYER, player.getName());
        UUID targetId = this.userManager.getRepository().getAssociatedId(targetName);
        if (targetId == null) {
            context.errorBadPlayer();
            return false;
        }

        return this.module.openHomes(player, targetId);
    }

    private boolean setHome(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String name = arguments.getString(CommandArguments.NAME, HomeDefaults.DEFAULT_HOME_ID);

        return this.module.setHome(player, name, false);
    }

    private boolean teleportToHome(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        Home home = this.getHomeOrDefault(player, arguments, context);
        if (home == null) return false;

        return this.module.teleportToHome(player, home);
    }

    private boolean visitHome(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        String ownerName = arguments.getString(CommandArguments.PLAYER);

        UUID ownerId = this.userManager.getRepository().getAssociatedId(ownerName);
        if (ownerId == null) {
            context.errorBadPlayer();
            return false;
        }

        Home home = arguments.get(ARG_HOME, Home.class);

        return this.module.visitHome(player, home);
    }

    private boolean inviteHome(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        Home home = this.getHomeOrDefault(player, arguments, context);
        if (home == null) return false;

        String targetName = arguments.getString(CommandArguments.PLAYER);
        UUID targetId = this.userManager.getRepository().getAssociatedId(targetName);
        if (targetId == null) {
            context.errorBadPlayer();
            return false;
        }

        return this.module.inviteToHome(player, home, new UserInfo(targetId, targetName));
    }
}
