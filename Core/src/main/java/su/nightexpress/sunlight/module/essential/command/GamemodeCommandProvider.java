package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.GameMode;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TYPE;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class GamemodeCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_SURVIVAL = "survival";
    private static final String COMMAND_SPECTATOR = "spectator";
    private static final String COMMAND_ADVENTURE = "adventure";
    private static final String COMMAND_CREATIVE = "creative";

    private static final Permission PERM_SURVIVAL  = EssentialPerms.COMMAND.permission("gamemode.survival");
    private static final Permission PERM_SPECTATOR = EssentialPerms.COMMAND.permission("gamemode.spectator");
    private static final Permission PERM_ADVENTURE = EssentialPerms.COMMAND.permission("gamemode.adventure");
    private static final Permission PERM_CREATIVE  = EssentialPerms.COMMAND.permission("gamemode.creative");
    private static final Permission PERM_OTEHRS    = EssentialPerms.COMMAND.permission("gamemode.others");
    private static final Permission PERM_ROOT      = EssentialPerms.COMMAND.permission("gamemode.root");

    private static final TextLocale DESCRIPTION_ROOT = LangEntry.builder("Command.GameMode.Root.Desc").text("Game Mode commands.");
    private static final TextLocale DESCRIPTION_TYPE = LangEntry.builder("Command.GameMode.Type.Desc").text("Set Game Mode to " + GENERIC_TYPE + ".");

    private static final MessageLocale MESSAGE_SET_NOTIFY = LangEntry.builder("Command.GameMode.Notify").chatMessage(
        GRAY.wrap("Your Game Mode has been set to " + SOFT_YELLOW.wrap(GENERIC_TYPE) + ".")
    );

    private static final MessageLocale MESSAGE_SET_FEEDBACK = LangEntry.builder("Command.GameMode.Target").chatMessage(
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s Game Mode to " + SOFT_YELLOW.wrap(GENERIC_TYPE) + ".")
    );

    private final EssentialModule module;
    private final UserManager userManager;

    public GamemodeCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_SURVIVAL, true, new String[]{"gms", "survival"}, builder -> this.builderType(builder, GameMode.SURVIVAL));
        this.registerLiteral(COMMAND_SPECTATOR, true, new String[]{"gmsp", "spectator"}, builder -> this.builderType(builder, GameMode.SPECTATOR));
        this.registerLiteral(COMMAND_ADVENTURE, true, new String[]{"gma", "adventure"}, builder -> this.builderType(builder, GameMode.ADVENTURE));
        this.registerLiteral(COMMAND_CREATIVE, true, new String[]{"gmc", "creative"}, builder -> this.builderType(builder, GameMode.CREATIVE));

        this.registerRoot("Game Mode", true, new String[]{"gm", "gamemode"},
            Map.of(
                COMMAND_SURVIVAL, "survival",
                COMMAND_SPECTATOR, "spectator",
                COMMAND_ADVENTURE, "adventure",
                COMMAND_CREATIVE, "creative"
            ),
            builder -> builder.description(DESCRIPTION_ROOT).permission(PERM_ROOT));
    }

    private void builderType(@NotNull LiteralNodeBuilder builder, @NotNull GameMode mode) {
        Permission permission = switch (mode) {
            case CREATIVE -> PERM_CREATIVE;
            case SURVIVAL -> PERM_SURVIVAL;
            case ADVENTURE -> PERM_ADVENTURE;
            case SPECTATOR -> PERM_SPECTATOR;
        };

        builder
            .description(DESCRIPTION_TYPE.text().replace(SLPlaceholders.GENERIC_TYPE, Lang.GAME_MODE.getLocalized(mode)))
            .permission(permission)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERM_OTEHRS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.changeGameMode(context, arguments, mode));
    }

    private boolean changeGameMode(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull GameMode mode) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            if (target.isOnline()) {
                target.setGameMode(mode);
            }
            else {
                SunNMS internals = this.plugin.getInternals();
                if (internals == null) {
                    this.module.sendPrefixed(Lang.ERROR_NO_INTERNALS_HANDLER, context.getSender());
                    return;
                }
                internals.setGameMode(target, mode);
            }

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_SET_FEEDBACK, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_TYPE, () -> Lang.GAME_MODE.getLocalized(mode))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_SET_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_TYPE, () -> Lang.GAME_MODE.getLocalized(mode))
                );
            }
        });
    }
}
