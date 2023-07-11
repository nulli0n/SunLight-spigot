package su.nightexpress.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.teleport.TeleportCommand;
import su.nightexpress.sunlight.command.teleport.TeleportRequestCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NearCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "near";

    private final int          radius;
    private final List<String> format;

    public NearCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_NEAR);
        this.setDescription(plugin.getMessage(Lang.COMMAND_NEAR_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_NEAR_USAGE));
        this.setPlayerOnly(true);

        this.radius = JOption.create("Near.Radius", 100,
            "Sets the block radius to look for players in.").read(cfg);

        this.format = JOption.create("Near.Format",
            List.of(
                "#ffeea2",
                "#ffeea2Players in #fdba5e+" + Placeholders.GENERIC_RADIUS + " #ffeea2blocks from you:",
                "#ffeea2",
                "#ffeea2▪ #fdba5e" + Placeholders.PLAYER_NAME + ": #ffeea2" + Placeholders.GENERIC_AMOUNT + " blocks away <? show_text:\"#ddeceeClick to send teleport request.\" run_command:\"/" + TeleportCommand.NAME + " " + TeleportRequestCommand.NAME + " " + Placeholders.PLAYER_NAME + "\" ?>#aefd5e[TPA]</>",
                "#ffeea2"
            ),
            "List format for nearby players.",
            "You can use " + EngineUtils.PLACEHOLDER_API + " here.",
            "JSON is also supported: " + Placeholders.ENGINE_URL_LANG_JSON).mapReader(Colorizer::apply).read(cfg);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = (Player) sender;

        Map<Player, Integer> listNear = new HashMap<>();
        Location location = player.getLocation();

        player.getNearbyEntities(radius, radius, radius).forEach(entity -> {
            if (!(entity instanceof Player pNear)) return;
            if (!player.canSee(pNear)) return;

            listNear.put(pNear, (int) location.distance(pNear.getLocation()));
        });

        if (listNear.isEmpty()) {
            plugin.getMessage(Lang.COMMAND_NEAR_ERROR_NOTHING).replace(Placeholders.GENERIC_RADIUS, this.radius).send(sender);
            return;
        }

        for (String line : this.format) {
            if (line.contains(Placeholders.PLAYER_NAME)) {
                listNear.forEach((nearPlayer, dist) -> {
                    String name = nearPlayer.getName();
                    String line2 = line
                        .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(dist))
                        .replace(Placeholders.PLAYER_NAME, name);

                    if (EngineUtils.hasPlaceholderAPI()) {
                        line2 = PlaceholderAPI.setPlaceholders(nearPlayer, line2);
                    }
                    PlayerUtil.sendRichMessage(sender, line2);
                });
                continue;
            }
            PlayerUtil.sendRichMessage(sender, line.replace(Placeholders.GENERIC_RADIUS, String.valueOf(radius)));
        }
    }
}
