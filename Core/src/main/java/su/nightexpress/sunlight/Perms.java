package su.nightexpress.sunlight;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.server.JPermission;
import su.nightexpress.sunlight.command.enderchest.*;
import su.nightexpress.sunlight.command.ignore.IgnoreCommand;
import su.nightexpress.sunlight.command.inventory.*;
import su.nightexpress.sunlight.command.item.*;
import su.nightexpress.sunlight.command.list.*;
import su.nightexpress.sunlight.command.mob.MobCommand;
import su.nightexpress.sunlight.command.mob.MobKillCommand;
import su.nightexpress.sunlight.command.mob.MobSpawnCommand;
import su.nightexpress.sunlight.command.nick.NickChangeCommand;
import su.nightexpress.sunlight.command.nick.NickClearCommand;
import su.nightexpress.sunlight.command.nick.NickCommand;
import su.nightexpress.sunlight.command.nick.NickSetCommand;
import su.nightexpress.sunlight.command.teleport.*;
import su.nightexpress.sunlight.command.time.TimeCommand;
import su.nightexpress.sunlight.command.time.TimePersonalCommand;
import su.nightexpress.sunlight.command.time.TimeSetCommand;
import su.nightexpress.sunlight.command.time.TimeShowCommand;
import su.nightexpress.sunlight.module.chat.command.BroadcastCommand;

public class Perms {

    public static final String PREFIX         = "sunlight.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_BYPASS  = PREFIX + "bypass.";

    @NotNull
    public static JPermission forCommand(@NotNull String name) {
        return new JPermission(PREFIX_COMMAND + name, "Access to the '/" + name + "' command.");
    }

    @NotNull
    public static JPermission forCommand(@NotNull String parent, @NotNull String child) {
        return new JPermission(PREFIX_COMMAND + parent + "." + child, "Access to the '/" + parent + " " + child + "' command.");
    }

    @NotNull
    public static JPermission forCommandOthers(@NotNull String name) {
        return new JPermission(PREFIX_COMMAND + name + ".others", "Access to the '/" + name + "' command on other players.");
    }

    @NotNull
    public static JPermission forCommandOthers(@NotNull String parent, @NotNull String child) {
        return new JPermission(PREFIX_COMMAND + parent + "." + child + ".others", "Access to the '/" + parent + " " + child + "' command on other players.");
    }

    public static final JPermission PLUGIN  = new JPermission(PREFIX + Placeholders.WILDCARD, "Access to all plugin functions (including modules).");
    public static final JPermission COMMAND = new JPermission(PREFIX_COMMAND + Placeholders.WILDCARD, "Access to all plugin basic (non-module) commands.");
    public static final JPermission BYPASS  = new JPermission(PREFIX_BYPASS + Placeholders.WILDCARD, "Bypassed all basic (non-module) restrictions.");

    public static final JPermission BYPASS_COMMAND_COOLDOWN           = new JPermission(PREFIX_BYPASS + "command.cooldown");
    public static final JPermission BYPASS_TELEPORT_REQUESTS_DISABLED = new JPermission(PREFIX_BYPASS + "teleport.requests.disabled");
    public static final JPermission BYPASS_IGNORE_PM                  = new JPermission(PREFIX_BYPASS + "ignore.pm");
    public static final JPermission BYPASS_IGNORE_TELEPORTS           = new JPermission(PREFIX_BYPASS + "ignore.teleports");

    public static final JPermission COMMAND_RELOAD = new JPermission(PREFIX_COMMAND + "reload", "Access to the '/sunlight reload' sub-command.");

    public static final JPermission COMMAND_AIR          = forCommand(AirCommand.NAME);
    public static final JPermission COMMAND_AIR_OTHERS   = forCommandOthers(AirCommand.NAME);
    public static final JPermission COMMAND_ANVIL        = forCommand(AnvilCommand.NAME);
    public static final JPermission COMMAND_ANVIL_OTHERS = forCommandOthers(AnvilCommand.NAME);

    public static final JPermission COMMAND_BACK               = forCommand(BackCommand.NAME);
    public static final JPermission COMMAND_BACK_OTHERS        = forCommandOthers(BackCommand.NAME);
    public static final JPermission COMMAND_BACK_BYPASS_WORLDS = new JPermission(PREFIX_COMMAND + BackCommand.NAME + ".bypass.worlds", "Bypasses disabled worlds for the '/" + BackCommand.NAME + "' command.");
    public static final JPermission COMMAND_BACK_BYPASS_CAUSES = new JPermission(PREFIX_COMMAND + BackCommand.NAME + ".bypass.causes", "Bypasses teleport causes for the '/" + BackCommand.NAME + "' command.");

