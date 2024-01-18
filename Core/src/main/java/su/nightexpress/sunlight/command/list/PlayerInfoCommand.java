package su.nightexpress.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static su.nexmedia.engine.utils.Colors.*;

public class PlayerInfoCommand extends TargetCommand {

    public static final String NAME = "playerinfo";

    private static final String PLACEHOLDER_IP          = "%ip%";
    private static final String PLACEHOLDER_LAST_JOIN   = "%last_join%";
    private static final String PLACEHOLDER_IS_ONLINE   = "%is_online%";
    private static final String PLACEHOLDER_GAMEMODE    = "%game_mode%";
    private static final String PLACEHOLDER_CAN_FLY     = "%can_fly%";
    private static final String PLACEHOLDER_FOOD_LEVEL  = "%food_level%";
    private static final String PLACEHOLDER_SATURATION  = "%saturation%";
    private static final String PLACEHOLDER_HEALTH      = "%health%";
    private static final String PLACEHOLDER_MAX_HEALTH  = "%max_health%";

    private final List<String> formatOnline;

    public PlayerInfoCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_PLAYER_INFO, Perms.COMMAND_PLAYER_INFO, 0);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_PLAYER_INFO_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_PLAYER_INFO_USAGE));

        this.formatOnline = JOption.create("PlayerInfo.Format", Arrays.asList(
            LIGHT_YELLOW,
            LIGHT_YELLOW + "&lPlayer Info:",
            LIGHT_YELLOW,
            LIGHT_YELLOW + "▪ " + ORANGE + "Real Name: " + LIGHT_YELLOW + Placeholders.PLAYER_NAME,
            LIGHT_YELLOW + "▪ " + ORANGE + "Display Name: " + LIGHT_YELLOW + Placeholders.PLAYER_DISPLAY_NAME,
            LIGHT_YELLOW + "▪ " + ORANGE + "Online: " + LIGHT_YELLOW + PLACEHOLDER_IS_ONLINE,
            LIGHT_YELLOW + "▪ " + ORANGE + "Last Join: " + LIGHT_YELLOW + PLACEHOLDER_LAST_JOIN,
            LIGHT_YELLOW + "▪ " + ORANGE + "Last IP: " + LIGHT_YELLOW + PLACEHOLDER_IP,
            LIGHT_YELLOW,
            LIGHT_YELLOW + "▪ " + ORANGE + "Location: " + LIGHT_YELLOW + Placeholders.LOCATION_X + ", " + Placeholders.LOCATION_Y + ", " + Placeholders.LOCATION_Z + " in " + Placeholders.LOCATION_WORLD,
            LIGHT_YELLOW + "▪ " + ORANGE + "Game Mode: " + LIGHT_YELLOW + PLACEHOLDER_GAMEMODE,
            LIGHT_YELLOW + "▪ " + ORANGE + "Can Fly: " + LIGHT_YELLOW + PLACEHOLDER_CAN_FLY,
            LIGHT_YELLOW + "▪ " + ORANGE + "Food Level: " + LIGHT_YELLOW + PLACEHOLDER_FOOD_LEVEL,
            LIGHT_YELLOW + "▪ " + ORANGE + "Saturation: " + LIGHT_YELLOW + PLACEHOLDER_SATURATION,
            LIGHT_YELLOW + "▪ " + ORANGE + "Health: " + LIGHT_YELLOW + PLACEHOLDER_HEALTH + "/" + PLACEHOLDER_MAX_HEALTH,
            LIGHT_YELLOW),
            "Sets player info format.",
            "You can use " + EngineUtils.PLACEHOLDER_API + " here.",
            "JSON is supported: " + Placeholders.ENGINE_URL_LANG_JSON
        ).mapReader(Colorizer::apply).read(cfg);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        SunUser user = plugin.getUserManager().getUserData(target);

        List<String> format = new ArrayList<>(this.formatOnline);
        if (EngineUtils.hasPlaceholderAPI()) format.replaceAll(str -> PlaceholderAPI.setPlaceholders(target, str));

        PlaceholderMap placeholderMap = new PlaceholderMap()
            .add(PLACEHOLDER_IP, user::getIp)
            .add(PLACEHOLDER_LAST_JOIN, () -> TimeUtil.formatTimeLeft(System.currentTimeMillis(), user.getLastOnline()))
            .add(Placeholders.LOCATION_X, () -> NumberUtil.format(target.getLocation().getX()))
            .add(Placeholders.LOCATION_Y, () -> NumberUtil.format(target.getLocation().getY()))
            .add(Placeholders.LOCATION_Z, () -> NumberUtil.format(target.getLocation().getZ()))
            .add(Placeholders.LOCATION_WORLD, () -> LangManager.getWorld(target.getWorld()))
            .add(PLACEHOLDER_IS_ONLINE, () -> LangManager.getBoolean(target.isOnline()))
            .add(PLACEHOLDER_CAN_FLY, () -> LangManager.getBoolean(target.getAllowFlight()))
            .add(PLACEHOLDER_FOOD_LEVEL, () -> String.valueOf(target.getFoodLevel()))
            .add(PLACEHOLDER_SATURATION, () -> NumberUtil.format(target.getSaturation()))
            .add(PLACEHOLDER_MAX_HEALTH, () -> NumberUtil.format(EntityUtil.getAttribute(target, Attribute.GENERIC_MAX_HEALTH)))
            .add(PLACEHOLDER_HEALTH, () -> NumberUtil.format(target.getHealth()))
            .add(PLACEHOLDER_GAMEMODE, () -> plugin.getLangManager().getEnum(target.getGameMode()))
            .add(Placeholders.PLAYER_DISPLAY_NAME, target::getDisplayName)
            .add(Placeholders.PLAYER_NAME, target::getName)
            ;
        format.replaceAll(placeholderMap.replacer());
        format.forEach(line -> PlayerUtil.sendRichMessage(sender, line));
    }
}
