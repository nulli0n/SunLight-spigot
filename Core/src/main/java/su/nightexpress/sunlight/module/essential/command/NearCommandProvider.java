package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.module.essential.EssentialSettings;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.Direction;
import su.nightexpress.sunlight.SLUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class NearCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION_COMMAND = EssentialPerms.COMMAND.permission("near");
    private static final Permission PERMISSION_OTHERS  = EssentialPerms.COMMAND.permission("near.others");
    private static final Permission PERMISSION_EXCLUDE = EssentialPerms.COMMAND.permission("near.exclude");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Near.Desc").text("Show nearest players.");

    private static final MessageLocale MESSAGE_NOTHING_NOTIFY = LangEntry.builder("Command.Near.Nothing.Notify").chatMessage(
        GRAY.wrap("There are no players in a " + ORANGE.wrap(GENERIC_RADIUS) + " block radius.")
    );

    private static final MessageLocale MESSAGE_NOTHING_FEEDBACK = LangEntry.builder("Command.Near.Nothing.Feedback").chatMessage(
        GRAY.wrap("There are no players around " + WHITE.wrap(PLAYER_DISPLAY_NAME) + " in a " + ORANGE.wrap(GENERIC_RADIUS) + " block radius.")
    );

    private final EssentialModule   module;
    private final EssentialSettings settings;
    private final UserManager       userManager;

    public NearCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull EssentialSettings settings, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.settings = settings;
        this.userManager = userManager;
    }

    private record NearbyPlayer(@NotNull Player player, int distance, @NotNull Direction direction) {}

    @Override
    public void registerDefaults() {
        this.registerLiteral("near", true, new String[]{"near"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION)
            .permission(PERMISSION_COMMAND)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).optional().permission(PERMISSION_OTHERS))
            .executes(this::showNearbyPlayers)
        );
    }

    @NotNull
    private String formatEntry(@NotNull NearbyPlayer nearby) {
        return Replacer.create()
            .replace(forPlayerWithPAPI(nearby.player()))
            .replace(GENERIC_DISTANCE, () -> NumberUtil.format(nearby.distance()))
            .replace(GENERIC_DIRECTION, () -> Lang.DIRECTION.getLocalized(nearby.direction()))
            .apply(this.settings.nearEntryFormat.get());
    }

    private boolean showNearbyPlayers(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, source -> {
            Player executor = context.getPlayer();
            List<NearbyPlayer> nearbyPlayers = new ArrayList<>();

            World sourceWorld = source.getWorld();
            Location sourceLocation = source.getLocation();

            int radius = this.settings.nearRadius.get();
            int distanceLookup = radius * radius;
            boolean isOthers = context.getSender() != source;

            Players.getOnline().forEach(other -> {
                if (other == source) return;
                if (other.getWorld() != sourceWorld) return;
                if (other.hasPermission(PERMISSION_EXCLUDE)) return;
                if (executor != null && !executor.canSee(other)) return;

                Location location = other.getLocation();

                int delta = (int) location.distanceSquared(sourceLocation);
                if (delta > distanceLookup) return;

                Direction direction = SLUtils.getDirection(sourceLocation, location);

                int distance = (int) Math.sqrt(delta);

                nearbyPlayers.add(new NearbyPlayer(other, distance, direction));
            });

            if (nearbyPlayers.isEmpty()) {
                this.module.sendPrefixed(isOthers ? MESSAGE_NOTHING_FEEDBACK : MESSAGE_NOTHING_NOTIFY, context.getSender(), replacer -> replacer
                    .with(GENERIC_RADIUS, () -> String.valueOf(radius))
                    .with(CommonPlaceholders.PLAYER.resolver(source))
                );
                return;
            }

            String entries = nearbyPlayers.stream()
                .sorted(Comparator.comparingDouble(NearbyPlayer::distance))
                .map(this::formatEntry)
                .collect(Collectors.joining(BR));

            String text = String .join("\n", Replacer.create()
                .replace(GENERIC_AMOUNT, () -> String.valueOf(nearbyPlayers.size()))
                .replace(GENERIC_RADIUS, () -> String.valueOf(radius))
                .replace(GENERIC_ENTRY, entries)
                .replace(forPlayerWithPAPI(source))
                .apply(isOthers ? this.settings.nearFormatOthers.get() : this.settings.nearFormatNormal.get()));

            Players.sendMessage(context.getSender(), text);
        });
    }
}
