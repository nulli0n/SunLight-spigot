package su.nightexpress.sunlight.module.kits.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.model.Kit;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class KitsCommandProvider extends AbstractCommandProvider {

    private static final String ARG_KIT = "kit";

    private static final String COMMAND_EDITOR         = "editor";
    private static final String COMMAND_GET            = "get";
    private static final String COMMAND_GIVE           = "give";
    private static final String COMMAND_LIST           = "list";
    private static final String COMMAND_RESET_COOLDOWN = "reset_cooldown";
    private static final String COMMAND_SET_COOLDOWN   = "set_cooldown";
    private static final String COMMAND_PREVIEW        = "preview";

    private final KitsModule module;
    private final UserManager userManager;

    public KitsCommandProvider(@NotNull SunLightPlugin plugin, @NotNull KitsModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_EDITOR, true, new String[]{"editkit"}, builder -> builder
            .playerOnly()
            .description(KitsLang.COMMAND_KITS_EDITOR_DESC)
            .permission(KitsPerms.COMMAND_EDIT_KIT)
            .executes(this::openEditor)
        );

        this.registerLiteral(COMMAND_GET, true, new String[]{"kit"}, builder -> builder
            .playerOnly()
            .description(KitsLang.COMMAND_KITS_GET_DESC)
            .permission(KitsPerms.COMMAND_KIT_GET)
            .withArguments(this.kitArgument())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::getKit)
        );

        this.registerLiteral(COMMAND_GIVE, true, new String[]{"givekit"}, builder -> builder
            .description(KitsLang.COMMAND_KITS_GIVE_DESC)
            .permission(KitsPerms.COMMAND_KIT_GIVE)
            .withArguments(
                this.kitArgument(),
                Arguments.playerName(CommandArguments.PLAYER)
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::giveKit)
        );

        this.registerLiteral(COMMAND_LIST, true, new String[]{"kitlist"}, builder -> builder
            .description(KitsLang.COMMAND_KITS_LIST_DESC)
            .permission(KitsPerms.COMMAND_KIT_LIST)
            .withArguments(Arguments.player(CommandArguments.PLAYER).optional().permission(KitsPerms.COMMAND_KIT_LIST_OTHERS))
            .executes(this::listKits)
        );

        this.registerLiteral(COMMAND_PREVIEW, true, new String[]{"viewkit"}, builder -> builder
            .description(KitsLang.COMMAND_KITS_PREVIEW_DESC)
            .permission(KitsPerms.COMMAND_PREVIEW_KIT)
            .withArguments(
                this.kitArgument(),
                Arguments.player(CommandArguments.PLAYER).optional().permission(KitsPerms.COMMAND_PREVIEW_KIT_OTHERS)
            )
            .executes(this::previewKit)
        );

        this.registerLiteral(COMMAND_RESET_COOLDOWN, false, new String[]{"resetkitcooldown"}, builder -> builder
            .description(KitsLang.COMMAND_KITS_RESET_COOLDOWN_DESC)
            .permission(KitsPerms.COMMAND_RESET_KIT_COOLDOWN)
            .withArguments(
                this.kitArgument(),
                Arguments.playerName(CommandArguments.PLAYER)
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::resetCooldown)
        );

        this.registerLiteral(COMMAND_SET_COOLDOWN, false, new String[]{"setkitcooldown"}, builder -> builder
            .description(KitsLang.COMMAND_KITS_SET_COOLDOWN_DESC)
            .permission(KitsPerms.COMMAND_SET_KIT_COOLDOWN)
            .withArguments(
                this.kitArgument(),
                Arguments.integer(CommandArguments.TIME, 1)
                    .localized(KitsLang.COMMAND_ARGUMENT_NAME_TIME)
                    .suggestions((reader, context) -> Lists.newList("300", "3600", "86400")),
                Arguments.playerName(CommandArguments.PLAYER)
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::setCooldown)
        );

        this.registerRoot("kits", true, new String[]{"kits"},
            map -> {
                map.put(COMMAND_EDITOR, "editor");
                map.put(COMMAND_GET, "get");
                map.put(COMMAND_GIVE, "give");
                map.put(COMMAND_LIST, "list");
                map.put(COMMAND_RESET_COOLDOWN, "resetcooldown");
                map.put(COMMAND_SET_COOLDOWN, "setcooldown");
                map.put(COMMAND_PREVIEW, "preview");
            },
            builder -> builder.description(KitsLang.COMMAND_KITS_ROOT_DESC).permission(KitsPerms.COMMAND_KITS_ROOT)
        );
    }

    @NotNull
    private ArgumentNodeBuilder<Kit> kitArgument() {
        return Commands.argument(ARG_KIT, (context, string) ->
                Optional.ofNullable(this.module.getKitById(string)).orElseThrow(() -> CommandSyntaxException.custom(KitsLang.COMMAND_SYNTAX_INVALID_KIT))
            )
            .suggestions((reader, context) -> context.getPlayer() != null ? this.module.getKitIds(context.getPlayer()) : this.module.getKitIds())
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME);
    }

    private boolean openEditor(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        this.module.openEditor(player);
        return true;
    }

    private boolean getKit(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.get(ARG_KIT, Kit.class);
        Player player = context.getPlayerOrThrow();

        return this.module.giveKit(kit, player, false, false);
    }

    private boolean giveKit(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.get(ARG_KIT, Kit.class);

        this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            boolean force = context.getSender() != target;
            boolean silent = context.hasFlag(CommandArguments.FLAG_SILENT);

            this.module.giveKit(kit, target, force, silent);

            this.module.sendPrefixed(KitsLang.KIT_GIVE_FEEDBACK, context.getSender(), builder -> builder
                .with(kit.placeholders())
                .with(CommonPlaceholders.PLAYER.resolver(target))
            );
        });

        return true;
    }

    private boolean listKits(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            this.module.openKitsMenu(target);

            if (context.getSender() != target) {
                this.module.sendPrefixed(KitsLang.KIT_BROWSER_OPEN_FEEDBACK, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }

            return true;
        });
    }

    private boolean previewKit(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.get(ARG_KIT, Kit.class);

        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            this.module.previewKit(target, kit);

            if (context.getSender() != target) {
                this.module.sendPrefixed(KitsLang.KIT_PREVIEW_FEEDBACK, context.getSender(), builder -> builder
                    .with(kit.placeholders())
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                );
            }
            return true;
        });
    }

    private boolean resetCooldown(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.get(ARG_KIT, Kit.class);
        String playerName = arguments.getString(CommandArguments.PLAYER);

        this.userManager.loadTargetProfile(playerName).thenCompose(profile -> {
            if (profile == null) {
                context.errorBadPlayer();
                return CompletableFuture.completedFuture(null);
            }

            return this.module.getKitDataOrCreate(profile.id(), kit.getId()).thenAccept(kitData -> {
                kitData.setCooldownDate(0L);
                kitData.markDirty();

                this.module.sendPrefixed(KitsLang.KIT_RESET_COOLDOWN_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(kit.placeholders())
                    .with(CommonPlaceholders.PLAYER_NAME, profile::name)
                );

                Player target = Players.getPlayer(profile.id());
                if (target != null && !context.hasFlag(CommandArguments.FLAG_SILENT)) {
                    this.module.sendPrefixed(KitsLang.KIT_RESET_COOLDOWN_NOTIFY, target, replacer -> replacer
                        .with(kit.placeholders())
                    );
                }
            }).whenComplete(FutureUtils::printStacktrace);
        });

        return true;
    }

    private boolean setCooldown(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Kit kit = arguments.get(ARG_KIT, Kit.class);
        String playerName = arguments.getString(CommandArguments.PLAYER);

        int amount = arguments.getInt(CommandArguments.TIME);
        if (amount == 0) return false;

        this.userManager.loadTargetProfile(playerName).thenCompose(profile -> {
            if (profile == null) {
                context.errorBadPlayer();
                return CompletableFuture.completedFuture(null);
            }

            return this.module.getKitDataOrCreate(profile.id(), kit.getId()).thenAccept(kitData -> {
                kitData.setCooldownDate(TimeUtil.createFutureTimestamp(amount));
                kitData.markDirty();

                this.module.sendPrefixed(KitsLang.KIT_SET_COOLDOWN_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(kit.placeholders())
                    .with(CommonPlaceholders.PLAYER_NAME, profile::name)
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> TimeFormats.formatAmount(TimeUnit.SECONDS.toMillis(amount), TimeFormatType.LITERAL))
                );

                Player target = Players.getPlayer(profile.id());
                if (target != null && !context.hasFlag(CommandArguments.FLAG_SILENT)) {
                    this.module.sendPrefixed(KitsLang.KIT_SET_COOLDOWN_NOTIFY, target, replacer -> replacer
                        .with(kit.placeholders())
                        .with(SLPlaceholders.GENERIC_AMOUNT, () -> TimeFormats.formatAmount(TimeUnit.SECONDS.toMillis(amount), TimeFormatType.LITERAL))
                    );
                }
            });
        }).whenComplete(FutureUtils::printStacktrace);

        return true;
    }
}