    public static final JPermission COMMAND_CONDENSE = forCommand(CondenseCommand.NAME);

    public static final JPermission COMMAND_DEATHBACK               = forCommand(DeathBackCommand.NAME);
    public static final JPermission COMMAND_DEATHBACK_OTHERS        = forCommandOthers(DeathBackCommand.NAME);
    public static final JPermission COMMAND_DEATHBACK_BYPASS_WORLDS = new JPermission(PREFIX_COMMAND + DeathBackCommand.NAME + ".bypass.worlds", "Bypasses disabled worlds for the '/" + DeathBackCommand.NAME + "' command.");

    public static final JPermission COMMAND_DISPOSAL                = forCommand(DisposalCommand.NAME);
    public static final JPermission COMMAND_DISPOSAL_OTHERS         = forCommandOthers(DisposalCommand.NAME);
    public static final JPermission COMMAND_DIMENSION               = forCommand(DimensionCommand.NAME);
    public static final JPermission COMMAND_DIMENSION_OTHERS        = forCommandOthers(DimensionCommand.NAME);
    public static final JPermission COMMAND_ENCHANT                 = forCommand(EnchantCommand.NAME);
    public static final JPermission COMMAND_ENCHANT_OTHERS          = forCommandOthers(EnchantCommand.NAME);
    public static final JPermission COMMAND_ENCHANTING              = forCommand(EnchantingCommand.NAME);
    public static final JPermission COMMAND_ENCHANTING_OTHERS       = forCommandOthers(EnchantingCommand.NAME);
    public static final JPermission COMMAND_ENCHANTMENT_SEED        = forCommand(EnchantmentSeedCommand.NAME);
    public static final JPermission COMMAND_ENCHANTMENT_SEED_OTHERS = forCommandOthers(EnchantmentSeedCommand.NAME);
    public static final JPermission COMMAND_EXP                     = forCommand(ExpCommand.NAME);
    public static final JPermission COMMAND_EXP_OTHERS              = forCommandOthers(ExpCommand.NAME);
    public static final JPermission COMMAND_EXTINGUISH              = forCommand(ExtinguishCommand.NAME);
    public static final JPermission COMMAND_EXTINGUISH_OTHERS       = forCommandOthers(ExtinguishCommand.NAME);
    public static final JPermission COMMAND_EQUIP                   = forCommand(EquipCommand.NAME);

