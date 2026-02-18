package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.module.essential.EssentialSettings;
import su.nightexpress.sunlight.module.essential.object.TimeAlias;
import su.nightexpress.sunlight.SLUtils;
import su.nightexpress.sunlight.utils.WorldTime;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class PlayerTimeCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION_ROOT         = EssentialPerms.COMMAND.permission("playertime.root");
    private static final Permission PERMISSION_SET          = EssentialPerms.COMMAND.permission("playertime.set");
    private static final Permission PERMISSION_SET_OTHERS   = EssentialPerms.COMMAND.permission("playertime.set.others");
    private static final Permission PERMISSION_RESET        = EssentialPerms.COMMAND.permission("playertime.reset");
    private static final Permission PERMISSION_RESET_OTHERS = EssentialPerms.COMMAND.permission("playertime.reset.others");

    private static final TextLocale DESCRIPTION_ROOT     = LangEntry.builder("Command.PlayerTime.Root.Desc").text("Player time commands.");
    private static final TextLocale DESCRIPTION_SET      = LangEntry.builder("Command.PlayerTime.Set.Desc").text("Set individual player time.");
    private static final TextLocale DESCRIPTION_SET_TIME = LangEntry.builder("Command.PlayerTime.SetTime.Desc").text("Set individual player time to %s ticks.");
    private static final TextLocale DESCRIPTION_RESET    = LangEntry.builder("Command.PlayerTime.Reset.Desc").text("Reset individual player time.");

    private static final MessageLocale MESSAGE_SET_FEEDBACK = LangEntry.builder("Command.Time.Personal.Set.Target").chatMessage(
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s time to " + SOFT_YELLOW.wrap(GENERIC_TIME) + " (" + WHITE.wrap(GENERIC_TOTAL + " ticks") + ")" + ".")
    );

    private static final MessageLocale MESSAGE_SET_NOTIFY = LangEntry.builder("Command.Time.Personal.Set.Notify").chatMessage(
        GRAY.wrap("Your personal time has been set to " + SOFT_YELLOW.wrap(GENERIC_TIME) + " (" + WHITE.wrap(GENERIC_TOTAL + " ticks") + ")" + ".")
    );

    private static final MessageLocale MESSAGE_RESET_FEEDBACK = LangEntry.builder("Command.Time.Personal.Reset.Target").chatMessage(
        GRAY.wrap("You have reset " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "'s time.")
    );

    private static final MessageLocale MESSAGE_RESET_NOTIFY = LangEntry.builder("Command.Time.Personal.Reset.Notify").chatMessage(
        GRAY.wrap("Your personal time has been reset.")
    );

    private static final String COMMAND_SET   = "set";
    private static final String COMMAND_RESET = "reset";

    private final EssentialModule module;
    private final EssentialSettings settings;
    private final Set<TimeAlias> timeAliases;

    public PlayerTimeCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull EssentialSettings settings) {
        super(plugin);
        this.module = module;
        this.settings = settings;
        this.timeAliases = new LinkedHashSet<>();

        this.settings.timeAliases.get().forEach((name, gameTime) -> {
            this.timeAliases.add(new TimeAlias(LowerCase.INTERNAL.apply(name), gameTime));
        });
    }

    @Override
    public void registerDefaults() {
        Map<String, String> rootChildrens = new LinkedHashMap<>();
        rootChildrens.put(COMMAND_SET, "set");
        rootChildrens.put(COMMAND_RESET, "reset");

        this.timeAliases.forEach(timeAlias -> {
            String name = timeAlias.name();
            long time = timeAlias.gameTime();

            this.registerLiteral(name, false, new String[]{"p" + name}, builder -> builder
                .description(DESCRIPTION_SET_TIME.text().formatted(String.valueOf(time)))
                .permission(PERMISSION_SET)
                .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_SET_OTHERS).optional())
                .executes((context, arguments) -> this.setPlayerTime(context, arguments, time))
            );
            rootChildrens.put(name, name);
        });

        this.registerLiteral(COMMAND_SET, false, new String[]{"setplayertime"}, builder -> builder
            .description(DESCRIPTION_SET)
            .permission(PERMISSION_SET)
            .withArguments(
                Arguments.integer(CommandArguments.TIME, (int) WorldTime.MIN_TICKS, (int) WorldTime.MAX_TICKS)
                    .localized(Lang.COMMAND_ARGUMENT_NAME_TIME)
                    .suggestions((reader, context) -> IntStream.range(0, 25).boxed().map(hour -> hour * WorldTime.MODIFIER).map(String::valueOf).toList()),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_SET_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes((context, arguments) -> this.setPlayerTime(context, arguments, arguments.getInt(CommandArguments.TIME)))
        );

        this.registerLiteral(COMMAND_RESET, false, new String[]{"resetplayertime"}, builder -> builder
            .description(DESCRIPTION_RESET.text())
            .permission(PERMISSION_RESET)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_RESET_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::clearPlayerTime)
        );

        this.registerRoot("Player Time", true, new String[]{"playertime", "ptime"}, rootChildrens, builder -> builder
            .description(DESCRIPTION_ROOT)
            .permission(PERMISSION_ROOT)
        );
    }

    private boolean setPlayerTime(@NotNull CommandContext context, @NotNull ParsedArguments arguments, long ticks) {
        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            long finalTicks = WorldTime.clamp(ticks);

            target.setPlayerTime(finalTicks, true);
            long totalTicks = target.getPlayerTime() % WorldTime.MAX_TICKS;
            LocalTime time = WorldTime.getTimeOfTicks(totalTicks);

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_SET_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(GENERIC_TIME, () -> SLUtils.formatTime(time))
                    .with(GENERIC_TOTAL, () -> NumberUtil.format(totalTicks))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_SET_NOTIFY, target, replacer -> replacer
                    .with(GENERIC_TIME, () -> SLUtils.formatTime(time))
                    .with(GENERIC_TOTAL, () -> NumberUtil.format(totalTicks))
                );
            }

            return true;
        });
    }

    private boolean clearPlayerTime(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            target.resetPlayerTime();

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_RESET_FEEDBACK, context.getSender(), replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_RESET_NOTIFY, target);
            }
            return true;
        });
    }
}
