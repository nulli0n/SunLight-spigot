package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.permissions.Permission;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.BooleanLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLSharedConsts;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.module.essential.EssentialPlaceholders;
import su.nightexpress.sunlight.module.essential.EssentialSettings;
import su.nightexpress.sunlight.user.UserManager;

import java.net.InetAddress;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.RED;
import static su.nightexpress.sunlight.SLPlaceholders.forLocation;
import static su.nightexpress.sunlight.SLPlaceholders.forPlayerWithPAPI;

public class PlayerInfoCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION = EssentialPerms.COMMAND.permission("playerinfo");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.PlayerInfo.Desc").text(
        "Show player info.");

    private static final BooleanLocale STATUS = LangEntry.builder("Command.PlayerInfo.Status").bool(GREEN.wrap(
        "Online"), RED.wrap("Offline"));

    private final EssentialModule   module;
    private final EssentialSettings settings;
    private final UserManager       userManager;

    public PlayerInfoCommandProvider(@NonNull SunLightPlugin plugin, @NonNull EssentialModule module, @NonNull EssentialSettings settings, @NonNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.settings = settings;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("playerinfo", true, new String[]{"playerinfo", SLSharedConsts.COMMAND_PLAYER_INFO},
            builder -> builder
                .description(DESCRIPTION)
                .permission(PERMISSION)
                .withArguments(Arguments.playerName(CommandArguments.PLAYER))
                .executes(this::showPlayerInfo)
        );
    }

    private boolean showPlayerInfo(@NonNull CommandContext context, @NonNull ParsedArguments arguments) {
        this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager,
            (user, target) -> {
                Location location = target.getLocation();
                Replacer replacer = Replacer.create()
                    .replace(forPlayerWithPAPI(target))
                    .replace(forLocation(location))
                    .replace(EssentialPlaceholders.PLAYER_STATUS, () -> STATUS.get(user.isOnline()))
                    .replace(EssentialPlaceholders.PLAYER_INET_ADDRESS, () -> user.getLatestAddress().map(
                        InetAddress::getHostAddress).orElse("null"))
                    .replace(EssentialPlaceholders.PLAYER_LAST_SEEN, () -> TimeFormats.formatSince(user.getLastOnline(),
                        TimeFormatType.LITERAL))
                    .replace(EssentialPlaceholders.PLAYER_LEVEL, () -> NumberUtil.format(target.getLevel()))
                    .replace(EssentialPlaceholders.PLAYER_CAN_FLY, () -> CoreLang.STATE_YES_NO.get(target
                        .getAllowFlight()))
                    .replace(EssentialPlaceholders.PLAYER_FOOD_LEVEL, () -> NumberUtil.format(target.getFoodLevel()))
                    .replace(EssentialPlaceholders.PLAYER_SATURATION, () -> NumberUtil.format(target.getSaturation()))
                    .replace(EssentialPlaceholders.PLAYER_MAX_HEALTH, () -> NumberUtil.format(EntityUtil
                        .getAttributeValue(target, Attribute.MAX_HEALTH)))
                    .replace(EssentialPlaceholders.PLAYER_HEALTH, () -> NumberUtil.format(target.getHealth()))
                    .replace(EssentialPlaceholders.PLAYER_GAME_MODE, () -> Lang.GAME_MODE.getLocalized(target
                        .getGameMode()))
                    .replace(EssentialPlaceholders.PLAYER_VANISHED, () -> CoreLang.STATE_YES_NO.get(this.plugin
                        .vanishProvider().map(provider -> provider.isVanished(target)).orElse(false)));

                String text = String.join("\n", replacer.apply(this.settings.playerInfoFormat.get()));
                Players.sendMessage(context.getSender(), text);
            });
        return true;
    }
}