    public static final JPermission COMMAND_ENDERCHEST              = forCommand(EnderchestCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_CLEAR        = forCommand(EnderchestCommand.NAME, EnderchestClearCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_CLEAR_OTHERS = forCommandOthers(EnderchestCommand.NAME, EnderchestClearCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_COPY         = forCommand(EnderchestCommand.NAME, EnderchestCopyCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_COPY_OTHERS  = forCommandOthers(EnderchestCommand.NAME, EnderchestCopyCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_FILL         = forCommand(EnderchestCommand.NAME, EnderchestFillCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_OPEN         = forCommand(EnderchestCommand.NAME, EnderchestOpenCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_OPEN_OTHERS  = forCommandOthers(EnderchestCommand.NAME, EnderchestOpenCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_REPAIR        = forCommand(EnderchestCommand.NAME, EnderchestRepairCommand.NAME);
    public static final JPermission COMMAND_ENDERCHEST_REPAIR_OTHERS = forCommandOthers(EnderchestCommand.NAME, EnderchestRepairCommand.NAME);

    public static final JPermission COMMAND_FLY               = forCommand(FlyCommand.NAME);
    public static final JPermission COMMAND_FLY_OTHERS        = forCommandOthers(FlyCommand.NAME);
    public static final JPermission COMMAND_FIRE              = forCommand(FireCommand.NAME);
    public static final JPermission COMMAND_FIRE_OTHERS       = forCommandOthers(FireCommand.NAME);
    public static final JPermission COMMAND_FEED              = forCommand(FeedCommand.NAME);
    public static final JPermission COMMAND_FEED_OTHERS       = forCommandOthers(FeedCommand.NAME);
    public static final JPermission COMMAND_FOOD              = forCommand(FoodCommand.NAME);
    public static final JPermission COMMAND_FOOD_OTHERS       = forCommandOthers(FoodCommand.NAME);
    public static final JPermission COMMAND_FOOD_GOD          = forCommand(FoodGodCommand.NAME);
    public static final JPermission COMMAND_FOOD_GOD_OTHERS   = forCommandOthers(FoodGodCommand.NAME);

    public static final JPermission COMMAND_GAMEMODE          = forCommand(GamemodeCommand.NAME);
    public static final JPermission COMMAND_GAMEMODE_OTHERS   = forCommandOthers(GamemodeCommand.NAME);
    public static final JPermission COMMAND_GOD               = forCommand(GodCommand.NAME);
    public static final JPermission COMMAND_GOD_OTHERS        = forCommandOthers(GodCommand.NAME);
    public static final JPermission COMMAND_GOD_BYPASS_WORLDS = new JPermission(PREFIX_COMMAND + GodCommand.NAME + ".bypass.worlds", "Bypasses disabled worlds for the God Mode.");
    public static final JPermission COMMAND_GRINDSTONE = forCommand(GrindstoneCommand.NAME);
    public static final JPermission COMMAND_GRINDSTONE_OTHERS = forCommandOthers(GrindstoneCommand.NAME);

    public static final JPermission COMMAND_HEAL              = forCommand(HealCommand.NAME);
    public static final JPermission COMMAND_HEAL_OTHERS       = forCommandOthers(HealCommand.NAME);
    public static final JPermission COMMAND_HEALTH            = forCommand(HealthCommand.NAME);
    public static final JPermission COMMAND_HEALTH_OTHERS     = forCommandOthers(HealthCommand.NAME);

    public static final JPermission COMMAND_IGNORE            = forCommand(IgnoreCommand.NAME);
    public static final JPermission COMMAND_IGNORE_OTHERS     = forCommandOthers(IgnoreCommand.NAME);

    public static final JPermission COMMAND_INVENTORY               = forCommand(InventoryCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_CLEAR         = forCommand(InventoryCommand.NAME, InventoryClearCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_CLEAR_OTHERS  = forCommandOthers(InventoryCommand.NAME, InventoryClearCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_COPY          = forCommand(InventoryCommand.NAME, InventoryCopyCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_COPY_OTHERS   = forCommandOthers(InventoryCommand.NAME, InventoryCopyCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_FILL          = forCommand(InventoryCommand.NAME, InventoryFillCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_OPEN          = forCommand(InventoryCommand.NAME, InventoryOpenCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_REPAIR        = forCommand(InventoryCommand.NAME, InventoryRepairCommand.NAME);
    public static final JPermission COMMAND_INVENTORY_REPAIR_OTHERS = forCommandOthers(InventoryCommand.NAME, InventoryRepairCommand.NAME);

    public static final JPermission COMMAND_ITEM             = forCommand(ItemCommand.NAME);
    public static final JPermission COMMAND_ITEM_AMOUNT      = forCommand(ItemCommand.NAME, ItemAmountCommand.NAME);
    public static final JPermission COMMAND_ITEM_DAMAGE      = forCommand(ItemCommand.NAME, ItemDamageCommand.NAME);
    public static final JPermission COMMAND_ITEM_UNBREAKABLE = forCommand(ItemCommand.NAME, ItemUnbreakableCommand.NAME);
    public static final JPermission COMMAND_ITEM_ENCHANT     = forCommand(ItemCommand.NAME, ItemEnchantCommand.NAME);
    public static final JPermission COMMAND_ITEM_FLAG        = forCommand(ItemCommand.NAME, ItemFlagCommand.NAME);
    public static final JPermission COMMAND_ITEM_GET         = forCommand(ItemCommand.NAME, ItemGetCommand.NAME);
    public static final JPermission COMMAND_ITEM_GIVE        = forCommand(ItemCommand.NAME, ItemGiveCommand.NAME);
    public static final JPermission COMMAND_ITEM_MODEL       = forCommand(ItemCommand.NAME, ItemModelCommand.NAME);
    public static final JPermission COMMAND_ITEM_NAME        = forCommand(ItemCommand.NAME, ItemNameCommand.NAME);
    public static final JPermission COMMAND_ITEM_LORE        = forCommand(ItemCommand.NAME, ItemLoreCommand.NAME);
    public static final JPermission COMMAND_ITEM_TAKE        = forCommand(ItemCommand.NAME, ItemTakeCommand.NAME);
    public static final JPermission COMMAND_ITEM_SPAWN       = forCommand(ItemCommand.NAME, ItemSpawnCommand.NAME);
    public static final JPermission COMMAND_ITEM_POTION      = forCommand(ItemCommand.NAME, ItemPotionCommand.NAME);

    public static final JPermission COMMAND_LOOM = forCommand(LoomCommand.NAME);
    public static final JPermission COMMAND_LOOM_OTHERS = forCommandOthers(LoomCommand.NAME);

    public static final JPermission COMMAND_MOB       = forCommand(MobCommand.NAME);
    public static final JPermission COMMAND_MOB_KILL  = forCommand(MobCommand.NAME, MobKillCommand.NAME);
    public static final JPermission COMMAND_MOB_SPAWN = forCommand(MobCommand.NAME, MobSpawnCommand.NAME);

    public static final JPermission COMMAND_NEAR                 = forCommand(NearCommand.NAME);
    public static final JPermission COMMAND_NO_PHANTOM           = forCommand(NoPhantomCommand.NAME);
    public static final JPermission COMMAND_NO_PHANTOM_OTHERS    = forCommandOthers(NoPhantomCommand.NAME);
    public static final JPermission COMMAND_NO_MOB_TARGET        = forCommand(NoMobTargetCommand.NAME);
    public static final JPermission COMMAND_NO_MOB_TARGET_OTHERS = forCommandOthers(NoMobTargetCommand.NAME);

    public static final JPermission COMMAND_NICK               = forCommand(NickCommand.NAME);
    public static final JPermission COMMAND_NICK_SET           = forCommand(NickCommand.NAME, NickSetCommand.NAME);
    public static final JPermission COMMAND_NICK_CHANGE        = forCommand(NickCommand.NAME, NickChangeCommand.NAME);
    public static final JPermission COMMAND_NICK_CLEAR         = forCommand(NickCommand.NAME, NickClearCommand.NAME);
    public static final JPermission COMMAND_NICK_CLEAR_OTHERS  = forCommandOthers(NickCommand.NAME, NickClearCommand.NAME);
    public static final JPermission COMMAND_NICK_BYPASS_WORDS  = new JPermission(PREFIX_COMMAND + NickCommand.NAME + ".bypass.words", "Bypasses banned words for the '/" + NickCommand.NAME + " " + NickChangeCommand.NAME + "' command.");
    public static final JPermission COMMAND_NICK_BYPASS_REGEX  = new JPermission(PREFIX_COMMAND + NickCommand.NAME + ".bypass.regex", "Bypasses regex for the '/" + NickCommand.NAME + " " + NickChangeCommand.NAME + "' command.");
    public static final JPermission COMMAND_NICK_BYPASS_LENGTH = new JPermission(PREFIX_COMMAND + NickCommand.NAME + ".bypass.length", "Bypasses length requirements for the /'" + NickCommand.NAME + " " + NickChangeCommand.NAME + "' command.");

    public static final JPermission COMMAND_PLAYER_LIST = forCommand(PlayerListCommand.NAME);
    public static final JPermission COMMAND_PLAYER_INFO = forCommand(PlayerInfoCommand.NAME);

    public static final JPermission COMMAND_SKULL        = forCommand(SkullCommand.NAME);
    public static final JPermission COMMAND_SKULL_OTHERS = forCommandOthers(SkullCommand.NAME);
    public static final JPermission COMMAND_SMITE        = forCommand(SmiteCommand.NAME);
    public static final JPermission COMMAND_SMITE_OTHERS = forCommandOthers(SmiteCommand.NAME);
    public static final JPermission COMMAND_SPEED        = forCommand(SpeedCommand.NAME);
    public static final JPermission COMMAND_SPEED_OTHERS = forCommandOthers(SpeedCommand.NAME);
    public static final JPermission COMMAND_SUDO         = forCommand(SudoCommand.NAME);
    public static final JPermission COMMAND_SUDO_BYPASS  = new JPermission(PREFIX_COMMAND + SudoCommand.NAME + ".bypass", "Prevents from being affected by the '/" + SudoCommand.NAME + "' command");
    public static final JPermission COMMAND_SUICIDE = forCommand(SuicideCommand.NAME);

    public static final JPermission COMMAND_TELEPORT                 = forCommand(TeleportCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_ACCEPT          = forCommand(TeleportCommand.NAME, TeleportAcceptCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_DECLINE         = forCommand(TeleportCommand.NAME, TeleportDeclineCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_INVITE          = forCommand(TeleportCommand.NAME, TeleportInviteCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_REQUEST         = forCommand(TeleportCommand.NAME, TeleportRequestCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_LOCATION        = forCommand(TeleportCommand.NAME, TeleportLocationCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_LOCATION_OTHERS = forCommandOthers(TeleportCommand.NAME, TeleportLocationCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_TOGGLE          = forCommand(TeleportCommand.NAME, TeleportToggleCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_TOGGLE_OTHERS   = forCommandOthers(TeleportCommand.NAME, TeleportToggleCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_SUMMON          = forCommand(TeleportCommand.NAME, TeleportSummonCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_TO              = forCommand(TeleportCommand.NAME, TeleportToCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_SEND            = forCommand(TeleportCommand.NAME, TeleportSendCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_TOP             = forCommand(TeleportCommand.NAME, TeleportTopCommand.NAME);
    public static final JPermission COMMAND_TELEPORT_TOP_OTHERS      = forCommandOthers(TeleportCommand.NAME, TeleportTopCommand.NAME);

    public static final JPermission COMMAND_TIME                 = forCommand(TimeCommand.NAME);
    public static final JPermission COMMAND_TIME_SHOW            = forCommand(TimeCommand.NAME, TimeShowCommand.NAME);
    public static final JPermission COMMAND_TIME_SET             = forCommand(TimeCommand.NAME, TimeSetCommand.NAME);
    public static final JPermission COMMAND_TIME_PERSONAL        = forCommand(TimeCommand.NAME, TimePersonalCommand.NAME);
    public static final JPermission COMMAND_TIME_PERSONAL_OTHERS = forCommandOthers(TimeCommand.NAME, TimePersonalCommand.NAME);

    public static final JPermission COMMAND_VANISH            = forCommand(VanishCommand.NAME);
    public static final JPermission COMMAND_VANISH_OTHERS     = forCommandOthers(VanishCommand.NAME);
    public static final JPermission COMMAND_VANISH_BYPASS_SEE = new JPermission(PREFIX_COMMAND + VanishCommand.NAME + ".bypass.see");

    public static final JPermission COMMAND_WEATHER          = forCommand(WeatherCommand.NAME);
    public static final JPermission COMMAND_WORKBENCH        = forCommand(WorkbenchCommand.NAME);
    public static final JPermission COMMAND_WORKBENCH_OTHERS = forCommandOthers(WorkbenchCommand.NAME);

    static {
        PLUGIN.addChildren(COMMAND, BYPASS);

        BYPASS.addChildren(
            BYPASS_COMMAND_COOLDOWN,
            BYPASS_TELEPORT_REQUESTS_DISABLED,
            BYPASS_IGNORE_PM, BYPASS_IGNORE_TELEPORTS
        );

        COMMAND.addChildren(
            COMMAND_RELOAD,

            COMMAND_ANVIL, COMMAND_ANVIL_OTHERS,
            COMMAND_AIR, COMMAND_AIR_OTHERS,

            COMMAND_BACK, COMMAND_BACK_OTHERS, COMMAND_BACK_BYPASS_WORLDS, COMMAND_BACK_BYPASS_CAUSES,

            COMMAND_CONDENSE,

            COMMAND_DEATHBACK, COMMAND_DEATHBACK_OTHERS, COMMAND_DEATHBACK_BYPASS_WORLDS,
            COMMAND_DISPOSAL, COMMAND_DISPOSAL_OTHERS,

            COMMAND_ENCHANT, COMMAND_ENCHANT_OTHERS,
            COMMAND_ENCHANTING, COMMAND_ENCHANTING_OTHERS,
            COMMAND_ENCHANTMENT_SEED, COMMAND_ENCHANTMENT_SEED_OTHERS,

            COMMAND_EXP, COMMAND_EXP_OTHERS,
            COMMAND_EXTINGUISH, COMMAND_EXTINGUISH_OTHERS,
            COMMAND_EQUIP,

            COMMAND_ENDERCHEST, COMMAND_ENDERCHEST_CLEAR, COMMAND_ENDERCHEST_CLEAR_OTHERS,
            COMMAND_ENDERCHEST_COPY, COMMAND_ENDERCHEST_COPY_OTHERS, COMMAND_ENDERCHEST_FILL,
            COMMAND_ENDERCHEST_OPEN, COMMAND_ENDERCHEST_OPEN_OTHERS,
            COMMAND_ENDERCHEST_REPAIR, COMMAND_ENDERCHEST_REPAIR_OTHERS,

            COMMAND_FLY, COMMAND_FLY_OTHERS,
            COMMAND_FIRE, COMMAND_FIRE_OTHERS,
            COMMAND_FEED, COMMAND_FEED_OTHERS,
            COMMAND_FOOD, COMMAND_FOOD_OTHERS,
            COMMAND_FOOD_GOD, COMMAND_FOOD_GOD_OTHERS,

            COMMAND_GAMEMODE, COMMAND_GAMEMODE_OTHERS,
            COMMAND_GOD, COMMAND_GOD_OTHERS, COMMAND_GOD_BYPASS_WORLDS,
            COMMAND_GRINDSTONE, COMMAND_GRINDSTONE_OTHERS,

            COMMAND_HEAL, COMMAND_HEAL_OTHERS,
            COMMAND_HEALTH, COMMAND_HEALTH_OTHERS,

            COMMAND_IGNORE_OTHERS,

            COMMAND_INVENTORY, COMMAND_INVENTORY_CLEAR, COMMAND_INVENTORY_CLEAR_OTHERS,
            COMMAND_INVENTORY_COPY, COMMAND_INVENTORY_COPY_OTHERS, COMMAND_INVENTORY_FILL,
            COMMAND_INVENTORY_OPEN,
            COMMAND_INVENTORY_REPAIR, COMMAND_INVENTORY_REPAIR_OTHERS,

            COMMAND_ITEM, COMMAND_ITEM_AMOUNT, COMMAND_ITEM_ENCHANT, COMMAND_ITEM_FLAG,
            COMMAND_ITEM_GET, COMMAND_ITEM_GIVE, COMMAND_ITEM_LORE, COMMAND_ITEM_MODEL,
            COMMAND_ITEM_NAME, COMMAND_ITEM_SPAWN, COMMAND_ITEM_TAKE, COMMAND_ITEM_POTION,
            COMMAND_ITEM_DAMAGE, COMMAND_ITEM_UNBREAKABLE,

            COMMAND_LOOM, COMMAND_LOOM_OTHERS,

            COMMAND_MOB, COMMAND_MOB_KILL, COMMAND_MOB_SPAWN,

            COMMAND_NEAR,
            COMMAND_NO_PHANTOM, COMMAND_NO_PHANTOM_OTHERS,

            COMMAND_NICK, COMMAND_NICK_CHANGE, COMMAND_NICK_SET, COMMAND_NICK_CLEAR, COMMAND_NICK_CLEAR_OTHERS,
            COMMAND_NICK_BYPASS_LENGTH, COMMAND_NICK_BYPASS_WORDS,

            COMMAND_TELEPORT, COMMAND_TELEPORT_ACCEPT, COMMAND_TELEPORT_DECLINE, COMMAND_TELEPORT_INVITE,
            COMMAND_TELEPORT_LOCATION, COMMAND_TELEPORT_LOCATION_OTHERS, COMMAND_TELEPORT_REQUEST,
            COMMAND_TELEPORT_TOGGLE, COMMAND_TELEPORT_TOGGLE_OTHERS, COMMAND_TELEPORT_SUMMON,
            COMMAND_TELEPORT_TO, COMMAND_TELEPORT_SEND, COMMAND_TELEPORT_TOP, COMMAND_TELEPORT_TOP_OTHERS,

            COMMAND_TIME, COMMAND_TIME_SHOW, COMMAND_TIME_SET, COMMAND_TIME_PERSONAL, COMMAND_TIME_PERSONAL_OTHERS,

            COMMAND_PLAYER_LIST,
            COMMAND_PLAYER_INFO,

            COMMAND_SKULL, COMMAND_SKULL_OTHERS,
            COMMAND_SMITE, COMMAND_SMITE_OTHERS,
            COMMAND_SPEED, COMMAND_SPEED_OTHERS,
            COMMAND_SUDO, COMMAND_SUDO_BYPASS,
            COMMAND_SUICIDE,

            COMMAND_VANISH, COMMAND_VANISH_OTHERS, COMMAND_VANISH_BYPASS_SEE,

            COMMAND_WEATHER,
            COMMAND_WORKBENCH, COMMAND_WORKBENCH_OTHERS
        );
    }

    @Deprecated public static final String PREFIX_CMD          = PREFIX + "cmd.";
    @Deprecated public static final String      CMD_BROADCAST              = PREFIX_COMMAND + BroadcastCommand.NAME;
    @Deprecated public static final String      CMD_CTEXT        = PREFIX_COMMAND + CustomTextCommand.NAME;
    @Deprecated public static final String CMD_SPAWNER         = PREFIX_COMMAND + SpawnerCommand.NAME;
}
