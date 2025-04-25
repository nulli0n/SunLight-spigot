package su.nightexpress.sunlight.command.list;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ArgumentTypes;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.flag.FlagTypes;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.language.message.LangMessage;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.CommandTools;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ItemCommands {

    public static final String NAME = "item";

    //private static final String FLAG_NAME     = "name";
    //private static final String FLAG_LORE     = "lore";
    //private static final String FLAG_ENCHANTS = "ench";

    public static final String NODE_AMOUNT      = "item_amount";
    public static final String NODE_DAMAGE      = "item_damage";
    public static final String NODE_ENCHANT     = "item_enchant";
    public static final String NODE_GET         = "item_get";
    public static final String NODE_GIVE        = "item_give";
    public static final String NODE_LORE_ADD    = "item_lore_add";
    public static final String NODE_LORE_CLEAR  = "item_lore_clear";
    public static final String NODE_LORE_REMOVE = "item_lore_remove";
    public static final String NODE_MODEL       = "item_model";
    public static final String NODE_NAME        = "item_name";
    public static final String NODE_POTION      = "item_potion";
    public static final String NODE_REPAIR      = "item_repair";
    public static final String NODE_SPAWN       = "item_spawn";
    public static final String NODE_TAKE        = "item_take";
    public static final String NODE_UNBREAKABLE = "item_unbreakable";

    private static int defaultAmount;

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE_AMOUNT, (template, config) -> builderAmount(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_DAMAGE, (template, config) -> builderDamage(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_ENCHANT, (template, config) -> builderEnchant(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_GET, (template, config) -> builderGet(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_GIVE, (template, config) -> builderGive(plugin, template, config));

        CommandRegistry.registerDirectExecutor(NODE_LORE_ADD, (template, config) -> builderLoreAdd(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_LORE_CLEAR, (template, config) -> builderLoreClear(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_LORE_REMOVE, (template, config) -> builderLoreRemove(plugin, template, config));

        CommandRegistry.registerDirectExecutor(NODE_MODEL, (template, config) -> builderModel(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_NAME, (template, config) -> builderName(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_POTION, (template, config) -> builderPotion(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_REPAIR, (template, config) -> builderRepair(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SPAWN, (template, config) -> builderSpawn(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_TAKE, (template, config) -> builderTake(plugin, template, config));
        CommandRegistry.registerDirectExecutor(NODE_UNBREAKABLE, (template, config) -> builderUnbreakable(plugin, template, config));

        CommandRegistry.addTemplate(NAME, CommandTemplate.group(new String[]{NAME, "i"},
            NAME,
            CommandPerms.PREFIX + "item",
            CommandTemplate.direct(new String[]{"amount"}, NODE_AMOUNT),
            CommandTemplate.direct(new String[]{"damage"}, NODE_DAMAGE),
            CommandTemplate.direct(new String[]{"enchant"}, NODE_ENCHANT),
            CommandTemplate.direct(new String[]{"get"}, NODE_GET),
            CommandTemplate.direct(new String[]{"give"}, NODE_GIVE),
//            CommandTemplate.chained(new String[]{"lore"}, NODE_LORE,
//                CommandTemplate.direct(new String[]{"add"}, NODE_LORE_ADD),
//                CommandTemplate.direct(new String[]{"clear"}, NODE_LORE_CLEAR),
//                CommandTemplate.direct(new String[]{"remove"}, NODE_LORE_REMOVE)
//            ),
            CommandTemplate.direct(new String[]{"model"}, NODE_MODEL),
            CommandTemplate.direct(new String[]{"name"}, NODE_NAME),
            CommandTemplate.direct(new String[]{"potion"}, NODE_POTION),
            CommandTemplate.direct(new String[]{"repair"}, NODE_REPAIR),
            CommandTemplate.direct(new String[]{"spawn"}, NODE_SPAWN),
            CommandTemplate.direct(new String[]{"take"}, NODE_TAKE),
            CommandTemplate.direct(new String[]{"unbreakable"}, NODE_UNBREAKABLE)
        ));

        CommandRegistry.addTemplate("itemname", CommandTemplate.direct(new String[]{"itemname", "rename"}, NODE_NAME));
        CommandRegistry.addTemplate("itemlore", CommandTemplate.group(new String[]{"itemlore", "relore"},
            "Item lore commands.",
            CommandPerms.PREFIX + "itemlore",
            CommandTemplate.direct(new String[]{"add"}, NODE_LORE_ADD),
            CommandTemplate.direct(new String[]{"clear"}, NODE_LORE_CLEAR),
            CommandTemplate.direct(new String[]{"remove"}, NODE_LORE_REMOVE)
        ));
        CommandRegistry.addTemplate("repair", CommandTemplate.direct(new String[]{"repair", "fix"}, NODE_REPAIR));
    }



    @NotNull
    public static DirectNodeBuilder builderAmount(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        defaultAmount = ConfigValue.create("Settings.Item.Amount.Default_Value",
            64,
            "Sets default amount for the '" + NODE_AMOUNT + "' command.").read(config);

        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_AMOUNT_DESC)
            .permission(CommandPerms.ITEM_AMOUNT)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "8", "16", "32", "64"))
            )
            .executes((context, arguments) -> executeAmount(plugin, context, arguments))
            ;
    }

    public static boolean executeAmount(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        int amount = arguments.getIntArgument(CommandArguments.AMOUNT, defaultAmount);
        item.setAmount(amount);

        context.send(Lang.COMMAND_ITEM_AMOUNT_DONE.getMessage()
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
        );
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderDamage(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_DAMAGE_DESC)
            .permission(CommandPerms.ITEM_DAMAGE)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .required()
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("0", "50", "100", "500"))
            )
            .executes((context, arguments) -> executeDamage(plugin, context, arguments))
            ;
    }

    public static boolean executeDamage(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        int amount = arguments.getIntArgument(CommandArguments.AMOUNT);
        boolean result = SunUtils.damageItem(item, amount);
        context.send((result ? Lang.COMMAND_ITEM_DAMAGE_DONE : Lang.ERROR_ITEM_NOT_DAMAGEABLE).getMessage()
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
            .replace(Placeholders.GENERIC_AMOUNT, amount)
        );
        return result;
    }



    @NotNull
    public static DirectNodeBuilder builderEnchant(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_ENCHANT_DESC)
            .permission(CommandPerms.ITEM_ENCHANT)
            .withArgument(ArgumentTypes.enchantment(CommandArguments.ENCHANT)
                .required()
                .withSamples(context -> BukkitThing.getEnchantments().stream().map(BukkitThing::toString).toList())
            )
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.LEVEL).withSamples(context -> IntStream.range(0, 11).boxed().map(String::valueOf).toList()))
            .executes((context, arguments) -> executeEnchant(plugin, context, arguments))
            ;
    }

    public static boolean executeEnchant(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        boolean isUnlimited = context.getSender().hasPermission(CommandPerms.ITEM_ENCHANT_UNLIMITED);
        Enchantment enchantment = arguments.getEnchantmentArgument(CommandArguments.ENCHANT);

        if (!isUnlimited && !enchantment.canEnchantItem(item)) {
            Lang.COMMAND_ITEM_ENCHANT_ERROR_NOT_COMPATIBLE.getMessage().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangAssets.get(enchantment))
            );
            return false;
        }

        int level = arguments.getIntArgument(CommandArguments.LEVEL, 1);
        if (!isUnlimited && level > enchantment.getMaxLevel()) {
            Lang.COMMAND_ITEM_ENCHANT_ERROR_OVERPOWERED.getMessage().send(context.getSender(), replacer -> replacer
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
                .replace(Placeholders.GENERIC_NAME, LangAssets.get(enchantment))
                .replace(Placeholders.GENERIC_LEVEL, NumberUtil.toRoman(level))
            );
            return false;
        }

        SunUtils.enchantItem(item, enchantment, level);

        context.send((level > 0 ? Lang.COMMAND_ITEM_ENCHANT_DONE_ENCHANTED : Lang.COMMAND_ITEM_ENCHANT_DONE_DISENCHANTED).getMessage()
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
            .replace(Placeholders.GENERIC_NAME, LangAssets.get(enchantment))
            .replace(Placeholders.GENERIC_LEVEL, NumberUtil.toRoman(level))
        );
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderGet(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_GET_DESC)
            .permission(CommandPerms.ITEM_GET)
            .withArgument(CommandArguments.material(CommandArguments.ITEM, Material::isItem).required())
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "16", "32", "64"))
            )
            .executes((context, arguments) -> executeGet(plugin, context, arguments))
            ;
    }

    public static boolean executeGet(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return false;

        ItemStack itemStack = createItem(context, arguments);
        Players.addItem(player, itemStack);

        context.send(Lang.COMMAND_ITEM_GET_DONE.getMessage()
            .replace(Placeholders.GENERIC_AMOUNT, itemStack.getAmount())
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(itemStack))
        );
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderGive(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ITEM_GIVE_DESC)
            .permission(CommandPerms.ITEM_GIVE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.material(CommandArguments.ITEM, Material::isItem).required())
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT)
                .localized(Lang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .withSamples(context -> Lists.newList("1", "16", "32", "64"))
            )
            .executes((context, arguments) -> executeGive(plugin, context, arguments))
            ;
    }

    public static boolean executeGive(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ItemStack item = createItem(context, arguments);

        Players.addItem(target, item);
        if (!target.isOnline()) target.saveData();

        context.send(Lang.COMMAND_ITEM_GIVE_DONE.getMessage()
            .replace(Placeholders.forPlayer(target))
            .replace(Placeholders.GENERIC_AMOUNT, item.getAmount())
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(item))
        );

        Lang.COMMAND_ITEM_GIVE_NOTIFY.getMessage()
            .replace(Placeholders.GENERIC_AMOUNT, item.getAmount())
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(item))
            .send(target);

        return true;
    }



    private enum LoreAction { ADD, REMOVE, CLEAR }

    // TODO Permissions
    @NotNull
    public static DirectNodeBuilder builderLoreAdd(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ITEM_LORE_ADD_DESC)
            .withArgument(ArgumentTypes.string(CommandArguments.TEXT).required().complex().localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .withFlag(FlagTypes.integer("pos"))
            .executes((context, arguments) -> executeLore(plugin, context, arguments, LoreAction.ADD))
            ;
    }

    @NotNull
    public static DirectNodeBuilder builderLoreRemove(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ITEM_LORE_REMOVE_DESC)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.POSITION).required().localized(Lang.COMMAND_ARGUMENT_NAME_POSITION))
            .executes((context, arguments) -> executeLore(plugin, context, arguments, LoreAction.REMOVE))
            ;
    }

    @NotNull
    public static DirectNodeBuilder builderLoreClear(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ITEM_LORE_CLEAR_DESC)
            .executes((context, arguments) -> executeLore(plugin, context, arguments, LoreAction.CLEAR))
            ;
    }

    private static boolean executeLore(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull LoreAction action) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        LangMessage message;
        List<String> lore = ItemUtil.getLore(item);

        switch (action) {
            case ADD -> {
                int pos = arguments.getIntFlag("pos", -1);
                String text = NightMessage.asLegacy(arguments.getStringArgument(CommandArguments.TEXT));
                if (pos >= 0 && pos < lore.size()) {
                    lore.add(pos, text);
                }
                else {
                    lore.add(text);
                }
                message = Lang.COMMAND_ITEM_LORE_ADD_DONE.getMessage();
            }
            case REMOVE -> {
                if (!lore.isEmpty()) {
                    int pos = arguments.getIntArgument(CommandArguments.POSITION);
                    if (pos < 0 || pos >= lore.size()) {
                        lore.removeLast();
                    }
                    else {
                        lore.remove(pos - 1);
                    }
                }
                message = Lang.COMMAND_ITEM_LORE_REMOVE_DONE.getMessage();
            }
            case CLEAR -> {
                lore.clear();
                message = Lang.COMMAND_ITEM_LORE_CLEAR_DONE.getMessage();
            }
            default -> {
                return false;
            }
        }

        ItemUtil.editMeta(item, meta -> meta.setLore(lore));
        context.send(message);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderModel(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_MODEL_DESC)
            .permission(CommandPerms.ITEM_MODEL)
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.VALUE).required())
            .executes((context, arguments) -> executeModel(plugin, context, arguments));
    }

    public static boolean executeModel(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        int modelData = arguments.getIntArgument(CommandArguments.VALUE);
        ItemUtil.editMeta(item, meta -> meta.setCustomModelData(modelData));

        context.send(Lang.COMMAND_ITEM_MODEL_DONE.getMessage()
            .replace(Placeholders.GENERIC_AMOUNT, modelData)
            .replace(Placeholders.GENERIC_NAME, ItemUtil.getItemName(item))
        );
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderName(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_NAME_DESC)
            .permission(CommandPerms.ITEM_NAME)
            .withArgument(ArgumentTypes.string(CommandArguments.TEXT).required().complex().localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes((context, arguments) -> executeName(plugin, context, arguments))
            ;
    }

    public static boolean executeName(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack itemStack = getItem(context, arguments);
        if (itemStack == null) return false;

        ItemUtil.editMeta(itemStack, meta -> {
            String name = NightMessage.asLegacy(arguments.getStringArgument(CommandArguments.TEXT));
            meta.setDisplayName(name);
        });

        context.send(Lang.COMMAND_ITEM_NAME_DONE.getMessage());
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderPotion(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        // TODO add remove clear commands
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_POTION_ADD_DESC)
            .permission(CommandPerms.ITEM_POTION)
            .withArgument(CommandArguments.effect(CommandArguments.TYPE, e -> true).required())
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.LEVEL).required().withSamples(context -> Lists.newList("0", "1", "5", "10", "127")))
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.TIME).required().withSamples(context -> Lists.newList("60", "300", "600", "3600")))
            .executes((context, arguments) -> executePotion(plugin, context, arguments))
            ;
    }

    public static boolean executePotion(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof PotionMeta potionMeta)) {
            context.send(Lang.ERROR_ITEM_NOT_POTION.getMessage());
            return false;
        }

        PotionEffectType effectType = arguments.getArgument(CommandArguments.TYPE, PotionEffectType.class);
        int amplifier = arguments.getIntArgument(CommandArguments.LEVEL);

        if (amplifier < 0) {
            potionMeta.removeCustomEffect(effectType);
        }
        else {
            int duration = arguments.getIntArgument(CommandArguments.TIME);
            if (duration <= 0) return false;

            PotionEffect pEffect = new PotionEffect(effectType, duration, amplifier);
            potionMeta.addCustomEffect(pEffect, true);
        }

        context.send(Lang.COMMAND_ITEM_POTION_ADD_DONE.getMessage());
        item.setItemMeta(meta);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderRepair(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_REPAIR_DESC)
            .permission(CommandPerms.ITEM_REPAIR)
            .executes((context, arguments) -> executeRepair(plugin, context, arguments))
            ;
    }

    public static boolean executeRepair(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        boolean result = SunUtils.repairItem(item);
        // TODO Better messages for: no durability / already at this value + check if item is damageable
        context.send((result ? Lang.COMMAND_ITEM_REPAIR_DONE : Lang.ERROR_ITEM_NOT_DAMAGEABLE).getMessage()
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
        );
        return result;
    }



    @NotNull
    public static DirectNodeBuilder builderSpawn(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ITEM_SPAWN_DESC)
            .permission(CommandPerms.ITEM_SPAWN)
            .withArgument(CommandArguments.material(CommandArguments.ITEM, Material::isItem).required())
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).required().withSamples(context -> Lists.newList("1", "8", "16", "32", "64")))
            .withArgument(ArgumentTypes.world(CommandArguments.WORLD).required())
            .withArgument(ArgumentTypes.decimal(CommandArguments.X).required())
            .withArgument(ArgumentTypes.decimal(CommandArguments.Y).required())
            .withArgument(ArgumentTypes.decimal(CommandArguments.Z).required())
            .executes((context, arguments) -> executeSpawn(plugin, context, arguments))
            ;
    }

    public static boolean executeSpawn(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = createItem(context, arguments);

        World world = arguments.getWorldArgument(CommandArguments.WORLD);
        double x = arguments.getDoubleArgument(CommandArguments.X);
        double y = arguments.getDoubleArgument(CommandArguments.Y);
        double z = arguments.getDoubleArgument(CommandArguments.Z);
        Location location = new Location(world, x, y, z);

        world.dropItemNaturally(location, item);

        context.send(Lang.COMMAND_ITEM_SPAWN_DONE.getMessage()
            .replace(Placeholders.GENERIC_AMOUNT, item.getAmount())
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(item))
            .replace(Placeholders.forLocation(location))
        );
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderTake(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .description(Lang.COMMAND_ITEM_TAKE_DESC)
            .permission(CommandPerms.ITEM_TAKE)
            .withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER).required())
            .withArgument(CommandArguments.material(CommandArguments.ITEM, Material::isItem).required())
            .withArgument(ArgumentTypes.integerAbs(CommandArguments.AMOUNT).withSamples(context -> Lists.newList("1", "8", "16", "32", "64")))
            .executes((context, arguments) -> executeTake(plugin, context, arguments))
            ;
    }

    public static boolean executeTake(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player target = CommandTools.getTarget(plugin, context, arguments, CommandArguments.PLAYER, true);
        if (target == null) return false;

        ItemStack itemStack = createItem(context, arguments);
        Predicate<ItemStack> predicate = itemHas -> itemHas.isSimilar(itemStack);
        int amount = itemStack.getAmount();

        if (Players.countItem(target, predicate) < amount) {
            context.send(Lang.COMMAND_ITEM_TAKE_ERROR_NOT_ENOUGH.getMessage()
                .replace(Placeholders.GENERIC_TOTAL, amount)
                .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(itemStack))
                .replace(Placeholders.GENERIC_AMOUNT, Players.countItem(target, predicate))
            );
            return false;
        }

        Players.takeItem(target, predicate, amount);

        Lang.COMMAND_ITEM_TAKE_DONE.getMessage()
            .replace(Placeholders.forPlayer(target))
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(itemStack))
            .send(context.getSender());

        Lang.COMMAND_ITEM_TAKE_NOTIFY.getMessage()
            .replace(Placeholders.GENERIC_AMOUNT, amount)
            .replace(Placeholders.GENERIC_TYPE, ItemUtil.getItemName(itemStack))
            .send(target);

        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderUnbreakable(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_ITEM_UNBREAKABLE_DESC)
            .permission(CommandPerms.ITEM_UNBREAKABLE)
            .executes((context, arguments) -> executeUnbreakable(plugin, context, arguments))
            ;
    }

    public static boolean executeUnbreakable(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack item = getItem(context, arguments);
        if (item == null) return false;

        if (!(item.getItemMeta() instanceof Damageable damageable)) {
            context.send(Lang.ERROR_ITEM_NOT_DAMAGEABLE.getMessage()
                .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
            );
            return false;
        }

        damageable.setUnbreakable(!damageable.isUnbreakable());
        item.setItemMeta(damageable);

        context.send(Lang.COMMAND_ITEM_UNBREAKABLE_DONE.getMessage()
            .replace(Placeholders.GENERIC_ITEM, ItemUtil.getItemName(item))
            .replace(Placeholders.GENERIC_STATE, Lang.getYesOrNo(damageable.isUnbreakable()))
        );

        return true;
    }



    @NotNull
    private static ItemStack createItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Material material = arguments.getMaterialArgument(CommandArguments.ITEM);
        int amount = arguments.getIntArgument(CommandArguments.AMOUNT, 1);

        ItemStack itemStack = new ItemStack(material, amount);

        //String flagName = arguments.getStringFlag(FLAG_NAME, "");
        //String itemLore = result.getFlag(ItemCommand.FLAG_LORE);
        //String flagEnchants = result.getFlag(ItemCommand.FLAG_ENCHANTS);

        //List<String> checkLore = itemLore == null ? new ArrayList<>() : ItemCommand.parseFlagLore(itemLore);
        //Map<Enchantment, Integer> itemEnchants = flagEnchants == null ? new HashMap<>() : ItemCommand.parseFlagEnchants(flagEnchants);

        ItemUtil.editMeta(itemStack, meta -> {
            //if (!flagName.isBlank()) meta.setDisplayName(NightMessage.asLegacy(flagName));
            //if (itemLore != null) meta.setLore(Colorizer.apply(checkLore));
            //if (flagEnchants != null) itemEnchants.forEach((enchant, level) -> meta.addEnchant(enchant, level, true));
        });

        return itemStack;
    }

    @Nullable
    private static ItemStack getItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getExecutor();
        if (player == null) return null;

        ItemStack itemStack = player.getInventory().getItemInMainHand();
        ItemMeta meta = itemStack.getItemMeta();
        if (itemStack.getType().isAir() || meta == null) {
            context.send(Lang.ERROR_EMPTY_HAND.getMessage());
            return null;
        }

        return itemStack;
    }

    @NotNull
    private static List<String> parseFlagLore(@NotNull String flagLore) {
        return Stream.of(flagLore.split("\\|")).toList();
    }

    @NotNull
    private static Map<Enchantment, Integer> parseFlagEnchants(@NotNull String flagEnchants) {
        Map<Enchantment, Integer> checkEnchants = new HashMap<>();
        for (String enchantRaw : flagEnchants.split(" ")) {
            if (enchantRaw.isEmpty()) continue;

            String[] enchantSplit = enchantRaw.split(":");
            if (enchantSplit.length == 0) continue;

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantSplit[0]));
            if (enchantment == null) continue;

            int level = enchantSplit.length >= 2 ? NumberUtil.getInteger(enchantSplit[1], -1) : 0;
            if (level <= 0) continue;

            checkEnchants.put(enchantment, level);
        }
        return checkEnchants;
    }
}
