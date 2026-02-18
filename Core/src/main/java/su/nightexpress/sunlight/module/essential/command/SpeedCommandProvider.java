package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Sound;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
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

public class SpeedCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION        = EssentialPerms.COMMAND.permission("speed");
    private static final Permission PERMISSION_OTHERS = EssentialPerms.COMMAND.permission("speed.others");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Speed.Desc").text("Change walk speed.");

    private static final MessageLocale MESSAGE_SET_SPEED_NOTIFY = LangEntry.builder("Command.Speed.Done.Notify").chatMessage(
        Sound.ITEM_FIRECHARGE_USE,
        GRAY.wrap("Your walk speed has been changed to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + ".")
    );

    private static final MessageLocale MESSAGE_SET_SPEED_FEEDBACK = LangEntry.builder("Command.Speed.Done.Target").chatMessage(
        Sound.ITEM_FIRECHARGE_USE,
        GRAY.wrap("You have set " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s walk speed to " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + ".")
    );

    private static final float DEF_SPEED     = 0.2F;
    private static final float MAX_SPEED     = 1.0F;
    private static final int   SPEEDS_AMOUNT = 10;

    private final EssentialModule module;
    private final UserManager userManager;

    public SpeedCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("speed", true, new String[]{"speed", "walkspeed"}, builder -> builder
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(
                Arguments.integer(CommandArguments.VALUE, 1)
                    .suggestions((reader, tabContext) -> IntStream.range(1, SPEEDS_AMOUNT + 1).boxed().map(String::valueOf).toList()),
                Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_OTHERS).optional()
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::changeSpeed)
        );
    }

    private boolean changeSpeed(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            int speed = Math.clamp(arguments.getInt(CommandArguments.VALUE), 1, SPEEDS_AMOUNT);
            float realSpeed = DEF_SPEED + (MAX_SPEED - DEF_SPEED) * (speed - 1) / (SPEEDS_AMOUNT - 1);

            target.setWalkSpeed(realSpeed);

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_SET_SPEED_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(GENERIC_AMOUNT, () -> String.valueOf(speed))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_SET_SPEED_NOTIFY, target, replacer -> replacer
                    .with(GENERIC_AMOUNT, () -> String.valueOf(speed))
                );
            }
        });
    }
}
