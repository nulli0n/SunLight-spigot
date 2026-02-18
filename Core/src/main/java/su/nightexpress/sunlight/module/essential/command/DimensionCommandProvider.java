package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.teleport.TeleportContext;
import su.nightexpress.sunlight.teleport.TeleportManager;
import su.nightexpress.sunlight.teleport.TeleportType;
import su.nightexpress.sunlight.user.UserManager;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_WORLD;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class DimensionCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("dimension");
    private static final Permission PERMISSION_OTHERS = EssentialPerms.COMMAND.permission("dimension.others");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Dimension.Desc").text("Teleport to a world.");

    private static final MessageLocale MESSAGE_FEEDBACK = LangEntry.builder("Command.Dimension.Target").chatMessage(
        GRAY.wrap("You've teleported player " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " to the " + SOFT_YELLOW.wrap(GENERIC_WORLD) + ".")
    );

    private static final MessageLocale MESSAGE_NOTIFY = LangEntry.builder("Command.Dimension.Notify").chatMessage(
        Sound.ENTITY_ENDERMAN_TELEPORT,
        GRAY.wrap("You have teleported to the " + ORANGE.wrap(GENERIC_WORLD) + ".")
    );

    private final EssentialModule module;
    private final UserManager     userManager;
    private final TeleportManager teleportManager;

    public DimensionCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager, @NotNull TeleportManager teleportManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
        this.teleportManager = teleportManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("dimension", true, new String[]{"dimension", "dim"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(
                Arguments.world(CommandArguments.WORLD),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::moveToWorld)
        );
    }

    private boolean moveToWorld(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            World world = arguments.getWorld(CommandArguments.WORLD);
            Location location = world.getSpawnLocation();

            TeleportContext teleportContext = TeleportContext.builder(this.module, target, location)
                .sender(context.getSender())
                .callback(() -> {
                    if (context.getSender() != target) {
                        this.module.sendPrefixed(MESSAGE_FEEDBACK, context.getSender(), builder -> builder
                            .with(CommonPlaceholders.PLAYER.resolver(target))
                            .with(SLPlaceholders.GENERIC_WORLD, () -> BukkitThing.getValue(world))
                        );
                    }

                    if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                        this.module.sendPrefixed(MESSAGE_NOTIFY, target, builder -> builder
                            .with(SLPlaceholders.GENERIC_WORLD, () -> BukkitThing.getValue(world))
                        );
                    }
                })
                .build();

            this.teleportManager.teleport(teleportContext, TeleportType.OTHER);
        });
    }
}
