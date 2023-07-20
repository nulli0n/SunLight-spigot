package su.nightexpress.sunlight.command;

import org.bukkit.GameMode;
import org.bukkit.inventory.EquipmentSlot;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.utils.Pair;
import su.nexmedia.engine.utils.PlayerRankMap;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.command.api.ChangeCommand;
import su.nightexpress.sunlight.command.enderchest.EnderchestClearCommand;
import su.nightexpress.sunlight.command.enderchest.EnderchestCommand;
import su.nightexpress.sunlight.command.enderchest.EnderchestOpenCommand;
import su.nightexpress.sunlight.command.ignore.IgnoreAddCommand;
import su.nightexpress.sunlight.command.ignore.IgnoreCommand;
import su.nightexpress.sunlight.command.ignore.IgnoreListCommand;
import su.nightexpress.sunlight.command.ignore.IgnoreRemoveCommand;
import su.nightexpress.sunlight.command.inventory.InventoryClearCommand;
import su.nightexpress.sunlight.command.inventory.InventoryCommand;
import su.nightexpress.sunlight.command.inventory.InventoryOpenCommand;
import su.nightexpress.sunlight.command.item.*;
import su.nightexpress.sunlight.command.list.*;
import su.nightexpress.sunlight.command.teleport.*;
import su.nightexpress.sunlight.command.time.TimeCommand;
import su.nightexpress.sunlight.command.time.TimeSetCommand;
import su.nightexpress.sunlight.module.homes.command.basic.*;
import su.nightexpress.sunlight.module.kits.command.kits.KitsCommand;
import su.nightexpress.sunlight.module.spawns.command.SpawnsCommand;
import su.nightexpress.sunlight.module.spawns.command.SpawnsCreateCommand;
import su.nightexpress.sunlight.module.spawns.command.SpawnsDeleteCommand;
import su.nightexpress.sunlight.module.spawns.command.SpawnsTeleportCommand;
import su.nightexpress.sunlight.module.warps.command.basic.*;
import su.nightexpress.sunlight.module.worlds.commands.main.*;

import java.util.*;

public class CommandConfig {

