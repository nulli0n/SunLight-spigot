package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.teleport.TeleportContext;
import su.nightexpress.sunlight.teleport.TeleportFlag;
import su.nightexpress.sunlight.teleport.TeleportManager;
import su.nightexpress.sunlight.teleport.TeleportType;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class TeleportCommandsProvider extends AbstractCommandProvider {

    // TODO Split

    private static final String COMMAND_LOCATION = "tppos";
    private static final String COMMAND_MOVE     = "tpplayer";
    private static final String COMMAND_BRING    = "tphere";
    private static final String COMMAND_GO_TO    = "goto";
    private static final String COMMAND_TOP      = "tptop";

    private static final Permission PERMISSION_LOCATION        = EssentialPerms.COMMAND.permission("coords");
    private static final Permission PERMISSION_LOCATION_OTHERS = EssentialPerms.COMMAND.permission("coords.others");
    private static final Permission PERMISSION_BRING           = EssentialPerms.COMMAND.permission("bring");
    private static final Permission PERMISSION_GOTO            = EssentialPerms.COMMAND.permission("goto");
    private static final Permission PERMISSION_MOVE            = EssentialPerms.COMMAND.permission("move");
    private static final Permission PERMISSION_SURFACE         = EssentialPerms.COMMAND.permission("surface");
    private static final Permission PERMISSION_SURFACE_OTHERS  = EssentialPerms.COMMAND.permission("surface.others");

    private static final TextLocale DESCRIPTION_LOCATION = LangEntry.builder("Command.Teleport.Location.Desc").text(
        "Teleport to specific position.");
    private static final TextLocale DESCRIPTION_SUMMON   = LangEntry.builder("Command.Teleport.Summon.Desc").text(
        "Teleport player to your location.");
    private static final TextLocale DESCRIPTION_TO       = LangEntry.builder("Command.Teleport.To.Desc").text(
        "Teleport to a player's location.");
    private static final TextLocale DESCRIPTION_SEND     = LangEntry.builder("Command.Teleport.Send.Desc").text(
        "Teleport one player to another.");
    private static final TextLocale DESCRIPTION_TOP      = LangEntry.builder("Command.Teleport.Top.Desc").text(
        "Teleport to the highest block above you.");

    private static final MessageLocale MESSAGE_COORDS_FEEDBACK = LangEntry.builder(
        "Command.Teleport.Location.Done.Target").chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have teleported " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " teleported to " + RED.wrap(
                LOCATION_X) + " " + GREEN.wrap(LOCATION_Y) + " " + BLUE.wrap(LOCATION_Z) + " @ " + WHITE.wrap(
                    LOCATION_WORLD) + ".")
        );

    private static final MessageLocale MESSAGE_COORDS_NOTIFY = LangEntry.builder(
        "Command.Teleport.Location.Done.Notify").chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have been teleported to " + RED.wrap(LOCATION_X) + " " + GREEN.wrap(LOCATION_Y) + " " + BLUE
                .wrap(LOCATION_Z) + " @ " + WHITE.wrap(LOCATION_WORLD) + ".")
        );

    private static final MessageLocale MESSAGE_SUMMON_YOURSELF = LangEntry.builder("Command.Teleport.Summon.Yourself")
        .chatMessage(
            Sound.ENTITY_VILLAGER_NO,
            GRAY.wrap("You can not summon " + RED.wrap("yourself") + ".")
        );

    private static final MessageLocale MESSAGE_SUMMON_FEEDBACK = LangEntry.builder("Command.Teleport.Summon.Target")
        .chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have summonned " + WHITE.wrap(PLAYER_DISPLAY_NAME) + ".")
        );

    private static final MessageLocale MESSAGE_SUMMON_NOTIFY = LangEntry.builder("Command.Teleport.Summon.Notify")
        .chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have been summoned by " + WHITE.wrap(PLAYER_DISPLAY_NAME) + ".")
        );

    private static final MessageLocale MESSAGE_TO_YOURSELF = LangEntry.builder("Command.Teleport.To.Yourself")
        .chatMessage(
            Sound.ENTITY_VILLAGER_NO,
            GRAY.wrap("You can not teleport to " + RED.wrap("yourself") + ".")
        );

    private static final MessageLocale MESSAGE_TO_DONE = LangEntry.builder("Command.Teleport.To.Done").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You have been teleported to " + WHITE.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    private static final MessageLocale MESSAGE_MOVE_FEEDBACK = LangEntry.builder("Command.Teleport.Send.Target")
        .chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have teleported " + RED.wrap(GENERIC_SOURCE) + " to " + BLUE.wrap(GENERIC_TARGET) + ".")
        );

    private static final MessageLocale MESSAGE_MOVE_NOTIFY = LangEntry.builder("Command.Teleport.Send.Notify")
        .chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have been teleported to " + WHITE.wrap(PLAYER_DISPLAY_NAME) + ".")
        );

    private static final MessageLocale MESSAGE_SURFACE_FEEDBACK = LangEntry.builder("Command.Teleport.Top.Target")
        .chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have teleported " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " to the surface.")
        );

    private static final MessageLocale MESSAGE_SURFACE_NOTIFY = LangEntry.builder("Command.Teleport.Top.Notify")
        .chatMessage(
            Sound.ENTITY_ENDERMAN_TELEPORT,
            GRAY.wrap("You have been teleported to the surface.")
        );

    private final EssentialModule module;
    private final UserManager     userManager;
    private final TeleportManager teleportManager;

    public TeleportCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager, @NotNull TeleportManager teleportManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
        this.teleportManager = teleportManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_LOCATION, true, new String[]{"coords", "tppos", "tploc"}, builder -> builder
            .description(DESCRIPTION_LOCATION)
            .permission(PERMISSION_LOCATION)
            .withArguments(
                Arguments.integer(CommandArguments.X).localized(Lang.COMMAND_ARGUMENT_NAME_X)
                    .suggestions((reader, context) -> getTabLocation(context, Block::getX)),
                Arguments.integer(CommandArguments.Y).localized(Lang.COMMAND_ARGUMENT_NAME_Y)
                    .suggestions((reader, context) -> getTabLocation(context, Block::getY)),
                Arguments.integer(CommandArguments.Z).localized(Lang.COMMAND_ARGUMENT_NAME_Z)
                    .suggestions((reader, context) -> getTabLocation(context, Block::getZ)),
                Arguments.world(CommandArguments.WORLD).optional(),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_LOCATION_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::teleportToCoords)
        );

        this.registerLiteral(COMMAND_MOVE, true, new String[]{"move", "tpplayer"}, builder -> builder
            .description(DESCRIPTION_SEND)
            .permission(PERMISSION_MOVE)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.playerName(CommandArguments.TARGET).localized(Lang.COMMAND_ARGUMENT_NAME_TARGET)
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::movePlayerToOther)
        );

        this.registerLiteral(COMMAND_BRING, true, new String[]{"bring", "tphere", "movehere"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION_SUMMON)
            .permission(PERMISSION_BRING)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER))
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::summonPlayer)
        );

        this.registerLiteral(COMMAND_GO_TO, true, new String[]{"goto", "tpto", "moveto"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION_TO)
            .permission(PERMISSION_GOTO)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER))
            .executes(this::teleportToPlayer)
        );

        this.registerLiteral(COMMAND_TOP, true, new String[]{"surface", "tptop", "movetop"}, builder -> builder
            .description(DESCRIPTION_TOP.text())
            .permission(PERMISSION_SURFACE)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).optional().permission(
                PERMISSION_SURFACE_OTHERS))
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::teleportToTop)
        );
    }

    @NotNull
    private List<String> getTabLocation(@NotNull CommandContext context, @NotNull Function<Block, Integer> function) {
        Player player = context.getPlayer();
        if (player == null) return Collections.emptyList();

        Block block = player.getTargetBlock(null, 100);
        return Lists.newList(NumberUtil.format(function.apply(block)));
    }

    private boolean teleportToCoords(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            World world = arguments.getWorld(CommandArguments.WORLD, target.getWorld());
            int x = arguments.getInt(CommandArguments.X);
            int y = arguments.getInt(CommandArguments.Y);
            int z = arguments.getInt(CommandArguments.Z);

            Block block = world.getBlockAt(x, y, z);
            Location location = block.getRelative(BlockFace.UP).getLocation();

            TeleportContext teleportContext = TeleportContext.builder(this.module, target, location)
                .withFlag(TeleportFlag.KEEP_DIRECTION)
                .withFlag(TeleportFlag.CENTERED)
                .sender(context.getSender())
                .callback(() -> {
                    if (context.getSender() != target) {
                        this.module.sendPrefixed(MESSAGE_COORDS_FEEDBACK, context.getSender(), replacer -> replacer
                            .with(CommonPlaceholders.PLAYER.resolver(target))
                            .with(CommonPlaceholders.LOCATION.resolver(location))
                        );
                    }

                    if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                        this.module.sendPrefixed(MESSAGE_COORDS_NOTIFY, target, replacer -> replacer.with(
                            CommonPlaceholders.LOCATION.resolver(location)));
                    }
                })
                .build();

            this.teleportManager.teleport(teleportContext, TeleportType.OTHER);
        });
    }

    private boolean movePlayerToOther(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String targetName = arguments.getString(CommandArguments.TARGET);
        this.loadPlayerAndRunInMainThread(context, targetName, this.module, this.userManager, target -> {

            this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, toPlayer -> {
                Location location = target.getLocation();

                TeleportContext teleportContext = TeleportContext.builder(this.module, target, location)
                    .withFlag(TeleportFlag.KEEP_DIRECTION)
                    .withFlag(TeleportFlag.LOOK_FOR_SURFACE)
                    .sender(context.getSender())
                    .callback(() -> {
                        if (toPlayer != context.getSender()) {
                            this.module.sendPrefixed(MESSAGE_MOVE_FEEDBACK, context.getSender(), replacer -> replacer
                                .with(GENERIC_SOURCE, toPlayer::getName)
                                .with(GENERIC_TARGET, target::getName)
                            );
                        }

                        if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                            this.module.sendPrefixed(MESSAGE_MOVE_NOTIFY, toPlayer, replacer -> replacer.with(
                                CommonPlaceholders.PLAYER.resolver(target)));
                        }
                    })
                    .build();

                this.teleportManager.teleport(teleportContext, TeleportType.OTHER);
            });
        });

        return true;
    }

    private boolean summonPlayer(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player executor = context.getPlayerOrThrow();

        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            if (target == executor) {
                this.module.sendPrefixed(MESSAGE_SUMMON_YOURSELF, context.getSender());
                return;
            }

            Location location = executor.getLocation();

            TeleportContext teleportContext = TeleportContext.builder(this.module, target, location)
                .withFlag(TeleportFlag.KEEP_DIRECTION)
                .withFlag(TeleportFlag.LOOK_FOR_SURFACE)
                .callback(() -> {
                    this.module.sendPrefixed(MESSAGE_SUMMON_FEEDBACK, context.getSender(), replacer -> replacer.with(
                        CommonPlaceholders.PLAYER.resolver(target)));

                    if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                        this.module.sendPrefixed(MESSAGE_SUMMON_NOTIFY, target, replacer -> replacer.with(
                            CommonPlaceholders.PLAYER.resolver(executor)));
                    }
                })
                .build();

            this.teleportManager.teleport(teleportContext, TeleportType.OTHER);
        });
    }

    private boolean teleportToPlayer(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            if (target == player) {
                this.module.sendPrefixed(MESSAGE_TO_YOURSELF, context.getSender());
                return;
            }

            Location location = target.getLocation();
            TeleportContext teleportContext = TeleportContext.builder(this.module, player, location)
                .withFlag(TeleportFlag.LOOK_FOR_SURFACE)
                .callback(() -> this.module.sendPrefixed(MESSAGE_TO_DONE, player, replacer -> replacer.with(
                    CommonPlaceholders.PLAYER.resolver(target))))
                .build();

            this.teleportManager.teleport(teleportContext, TeleportType.OTHER);
        });
    }

    private boolean teleportToTop(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            Block block = target.getWorld().getHighestBlockAt(target.getLocation()).getRelative(BlockFace.UP);
            Location location = block.getLocation();

            TeleportContext teleportContext = TeleportContext.builder(this.module, target, location)
                .withFlag(TeleportFlag.KEEP_DIRECTION)
                .withFlag(TeleportFlag.LOOK_FOR_SURFACE)
                .withFlag(TeleportFlag.CENTERED)
                .sender(context.getSender())
                .callback(() -> {
                    if (context.getSender() != target) {
                        this.module.sendPrefixed(MESSAGE_SURFACE_FEEDBACK, context.getSender(), replacer -> replacer
                            .with(CommonPlaceholders.PLAYER.resolver(target))
                        );
                    }

                    if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                        this.module.sendPrefixed(MESSAGE_SURFACE_NOTIFY, target);
                    }
                })
                .build();

            this.teleportManager.teleport(teleportContext, TeleportType.OTHER);
        });
    }
}
