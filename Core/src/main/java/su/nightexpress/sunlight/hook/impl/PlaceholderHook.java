package su.nightexpress.sunlight.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Config;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.core.cooldown.CooldownInfo;
import su.nightexpress.sunlight.core.cooldown.CooldownType;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.afk.AfkModule;
import su.nightexpress.sunlight.module.afk.AfkState;
import su.nightexpress.sunlight.module.afk.config.AfkConfig;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.extras.chairs.ChairsManager;
import su.nightexpress.sunlight.module.extras.chestsort.SortManager;
import su.nightexpress.sunlight.module.godmode.GodModule;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.nerfphantoms.PhantomsModule;
import su.nightexpress.sunlight.module.ptp.PTPModule;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.spawns.SpawnsModule;
import su.nightexpress.sunlight.module.vanish.VanishModule;
import su.nightexpress.sunlight.module.warps.WarpsModule;
import su.nightexpress.sunlight.module.warps.impl.Warp;
import su.nightexpress.sunlight.module.worlds.WorldsModule;
import su.nightexpress.sunlight.module.worlds.impl.WorldData;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PlaceholderHook {

    private static Expansion expansion;

    public static void setup(@NotNull SunLightPlugin plugin) {
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

        private final SunLightPlugin plugin;

        public Expansion(@NotNull SunLightPlugin plugin) {
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
            return plugin.getDescription().getAuthors().getFirst();
        }

        @Override
        @NotNull
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(Player player, @NotNull String params) {
            if (player == null) return null;

            String[] split = params.split("_");
            String prefix = split[0];
            String subParams = split.length >= 2 ? Arrays.stream(split).skip(1).collect(Collectors.joining("_")) : prefix;

            SunUser user = plugin.getUserManager().getUserData(player);

            if (prefix.equalsIgnoreCase("afk")) {
                AfkModule module = this.plugin.getModuleManager().getModule(AfkModule.class).orElse(null);
                if (module == null) return null;

                AfkState state = module.getState(player);
                if (state == null) return "?";

                if (subParams.equalsIgnoreCase("state")) {
                    return Lang.getYesOrNo(state.isAfk());
                }
                if (subParams.equalsIgnoreCase("mode")) {
                    String placeholder = state.isAfk() ? AfkConfig.AFK_PLACEHOLDER_IN.get() : AfkConfig.AFK_PLACEHOLDER_OUT.get();
                    return placeholder.replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(state.getIdleTime() * 1000L));
                }
                if (subParams.equalsIgnoreCase("idle_time")) {
                    return NumberUtil.format(state.getIdleTime());
                }
                if (subParams.equalsIgnoreCase("idle_time_formatted")) {
                    return TimeUtil.formatTime(state.getIdleTime() * 1000L);
                }
                if (subParams.equalsIgnoreCase("since")) {
                    String placeholder = state.isAfk() ? AfkConfig.AFK_PLACEHOLDER_IN.get() : AfkConfig.AFK_PLACEHOLDER_OUT.get();
                    return placeholder.replace(Placeholders.GENERIC_TIME, TimeUtil.formatTime(System.currentTimeMillis() - state.getAfkSince()));
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

                if (subParams.equalsIgnoreCase("limit")) {
                    int limit = module.getHomesMaxAmount(player);
                    return limit >= 0 ? NumberUtil.format(limit) : Lang.OTHER_INFINITY.getString();
                }
                if (subParams.equalsIgnoreCase("amount")) {
                    return NumberUtil.format(module.getHomesAmount(player));
                }
                if (subParams.equalsIgnoreCase("respawn_home")) {
                    Home home = module.getRespawnHome(player);
                    return home != null ? home.getName() : "-";
                }
                if (subParams.equalsIgnoreCase("default_home")) {
                    Home home = module.getDefaultHome(player);
                    return home != null ? home.getName() : "-";
                }
                if (subParams.equalsIgnoreCase("can_set")) {
                    return Lang.getYesOrNo(module.checkLocation(player, player.getLocation(), false));
                }
            }
            else if (prefix.equalsIgnoreCase("kits")) {
                KitsModule module = this.plugin.getModuleManager().getModule(KitsModule.class).orElse(null);
                if (module == null) return null;

                Kit kit = this.getKit(module, subParams, "is_on_cooldown_");
                if (kit != null) return Lang.getYesOrNo(kit.isOnCooldown(player));

                kit = this.getKit(module, subParams, "cooldown_raw_");
                if (kit != null) return String.valueOf(user.getCooldown(kit).map(CooldownInfo::getExpireDate).orElse(0L));

                kit = this.getKit(module, subParams, "cooldown_");
                if (kit != null) return user.getCooldown(kit).map(c -> TimeUtil.formatDuration(c.getExpireDate())).orElse("-");

                kit = this.getKit(module, subParams, "is_available_");
                if (kit != null) return Lang.getYesOrNo(kit.isAvailable(player));

                kit = this.getKit(module, subParams, "can_afford_");
                if (kit != null) return Lang.getYesOrNo(kit.canAfford(player));

                kit = this.getKit(module, subParams, "has_permission_");
                if (kit != null) return Lang.getYesOrNo(kit.hasPermission(player));
            }
            else if (prefix.equalsIgnoreCase("scoreboard")) {
                ScoreboardModule module = this.plugin.getModuleManager().getModule(ScoreboardModule.class).orElse(null);
                if (module == null) return null;

                if (subParams.equalsIgnoreCase("state")) {
                    return Lang.getYesOrNo(module.isScoreboardEnabled(player));
                }
            }
            else if (prefix.equalsIgnoreCase("spawns")) {
                SpawnsModule module = this.plugin.getModuleManager().getModule(SpawnsModule.class).orElse(null);
                if (module == null) return null;

            }
            else if (prefix.equalsIgnoreCase("warps")) {
                WarpsModule module = this.plugin.getModuleManager().getModule(WarpsModule.class).orElse(null);
                if (module == null) return null;

                if (subParams.equalsIgnoreCase("limit")) {
                    int limit = module.getAllowedWarpsAmount(player);
                    return limit >= 0 ? NumberUtil.format(limit) : Lang.OTHER_INFINITY.getString();
                }
                if (subParams.equalsIgnoreCase("amount")) {
                    return NumberUtil.format(module.getOwnedWarpsAmount(player));
                }

                Warp warp = this.getWarp(module, subParams, "is_on_cooldown_");
                if (warp != null) return Lang.getYesOrNo(warp.isOnCooldown(player));

                warp = this.getWarp(module, subParams, "cooldown_raw_");
                if (warp != null) return String.valueOf(user.getCooldown(warp).map(CooldownInfo::getExpireDate).orElse(0L));

                warp = this.getWarp(module, subParams, "cooldown_");
                if (warp != null) return user.getCooldown(warp).map(c -> TimeUtil.formatDuration(c.getExpireDate())).orElse("-");

                warp = this.getWarp(module, subParams, "is_available_");
                if (warp != null) return Lang.getYesOrNo(warp.isAvailable(player));

                warp = this.getWarp(module, subParams, "can_afford_");
                if (warp != null) return Lang.getYesOrNo(warp.canAffordVisit(player));

                warp = this.getWarp(module, subParams, "has_permission_");
                if (warp != null) return Lang.getYesOrNo(warp.hasPermission(player));
            }
            else if (prefix.equalsIgnoreCase("worlds")) {
                WorldsModule module = this.plugin.getModuleManager().getModule(WorldsModule.class).orElse(null);
                if (module == null) return null;

                WorldData world = this.getWorld(module, subParams, "autowipe_next_date_");
                if (world != null) return Config.GENERAL_DATE_FORMAT.get().format(world.getNextWipe());

                world = this.getWorld(module, subParams, "autowipe_timelft_");
                if (world != null) return TimeUtil.formatDuration(world.getNextWipe());

                world = this.getWorld(module, subParams, "autowipe_latest_date_");
                if (world != null) return Config.GENERAL_DATE_FORMAT.get().format(world.getLastResetDate());

                world = this.getWorld(module, subParams, "autowipe_latest_since_");
                if (world != null) return TimeUtil.formatDuration(world.getLastResetDate(), System.currentTimeMillis());
            }
            else {
                if (params.equalsIgnoreCase("god_state")) {
                    return Lang.getYesOrNo(user.getSettings().get(GodModule.GOD_MODE));
                }
                if (params.equalsIgnoreCase("foodgod_state")) {
                    return Lang.getYesOrNo(user.getSettings().get(GodModule.FOOD_GOD));
                }
                if (params.equalsIgnoreCase("teleport_requests_state")) {
                    return Lang.getYesOrNo(user.getSettings().get(PTPModule.TELEPORT_REQUESTS));
                }
                if (params.equalsIgnoreCase("nophantom_state")) {
                    return Lang.getYesOrNo(user.getSettings().get(PhantomsModule.ANTI_PHANTOM));
                }
                if (params.equalsIgnoreCase("vanish_state")) {
                    return Lang.getYesOrNo(user.getSettings().get(VanishModule.VANISH));
                }
                if (params.equalsIgnoreCase("chairs_state")) {
                    return Lang.getYesOrNo(user.getSettings().get(ChairsManager.SETTING_CHAIRS));
                }
                if (params.equalsIgnoreCase("chestsort_state")) {
                    return Lang.getYesOrNo(user.getSettings().get(SortManager.SETTING_CHEST_SORT));
                }
                if (params.equalsIgnoreCase("nick")) {
                    return user.hasCustomName() ? user.getCustomName() : user.getName();
                }

                if (params.startsWith("world_name_localized_")) {
                    String worldRaw = params.substring("name_localized_".length());
                    World world = this.plugin.getServer().getWorld(worldRaw);
                    return world == null ? null : LangAssets.get(world);
                }
                if (params.startsWith("world_name_localized")) {
                    return LangAssets.get(player.getWorld());
                }
                if (params.startsWith("command_is_on_cooldown_")) {
                    String name = params.substring("command_is_on_cooldown_".length());
                    return Lang.getYesOrNo(user.getCooldown(CooldownType.COMMAND, name).isPresent());
                }
                if (params.startsWith("command_cooldown_")) {
                    String name = params.substring("command_cooldown_".length());
                    return user.getCooldown(CooldownType.COMMAND, name).map(c -> TimeUtil.formatDuration(c.getExpireDate())).orElse("-");
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
                return module.getWarp(warpid);
            }
            return null;
        }

        @Nullable
        private WorldData getWorld(@NotNull WorldsModule module, @NotNull String subParams, @NotNull String placeholder) {
            if (subParams.startsWith(placeholder)) {
                String name = subParams.substring(placeholder.length());
                return module.getWorldData(name);
            }
            return null;
        }
    }
}
