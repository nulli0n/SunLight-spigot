package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
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
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.user.UserManager;

import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_AMOUNT;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class FlySpeedCommandProvider extends AbstractCommandProvider {

    private static final float DEF_SPEED = 0.1F;
    private static final float MAX_SPEED = 1.0F;
    private static final int SPEEDS_AMOUNT = 10;

    // TODO Per speed permission

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("flyspeed");
    private static final Permission PERMISSION_OTHERS = EssentialPerms.COMMAND.permission("flyspeed.others");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.FlySpeed.Desc").text("Change fly speed.");

    private static final MessageLocale MESSAGE_SET_NOTIFY = LangEntry.builder("Command.FlySpeed.Done.Notify").chatMessage(
        GRAY.wrap("Your fly speed has been set to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + ".")
    );

    private static final MessageLocale MESSAGE_SET_FEEDBACK = LangEntry.builder("Command.FlySpeed.Done.Target").chatMessage(
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s fly speed to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + ".")
    );

    private final EssentialModule module;
    private final UserManager userManager;

    public FlySpeedCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("flyspeed", true, new String[]{"flyspeed"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(
                Arguments.integer(CommandArguments.VALUE, 1)
                    .suggestions((reader, context) -> IntStream.range(1, SPEEDS_AMOUNT + 1).boxed().map(String::valueOf).toList()),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::setFlySpeed)
        );
    }

    private boolean setFlySpeed(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            int speed = Math.clamp(arguments.getInt(CommandArguments.VALUE), 1, SPEEDS_AMOUNT);

            float realSpeed = DEF_SPEED + (MAX_SPEED - DEF_SPEED) * (speed - 1) / (SPEEDS_AMOUNT - 1);

            target.setFlySpeed(realSpeed);

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_SET_FEEDBACK, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(speed))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_SET_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(speed))
                );
            }
        });
    }
}
