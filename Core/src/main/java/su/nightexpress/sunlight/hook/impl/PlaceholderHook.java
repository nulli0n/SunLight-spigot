package su.nightexpress.sunlight.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownInfo;
import su.nightexpress.sunlight.data.impl.cooldown.CooldownType;
import su.nightexpress.sunlight.data.impl.settings.DefaultSettings;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.afk.config.AfkConfig;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.extras.impl.chairs.ChairsManager;
import su.nightexpress.sunlight.module.extras.impl.chestsort.SortManager;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.worlds.WorldsModule;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PlaceholderHook {

    private static Expansion expansion;

    public static void setup(@NotNull SunLight plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class Expansion extends PlaceholderExpansion {

        private final SunLight plugin;

        public Expansion(@NotNull SunLight plugin) {
            this.plugin = plugin;
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return plugin.getName().toLowerCase();
        }

        @Override
        @NotNull
        public String getAuthor() {
            return plugin.getDescription().getAuthors().get(0);
        }

        @Override
        @NotNull
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            if (player == null) return null;

            String[] split = params.split("_");
            String prefix = split[0];
            String rest = split.length >= 2 ? Arrays.stream(split).skip(1).collect(Collectors.joining("_")) : prefix;

            SunUser user = plugin.getUserManager().getUserData(player);

            if (prefix.equalsIgnoreCase("afk")) {
                AfkModule module = this.plugin.getModuleManager().getModule(AfkModule.class).orElse(null);
                if (module == null) return null;

                if (rest.equalsIgnoreCase("state")) {
                    return LangManager.getBoolean(module.isAfk(player));
                }
                if (rest.equalsIgnoreCase("mode")) {
                    String placeholder = AfkModule.isAfk(user) ? AfkConfig.AFK_PLACEHOLDER_IN.get() : AfkConfig.AFK_PLACEHOLDER_OUT.get();
                    return placeholder.replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(AfkModule.getIdleTime(user) * 1000L));
                }
                if (rest.equalsIgnoreCase("idle_time")) {
                    return NumberUtil.format(AfkModule.getIdleTime(user));
                }
                if (rest.equalsIgnoreCase("idle_time_formatted")) {
                    return TimeUtil.formatTime(AfkModule.getIdleTime(user) * 1000L);
                }
                if (rest.equalsIgnoreCase("since")) {
                    if (module.isAfk(player)) {
                        return TimeUtil.formatTime(System.currentTimeMillis() - AfkModule.getAfkSince(user));
                    }
                    else return "-";
                }
            }
            else if (prefix.equalsIgnoreCase("bans")) {
                BansModule module = this.plugin.getModuleManager().getModule(BansModule.class).orElse(null);
                if (module == null) return null;

            }
            else if (prefix.equalsIgnoreCase("chat")) {
                ChatModule module = this.plugin.getModuleManager().getModule(ChatModule.class).orElse(null);
                if (module == null) return null;

            }
            else if (prefix.equalsIgnoreCase("homes")) {
                HomesModule module = this.plugin.getModuleManager().getModule(HomesModule.class).orElse(null);
                if (module == null) return null;

                if (rest.equalsIgnoreCase("limit")) {
                    int limit = module.getHomesMaxAmount(player);
                    return limit >= 0 ? NumberUtil.format(limit) : LangManager.getPlain(Lang.OTHER_INFINITY);
                }
                if (rest.equalsIgnoreCase("amount")) {
                    return NumberUtil.format(module.getHomesAmount(player));
                }
                if (rest.equalsIgnoreCase("respawn_home")) {
                    return module.getHomeToRespawn(player).map(Home::getName).orElse("-");
                }
                if (rest.equalsIgnoreCase("default_home")) {
                    return module.getHomeDefault(player).map(Home::getName).orElse("-");
                }
                if (rest.equalsIgnoreCase("can_set")) {
                    return LangManager.getBoolean(module.canSetHome(player, player.getLocation(), false));
                }
            }
            else if (prefix.equalsIgnoreCase("kits")) {
                KitsModule module = this.plugin.getModuleManager().getModule(KitsModule.class).orElse(null);
                if (module == null) return null;

                Kit kit = this.getKit(module, rest, "is_on_cooldown_");
                if (kit != null) return LangManager.getBoolean(kit.isOnCooldown(player));

                kit = this.getKit(module, rest, "cooldown_raw_");
                if (kit != null) return String.valueOf(user.getCooldown(kit).map(CooldownInfo::getExpireDate).orElse(0L));

                kit = this.getKit(module, rest, "cooldown_");
                if (kit != null) return user.getCooldown(kit).map(c -> TimeUtil.formatTimeLeft(c.getExpireDate())).orElse("-");

                kit = this.getKit(module, rest, "is_available_");
                if (kit != null) return LangManager.getBoolean(kit.isAvailable(player));

                kit = this.getKit(module, rest, "can_afford_");
                if (kit != null) return LangManager.getBoolean(kit.canAfford(player));

                kit = this.getKit(module, rest, "has_permission_");
                if (kit != null) return LangManager.getBoolean(kit.hasPermission(player));
            }
            else if (prefix.equalsIgnoreCase("scoreboard")) {
                ScoreboardModule module = this.plugin.getModuleManager().getModule(ScoreboardModule.class).orElse(null);
                if (module == null) return null;

                if (rest.equalsIgnoreCase("state")) {
                    return LangManager.getBoolean(module.isScoreboardEnabled(player));
                }
            }
            else if (prefix.equalsIgnoreCase("spawns")) {
                SpawnsModule module = this.plugin.getModuleManager().getModule(SpawnsModule.class).orElse(null);
                if (module == null) return null;

            }
            else if (prefix.equalsIgnoreCase("warps")) {
                WarpsModule module = this.plugin.getModuleManager().getModule(WarpsModule.class).orElse(null);
                if (module == null) return null;

                if (rest.equalsIgnoreCase("limit")) {
                    int limit = module.getWarpsMaxAmount(player);
                    return limit >= 0 ? NumberUtil.format(limit) : LangManager.getPlain(Lang.OTHER_INFINITY);
                }
                if (rest.equalsIgnoreCase("amount")) {
                    return NumberUtil.format(module.getWarpsCreatedAmount(player));
                }

                Warp warp = this.getWarp(module, rest, "is_on_cooldown_");
                if (warp != null) return LangManager.getBoolean(warp.isOnCooldown(player));

                warp = this.getWarp(module, rest, "cooldown_raw_");
                if (warp != null) return String.valueOf(user.getCooldown(warp).map(CooldownInfo::getExpireDate).orElse(0L));

                warp = this.getWarp(module, rest, "cooldown_");
                if (warp != null) return user.getCooldown(warp).map(c -> TimeUtil.formatTimeLeft(c.getExpireDate())).orElse("-");

                warp = this.getWarp(module, rest, "is_available_");
                if (warp != null) return LangManager.getBoolean(warp.isAvailable(player));

                warp = this.getWarp(module, rest, "can_afford_");
                if (warp != null) return LangManager.getBoolean(warp.canAffordVisit(player));

                warp = this.getWarp(module, rest, "has_permission_");
                if (warp != null) return LangManager.getBoolean(warp.hasPermission(player));
            }
            else if (prefix.equalsIgnoreCase("worlds")) {
                WorldsModule module = this.plugin.getModuleManager().getModule(WorldsModule.class).orElse(null);
                return null;
            }
            else {
                if (params.equalsIgnoreCase("god_state")) {
                    return LangManager.getBoolean(user.getSettings().get(DefaultSettings.GOD_MODE));
                }
                if (params.equalsIgnoreCase("foodgod_state")) {
                    return LangManager.getBoolean(user.getSettings().get(DefaultSettings.FOOD_GOD));
                }
                if (params.equalsIgnoreCase("teleport_requests_state")) {
                    return LangManager.getBoolean(user.getSettings().get(DefaultSettings.TELEPORT_REQUESTS));
                }
                if (params.equalsIgnoreCase("nophantom_state")) {
                    return LangManager.getBoolean(user.getSettings().get(DefaultSettings.ANTI_PHANTOM));
                }
                if (params.equalsIgnoreCase("vanish_state")) {
                    return LangManager.getBoolean(user.getSettings().get(DefaultSettings.VANISH));
                }
                if (params.equalsIgnoreCase("chairs_state")) {
                    return LangManager.getBoolean(user.getSettings().get(ChairsManager.SETTING_CHAIRS));
                }
                if (params.equalsIgnoreCase("chestsort_state")) {
                    return LangManager.getBoolean(user.getSettings().get(SortManager.SETTING_CHEST_SORT));
                }
                if (params.equalsIgnoreCase("nick")) {
                    return user.getCustomName().orElse(user.getName());
                }

                if (params.startsWith("world_name_localized_")) {
                    String worldRaw = params.substring("name_localized_".length());
                    World world = this.plugin.getServer().getWorld(worldRaw);
                    return world == null ? null : LangManager.getWorld(world);
                }
                if (params.startsWith("world_name_localized")) {
                    return LangManager.getWorld(player.getWorld());
                }
                if (params.startsWith("command_is_on_cooldown_")) {
                    String name = params.substring("command_is_on_cooldown_".length());
                    return LangManager.getBoolean(user.getCooldown(CooldownType.COMMAND, name).isPresent());
                }
                if (params.startsWith("command_cooldown_")) {
                    String name = params.substring("command_cooldown_".length());
                    return user.getCooldown(CooldownType.COMMAND, name).map(c -> TimeUtil.formatTimeLeft(c.getExpireDate())).orElse("-");
                }
            }

            return super.onPlaceholderRequest(player, params);
        }

        @Nullable
        private Kit getKit(@NotNull KitsModule module, @NotNull String rest, @NotNull String placeholder) {
            if (rest.startsWith(placeholder)) {
                String kitId = rest.substring(placeholder.length());
                return module.getKitById(kitId);
            }
            return null;
        }

        @Nullable
        private Warp getWarp(@NotNull WarpsModule module, @NotNull String rest, @NotNull String placeholder) {
            if (rest.startsWith(placeholder)) {
                String warpid = rest.substring(placeholder.length());
                return module.getWarpById(warpid);
            }
            return null;
        }
    }
}