    public static final JOption<Map<String, List<Pair<String[], String[]>>>> SHORTCUTS = new JOption<Map<String, List<Pair<String[], String[]>>>>("Shortcuts",
        (cfg, path, def) -> {
            Map<String, List<Pair<String[], String[]>>> map = new HashMap<>();
            for (String shortcut: cfg.getSection(path)) {
                String[] shortcuts = shortcut.trim().split(",");
                String[] argsFull = StringUtil.oneSpace(cfg.getString(path + "." + shortcut, "").trim()).split(" ");
                if (argsFull.length <= 1) continue;

                String label = argsFull[0];
                String[] args = Arrays.copyOfRange(argsFull, 1, argsFull.length);
                //map.put(label, Pair.of(Arrays.asList(shortcuts), args));
                map.computeIfAbsent(label, k -> new ArrayList<>()).add(Pair.of(shortcuts, args));
            }
            return map;
        },
        () -> {
            Map<String, List<Pair<String[], String[]>>> map = new HashMap<>();
            map.put(TeleportCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"tpa,tpr,call"}, new String[]{TeleportRequestCommand.NAME}),
                Pair.of(new String[]{"tpi"}, new String[]{TeleportInviteCommand.NAME}),
                Pair.of(new String[]{"tploc"}, new String[]{TeleportLocationCommand.NAME}),
                Pair.of(new String[]{"tpyes"}, new String[]{TeleportAcceptCommand.NAME}),
                Pair.of(new String[]{"tpno"}, new String[]{TeleportDeclineCommand.NAME})
            ));
            map.put(GamemodeCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"gms"}, new String[]{GameMode.SURVIVAL.name().toLowerCase()}),
                Pair.of(new String[]{"gmc"}, new String[]{GameMode.CREATIVE.name().toLowerCase()}),
                Pair.of(new String[]{"gma"}, new String[]{GameMode.ADVENTURE.name().toLowerCase()}),
                Pair.of(new String[]{"gmsp"}, new String[]{GameMode.SPECTATOR.name().toLowerCase()})
            ));
            map.put(WeatherCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"rain"}, new String[]{WeatherCommand.WeatherType.STORM.name().toLowerCase()}),
                Pair.of(new String[]{"sun"}, new String[]{WeatherCommand.WeatherType.CLEAR.name().toLowerCase()}),
                Pair.of(new String[]{"thunder"}, new String[]{WeatherCommand.WeatherType.THUNDER.name().toLowerCase()})
            ));
            map.put(TimeCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"morning"}, new String[]{TimeSetCommand.NAME + " 0"}),
                Pair.of(new String[]{"day"}, new String[]{TimeSetCommand.NAME + " 6000"}),
                Pair.of(new String[]{"evening"}, new String[]{TimeSetCommand.NAME + " 12000"}),
                Pair.of(new String[]{"night"}, new String[]{TimeSetCommand.NAME + " 18000"})
            ));
            map.put(InventoryCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"clearinv"}, new String[]{InventoryClearCommand.NAME}),
                Pair.of(new String[]{"invsee"}, new String[]{InventoryOpenCommand.NAME})
            ));
            map.put(EnderchestCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"clearender"}, new String[]{EnderchestClearCommand.NAME}),
                Pair.of(new String[]{"endersee", "ec"}, new String[]{EnderchestOpenCommand.NAME})
            ));
            map.put(FireCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"ignite"}, new String[]{ChangeCommand.Mode.ADD.name().toLowerCase() + " 1200"})
            ));
            map.put(EquipCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"hat"}, new String[]{EquipmentSlot.HEAD.name().toLowerCase()})
            ));
            map.put(IgnoreCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"unignore"}, new String[]{IgnoreRemoveCommand.NAME}),
                Pair.of(new String[]{"addignore"}, new String[]{IgnoreAddCommand.NAME}),
                Pair.of(new String[]{"ignorelist"}, new String[]{IgnoreListCommand.NAME})
            ));
            map.put(ItemCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"more"}, new String[]{ItemAmountCommand.NAME + " 64"}),
                Pair.of(new String[]{"rename"}, new String[]{ItemNameCommand.NAME}),
                Pair.of(new String[]{"relore"}, new String[]{ItemLoreCommand.NAME}),
                Pair.of(new String[]{"fix"}, new String[]{ItemDamageCommand.NAME + " 0"})
            ));
            map.put(HomesCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"home"}, new String[]{HomesTeleportCommand.NAME}),
                Pair.of(new String[]{"sethome"}, new String[]{HomesSetCommand.NAME}),
                Pair.of(new String[]{"delhome"}, new String[]{HomesDeleteCommand.NAME}),
                Pair.of(new String[]{"homelist"}, new String[]{HomesListCommand.NAME}),
                Pair.of(new String[]{"visithome"}, new String[]{HomesVisitCommand.NAME})
            ));
            map.put(SpawnsCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"spawn"}, new String[]{SpawnsTeleportCommand.NAME}),
                Pair.of(new String[]{"setspawn"}, new String[]{SpawnsCreateCommand.NAME}),
                Pair.of(new String[]{"delspawn"}, new String[]{SpawnsDeleteCommand.NAME})
            ));
            map.put(WarpsCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"warp"}, new String[]{WarpsTeleportCommand.NAME}),
                Pair.of(new String[]{"setwarp"}, new String[]{WarpsCreateCommand.NAME}),
                Pair.of(new String[]{"delwarp"}, new String[]{WarpsDeleteCommand.NAME}),
                Pair.of(new String[]{"warplist"}, new String[]{WarpsListCommand.NAME})
            ));
            map.put(KitsCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"kit"}, new String[]{"get"}),
                Pair.of(new String[]{"kitlist"}, new String[]{"list"})
            ));
            map.put(WorldsCommand.NAME, Arrays.asList(
                Pair.of(new String[]{"createworld"}, new String[]{CreateSubCommand.NAME}),
                Pair.of(new String[]{"deleteworld"}, new String[]{DeleteSubCommand.NAME}),
                Pair.of(new String[]{"loadworld"}, new String[]{LoadSubCommand.NAME}),
                Pair.of(new String[]{"unloadworld"}, new String[]{UnloadSubCommand.NAME})
            ));
            return map;
        },
        "Here you can create your own shortcuts for the SunLight main and module's sub-commands.",
        "Shortcut syntax: 'shortcut1,shortcut2,etc: command arg1 arg2 etc'",
        "Example #1: 'call,tpa: teleport request' will create two shortcuts: '/tpa' and '/call' for the '/teleport request' command.",
        "Example #2: 'gmc: gamemode creative' will create '/gmc' shortcut for '/gamemode creative' command."
    ).setWriter((cfg, path, map) -> map.forEach((label, list) -> {
            list.forEach(pair -> {
                String shortcuts = String.join(",", pair.getFirst());
                String args = String.join(" ", pair.getSecond());

                cfg.set(path + "." + shortcuts, label + " " + args);
            });
        }));

    public static final JOption<Boolean> UNREGISTER_CONFLICTS = JOption.create("Unregister_Conflicts", false,
        "Sets whether commands with similar names/aliases should be unregistered from the server",
        "if they're overlaps with the SunLight commands.",
        "Warning: Settings this on 'true' may result both positive and negative effects.");

    public static final JOption<Set<String>> DISABLED = JOption.create("Disabled", Set.of(),
        "A list of SunLight (!) commands that won't be registered into the server at all.",
        "You can put here any command alias from the 'Aliases' section.");

    public static final JOption<Map<String, CommandCooldown>> COOLDOWNS = JOption.forMap("Cooldowns",
        (cfg, path, key) -> CommandCooldown.read(cfg, path + "." + key, key),
        () -> {
            List<String[]> args1 = new ArrayList<>();
            args1.add(new String[]{"teleport", "tp"});

            return Map.of(
                "heal", new CommandCooldown("heal", HealCommand.NAME, Collections.emptyList(), new PlayerRankMap<>(Map.of("vip", 60, "gold", 30))),
                "feed", new CommandCooldown("feed", FeedCommand.NAME, Collections.emptyList(), new PlayerRankMap<>(Map.of("vip", 60, "gold", 30))),
                "home_tp", new CommandCooldown("home_tp", HomesCommand.NAME, args1, new PlayerRankMap<>(Map.of("default", 30, "vip", 10)))
            );
        },
        "Here you can create custom cooldowns for ALL server commands including command arguments.",
        "===== Options Description =====",
        "[Command] - This is command name to add cooldown for. It will auto-detect all its aliases and will work for all of them.",
        "[Arguments] - List of additional arguments to be checked in command line. Each line = new argument.",
        "        You can provide multiple arguments on the same line (split them with commas).",
        "        It can be useful if argument has aliases like: '/home teleport' and '/home tp', where 'teleport' and 'tp' does the same thing.",
        "        If arguments amount is greater than in executed command, the cooldown will be skipped.",
        "        If arguments amount is smaller or equals to arguments in executed command, the cooldown will be applied.",
        "[Cooldown] - Rank-based cooldown (in seconds) before player can use this command again.",
        "        If player has multiple ranks, the SMALLEST cooldown will be used.",
        "        You can put cooldown as '-1' to make command one-timed.",
        "        Set cooldown to 0 to disable it for certain rank(s)."
    ).setWriter((cfg, path, map) -> map.forEach((id, cd) -> cd.write(cfg, path + "." + id)));
}
