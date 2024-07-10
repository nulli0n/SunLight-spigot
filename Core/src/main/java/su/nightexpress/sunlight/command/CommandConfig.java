package su.nightexpress.sunlight.command;

import su.nightexpress.nightcore.config.ConfigValue;

public class CommandConfig {

    public static final ConfigValue<Boolean> UNREGISTER_CONFLICTS = ConfigValue.create("Unregister_Conflicts",
        false,
        "Sets whether commands with similar names/aliases should be unregistered from the server",
        "if they're overlaps with the SunLight commands.",
        "Warning: Settings this on 'true' may result both positive and negative effects."
    );

//    public static final ConfigValue<Map<String, CommandCooldown>> COOLDOWNS = ConfigValue.forMap("Cooldowns",
//        (cfg, path, key) -> CommandCooldown.read(cfg, path + "." + key, key),
//        (cfg, path, map) -> map.forEach((id, cd) -> cd.write(cfg, path + "." + id)),
//        () -> {
//            List<String[]> args1 = new ArrayList<>();
//            args1.add(new String[]{"teleport", "tp"});
//
//            return Map.of(
//                // TODO "heal", new CommandCooldown("heal", HealCommand.NAME, Collections.emptyList(), new RankMap<>(RankMap.Mode.RANK, "command.cooldown.", 0, Map.of("vip", 60, "gold", 30))),
//                //"feed", new CommandCooldown("feed", FeedCommand.NAME, Collections.emptyList(), new RankMap<>(RankMap.Mode.RANK, "command.cooldown.", 0, Map.of("vip", 60, "gold", 30))),
//                //"home_tp", new CommandCooldown("home_tp", HomesCommand.NAME, args1, new RankMap<>(RankMap.Mode.RANK, "command.cooldown.", 0, Map.of("default", 30, "vip", 10)))
//            );
//        },
//        "Here you can create custom cooldowns for ALL server commands including command arguments.",
//        "===== Options Description =====",
//        "[Command] - This is command name to add cooldown for. It will auto-detect all its aliases and will work for all of them.",
//        "[Arguments] - List of additional arguments to be checked in command line. Each line = new argument.",
//        "        You can provide multiple arguments on the same line (split them with commas).",
//        "        It can be useful if argument has aliases like: '/home teleport' and '/home tp', where 'teleport' and 'tp' does the same thing.",
//        "        If arguments amount is greater than in executed command, the cooldown will be skipped.",
//        "        If arguments amount is smaller or equals to arguments in executed command, the cooldown will be applied.",
//        "[Cooldown] - Rank-based cooldown (in seconds) before player can use this command again.",
//        "        If player has multiple ranks, the SMALLEST cooldown will be used.",
//        "        You can put cooldown as '-1' to make command one-timed.",
//        "        Set cooldown to 0 to disable it for certain rank(s)."
//    );
}
