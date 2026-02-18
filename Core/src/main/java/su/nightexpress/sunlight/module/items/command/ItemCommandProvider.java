package su.nightexpress.sunlight.module.items.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.items.ItemsModule;
import su.nightexpress.sunlight.module.items.ItemsSettings;
import su.nightexpress.sunlight.module.items.ItemsLang;
import su.nightexpress.sunlight.module.items.ItemsPerms;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.ItemStackUtils;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.IntStream;

import static su.nightexpress.sunlight.SLPlaceholders.*;

public class ItemCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_AMOUNT      = "amount";
    private static final String COMMAND_DAMAGE      = "damage";
    private static final String COMMAND_ENCHANT     = "enchant";
    private static final String COMMAND_DISENCHANT  = "disenchant";
    private static final String COMMAND_GET         = "get";
    private static final String COMMAND_GIVE        = "give";
    private static final String COMMAND_MODEL       = "model";
    private static final String COMMAND_NAME        = "name";
    private static final String COMMAND_POTION      = "potion";
    private static final String COMMAND_REPAIR      = "repair";
    private static final String COMMAND_SPAWN       = "spawn";
    private static final String COMMAND_UNBREAKABLE = "unbreakable";

    private final ItemsModule   module;
    private final ItemsSettings settings;
    private final UserManager userManager;

    public ItemCommandProvider(@NotNull SunLightPlugin plugin, @NotNull ItemsModule module, @NotNull ItemsSettings settings, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.settings = settings;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_AMOUNT, false, new String[]{"itemamount"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_AMOUNT_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_AMOUNT)
            .withArguments(Arguments.integer(CommandArguments.AMOUNT, 1, 64)
                .optional()
                .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .suggestions((reader, context) -> Lists.newList("1", "8", "16", "32", "64"))
            )
            .executes(this::setItemStackSize)
        );

        this.registerLiteral(COMMAND_DAMAGE, false, new String[]{"itemdamage"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_DAMAGE_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_DAMAGE)
            .withArguments(Arguments.integer(CommandArguments.AMOUNT, 0)
                .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                .suggestions((reader, context) -> {
                    return Optional.ofNullable(context.getPlayer())
                        .map(player -> player.getInventory().getItemInMainHand())
                        .map(itemStack -> Lists.newList("0", String.valueOf(itemStack.getType().getMaxDurability())))
                        .orElse(Collections.emptyList());
                })
            )
            .executes(this::damageOrRepairItem)
        );

        this.registerLiteral(COMMAND_ENCHANT, false, new String[]{"addenchant"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_ENCHANT_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_ENCHANT)
            .withArguments(
                Arguments.enchantment(CommandArguments.ENCHANT)
                    .suggestions((reader, context) -> {
                        return Optional.ofNullable(context.getPlayer())
                            .map(player -> player.getInventory().getItemInMainHand())
                            .map(itemStack -> BukkitThing.getEnchantments().stream()
                                .filter(enchantment -> enchantment.canEnchantItem(itemStack) || context.getSender().hasPermission(ItemsPerms.COMMAND_ITEM_ENCHANT_UNLIMITED))
                                .map(BukkitThing::getAsString)
                                .toList()
                            )
                            .orElse(Collections.emptyList());
                    }),
                Arguments.integer(CommandArguments.LEVEL, 1, 1000)
                    .suggestions((reader, context) -> {
                        return Optional.ofNullable(BukkitThing.getEnchantment(reader.getArgument(reader.getCursor() - 1)))
                            .map(enchantment -> context.getSender().hasPermission(ItemsPerms.COMMAND_ITEM_ENCHANT_UNLIMITED) ? Math.max(10, enchantment.getMaxLevel()) : enchantment.getMaxLevel())
                            .map(maxLevel -> IntStream.range(1, maxLevel + 1).boxed().map(String::valueOf).toList())
                            .orElse(Collections.emptyList());
                    })
            )
            .executes(this::enchantItem)
        );

        this.registerLiteral(COMMAND_DISENCHANT, false, new String[]{"removeenchant"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_DISENCHANT_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_DISENCHANT)
            .withArguments(
                Arguments.enchantment(CommandArguments.ENCHANT)
                    .optional()
                    .suggestions((reader, context) -> {
                        return Optional.ofNullable(context.getPlayer())
                            .map(player -> player.getInventory().getItemInMainHand())
                            .filter(itemStack -> !itemStack.getType().isAir())
                            .map(itemStack -> itemStack.getEnchantments().keySet().stream().map(BukkitThing::getAsString).toList())
                            .orElse(Collections.emptyList());
                    })
            )
            .executes(this::disenchantItem)
        );

        this.registerLiteral(COMMAND_GET, true, new String[]{"getitem"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_GET_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_GET)
            .withArguments(
                Arguments.itemType(CommandArguments.ITEM),
                Arguments.integer(CommandArguments.AMOUNT, 1)
                    .optional()
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .suggestions((reader, context) -> Lists.newList("1"))
            )
            .executes(this::getItem)
        );

        this.registerLiteral(COMMAND_GIVE, true, new String[]{"giveitem"}, builder -> builder
            .description(ItemsLang.COMMAND_ITEM_GIVE_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_GIVE)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.itemType(CommandArguments.ITEM),
                Arguments.integer(CommandArguments.AMOUNT, 1)
                    .optional()
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT)
                    .suggestions((reader, context) -> Lists.newList("1"))
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::giveItem)
        );

        this.registerLiteral(COMMAND_MODEL, false, new String[]{"itemmodel"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_MODEL_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_MODEL)
            .withArguments(Arguments.integer(CommandArguments.VALUE, 0))
            .executes(this::setModelData)
        );

        this.registerLiteral(COMMAND_NAME, true, new String[]{"rename"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_NAME_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_NAME)
            .withArguments(Arguments.greedyString(CommandArguments.TEXT).localized(Lang.COMMAND_ARGUMENT_NAME_TEXT))
            .executes(this::setItemName)
        );

        this.registerLiteral(COMMAND_REPAIR, true, new String[]{"repair", "fix"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_REPAIR_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_REPAIR)
            .executes(this::damageOrRepairItem)
        );

        this.registerLiteral(COMMAND_SPAWN, false, new String[]{"dropitem"}, builder -> builder
            .description(ItemsLang.COMMAND_ITEM_SPAWN_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_SPAWN)
            .withArguments(
                Arguments.itemType(CommandArguments.ITEM),
                Arguments.decimal(CommandArguments.X).suggestions((reader, context) -> CommandArguments.getTargetPosSuggestions(context, Block::getX)),
                Arguments.decimal(CommandArguments.Y).suggestions((reader, context) -> CommandArguments.getTargetPosSuggestions(context, block -> block.getY() + 1)),
                Arguments.decimal(CommandArguments.Z).suggestions((reader, context) -> CommandArguments.getTargetPosSuggestions(context, Block::getZ)),
                Arguments.world(CommandArguments.WORLD).optional(),
                Arguments.integer(CommandArguments.AMOUNT, 1).optional().suggestions((reader, context) -> Lists.newList("1", "8", "16", "32", "64"))
            )
            .executes(this::dropItemAtLocation)
        );

        this.registerLiteral(COMMAND_UNBREAKABLE, true, new String[]{"unbreakable"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_UNBREAKABLE_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_UNBREAKABLE)
            .withArguments(Arguments.bool(CommandArguments.STATE).localized(Lang.COMMAND_ARGUMENT_NAME_STATE).optional())
            .executes(this::setUnbreakable)
        );

        this.registerRoot("item", true, new String[]{"item", "i"},
            map -> {
                map.put(COMMAND_AMOUNT, "amount");
                map.put(COMMAND_DAMAGE, "damage");
                map.put(COMMAND_ENCHANT, "enchant");
                map.put(COMMAND_DISENCHANT, "disenchant");
                map.put(COMMAND_GET, "get");
                map.put(COMMAND_GIVE, "give");
                map.put(COMMAND_MODEL, "model");
                map.put(COMMAND_NAME, "name");
                map.put(COMMAND_REPAIR, "repair");
                map.put(COMMAND_SPAWN, "spawn");
                map.put(COMMAND_UNBREAKABLE, "unbreakable");
            },
            builder -> builder.description(ItemsLang.COMMAND_ITEM_ROOT_DESC).permission(ItemsPerms.COMMAND_ITEM_ROOT)
        );

        // TODO remove, clear commands
        this.registerLiteral(COMMAND_POTION, false, new String[]{"addpotioneffect"}, builder -> builder
            .playerOnly()
            .description(ItemsLang.COMMAND_ITEM_POTION_ADD_DESC)
            .permission(ItemsPerms.COMMAND_ITEM_POTION)
            .withArguments(
                CommandArguments.effect(CommandArguments.TYPE, effectType -> true),
                Arguments.integer(CommandArguments.LEVEL, 0).suggestions((reader, context) -> Lists.newList("0", "1", "5", "10", "127")),
                Arguments.integer(CommandArguments.TIME, 1).suggestions((reader, context) -> Lists.newList("60", "300", "600", "3600"))
            )
            .executes(this::addPotionEffect)
        );
    }

    private boolean setItemStackSize(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            int maxStackSize = itemStack.getMaxStackSize();
            if (maxStackSize == 1) {
                this.module.sendPrefixed(ItemsLang.ERROR_ITEM_NOT_STACKABLE, context.getSender(), builder -> builder.with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack)));
                return false;
            }

            int amount = arguments.getInt(CommandArguments.AMOUNT, this.settings.defaultAmountValue.get());
            int stackSize = Math.min(maxStackSize, amount);
            itemStack.setAmount(stackSize);

            this.module.sendPrefixed(ItemsLang.ITEM_SET_AMOUNT_FEEDBACK, context.getSender(), builder -> builder
                .with(GENERIC_AMOUNT, () -> String.valueOf(stackSize))
                .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
            );
            return true;
        });
    }

    private boolean damageOrRepairItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            if (!ItemStackUtils.isDamageable(itemStack)) {
                this.module.sendPrefixed(ItemsLang.ERROR_ITEM_NOT_DAMAGEABLE, context.getSender(), builder -> builder.with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack)));
                return false;
            }

            MessageLocale locale;
            int amount;

            if (arguments.contains(CommandArguments.AMOUNT)) {
                amount = arguments.getInt(CommandArguments.AMOUNT);
                locale = ItemsLang.ITEM_SET_DAMAGE_FEEDBACK;
            }
            else {
                amount = 0;
                locale = ItemsLang.ITEM_REPAIRED_FEEDBACK;
            }

            int result = ItemStackUtils.setItemDamage(itemStack, amount);

            this.module.sendPrefixed(locale, context.getSender(), builder -> builder
                .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                .with(GENERIC_AMOUNT, () -> String.valueOf(result))
            );
            return true;
        });
    }

    private boolean enchantItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            boolean hasBypass = context.getSender().hasPermission(ItemsPerms.COMMAND_ITEM_ENCHANT_UNLIMITED);
            Enchantment enchantment = arguments.getEnchantment(CommandArguments.ENCHANT);

            if (!hasBypass && !enchantment.canEnchantItem(itemStack)) {
                this.module.sendPrefixed(ItemsLang.ITEM_ADD_ENCHANT_INCOMPATIBLE, context.getSender(), builder -> builder
                    .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                    .with(GENERIC_ENCHANTMENT, () -> LangUtil.getSerializedName(enchantment))
                    .with(GENERIC_NAME, () -> LangUtil.getSerializedName(enchantment)) // old
                );
                return false;
            }

            int level = arguments.getInt(CommandArguments.LEVEL, 1);
            if (!hasBypass && level > enchantment.getMaxLevel()) {
                this.module.sendPrefixed(ItemsLang.ITEM_ADD_ENCHANT_LEVEL_OVERFLOW, context.getSender(), builder -> builder
                    .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                    .with(GENERIC_ENCHANTMENT, () -> LangUtil.getSerializedName(enchantment))
                    .with(GENERIC_NAME, () -> LangUtil.getSerializedName(enchantment)) // old
                    .with(GENERIC_LEVEL, () -> LangUtil.getEnchantmentLevelLang(level))
                );
                return false;
            }

            ItemStackUtils.addEnchantment(itemStack, enchantment, level);

            this.module.sendPrefixed(ItemsLang.ITEM_ADD_ENCHANT_FEEDBACK, context.getSender(), builder -> builder
                .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                .with(GENERIC_ENCHANTMENT, () -> LangUtil.getSerializedName(enchantment))
                .with(GENERIC_NAME, () -> LangUtil.getSerializedName(enchantment)) // old
                .with(GENERIC_LEVEL, () -> LangUtil.getEnchantmentLevelLang(level))
            );
            return true;
        });
    }

    private boolean disenchantItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            if (arguments.contains(CommandArguments.ENCHANT)) {
                Enchantment enchantment = arguments.getEnchantment(CommandArguments.ENCHANT);
                if (!itemStack.containsEnchantment(enchantment)) {
                    this.module.sendPrefixed(ItemsLang.ITEM_DISENCHANT_SINGLE_NOTHING, context.getSender(), builder -> builder
                        .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                        .with(GENERIC_ENCHANTMENT, () -> LangUtil.getSerializedName(enchantment))
                    );
                    return false;
                }

                int oldLevel = itemStack.getEnchantmentLevel(enchantment);
                ItemStackUtils.removeEnchantment(itemStack, enchantment);

                this.module.sendPrefixed(ItemsLang.ITEM_DISENCHANT_SINGLE_FEEDBACK, context.getSender(), builder -> builder
                    .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                    .with(GENERIC_ENCHANTMENT, () -> LangUtil.getSerializedName(enchantment))
                    .with(GENERIC_LEVEL, () -> LangUtil.getEnchantmentLevelLang(oldLevel))
                );
            }
            else {
                if (itemStack.getEnchantments().isEmpty()) {
                    this.module.sendPrefixed(ItemsLang.ITEM_DISENCHANT_FULL_NOTHING, context.getSender(), builder -> builder
                        .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                    );
                    return false;
                }

                ItemStackUtils.removeEnchantments(itemStack);

                this.module.sendPrefixed(ItemsLang.ITEM_DISENCHANT_FULL_FEEDBACK, context.getSender(), builder -> builder
                    .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                );
            }

            return true;
        });
    }

    private boolean getItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ItemStack itemStack = this.createItem(arguments);
        Players.addItem(player, itemStack);

        this.module.sendPrefixed(ItemsLang.ITEM_GET_FEEDBACK, context.getSender(), builder -> builder
            .with(GENERIC_AMOUNT, () -> String.valueOf(itemStack.getAmount()))
            .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
            .with(GENERIC_TYPE, () -> ItemUtil.getNameSerialized(itemStack)) // old
        );
        return true;
    }

    private boolean giveItem(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            ItemStack itemStack = this.createItem(arguments);

            Players.addItem(target, itemStack);

            this.module.sendPrefixed(ItemsLang.ITEM_GIVE_FEEDBACK, context.getSender(), builder -> builder
                .with(CommonPlaceholders.PLAYER.resolver(target))
                .with(GENERIC_AMOUNT, () -> String.valueOf(itemStack.getAmount()))
                .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                .with(GENERIC_TYPE, () -> ItemUtil.getNameSerialized(itemStack)) // old
            );

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(ItemsLang.ITEM_GIVE_NOTIFY, target, builder -> builder
                    .with(GENERIC_AMOUNT, () -> String.valueOf(itemStack.getAmount()))
                    .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                    .with(GENERIC_TYPE, () -> ItemUtil.getNameSerialized(itemStack)) // old
                );
            }
        });
    }

    // TODO More robust model data commands
    private boolean setModelData(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            int modelData = arguments.getInt(CommandArguments.VALUE);
            ItemUtil.editMeta(itemStack, meta -> meta.setCustomModelData(modelData));

            this.module.sendPrefixed(ItemsLang.COMMAND_ITEM_MODEL_DONE, context.getSender(), builder -> builder
                .with(GENERIC_AMOUNT, () -> String.valueOf(modelData))
                .with(GENERIC_NAME, () -> ItemUtil.getNameSerialized(itemStack))
            );
            return true;
        });
    }

    private boolean setItemName(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            String name = arguments.getString(CommandArguments.TEXT);
            String oldName = ItemUtil.getNameSerialized(itemStack);

            ItemUtil.setCustomName(itemStack, name);

            this.module.sendPrefixed(ItemsLang.ITEM_RENAME_FEEDBACK, context.getSender(), builder -> builder
                .with(GENERIC_ITEM, () -> oldName)
                .with(GENERIC_NAME, () -> name)
            );
            return true;
        });
    }

    private boolean addPotionEffect(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            ItemMeta meta = itemStack.getItemMeta();
            if (!(meta instanceof PotionMeta potionMeta)) {
                this.module.sendPrefixed(ItemsLang.ERROR_ITEM_NOT_POTION, context.getSender());
                return false;
            }

            PotionEffectType effectType = arguments.get(CommandArguments.TYPE, PotionEffectType.class);
            int amplifier = arguments.getInt(CommandArguments.LEVEL);

            if (amplifier < 0) {
                potionMeta.removeCustomEffect(effectType);
            }
            else {
                int duration = arguments.getInt(CommandArguments.TIME);
                if (duration <= 0) return false;

                PotionEffect pEffect = new PotionEffect(effectType, duration, amplifier);
                potionMeta.addCustomEffect(pEffect, true);
            }

            this.module.sendPrefixed(ItemsLang.COMMAND_ITEM_POTION_ADD_DONE, context.getSender());
            itemStack.setItemMeta(meta);
            return true;
        });
    }

    private boolean dropItemAtLocation(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        ItemStack itemStack = this.createItem(arguments);

        World world = this.getWorld(context, arguments, CommandArguments.WORLD);
        if (world == null) return false;

        double x = arguments.getDouble(CommandArguments.X);
        double y = arguments.getDouble(CommandArguments.Y);
        double z = arguments.getDouble(CommandArguments.Z);
        Location location = new Location(world, x, y, z);

        int maxStackSize = itemStack.getMaxStackSize();
        int amountToDrop = itemStack.getAmount();

        while (amountToDrop > 0) {
            ItemStack copy = new ItemStack(itemStack);
            copy.setAmount(Math.min(maxStackSize, amountToDrop));
            amountToDrop -= copy.getAmount();

            world.dropItemNaturally(location, copy);
        }

        this.module.sendPrefixed(ItemsLang.ITEM_DROP_FEEDBACK, context.getSender(), builder -> builder
            .with(GENERIC_AMOUNT, () -> String.valueOf(itemStack.getAmount()))
            .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
            .with(GENERIC_TYPE, () -> ItemUtil.getNameSerialized(itemStack)) // old
            .with(CommonPlaceholders.LOCATION.resolver(location))
        );
        return true;
    }

    private boolean setUnbreakable(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return CommandArguments.handleItemInHandOrError(context, (player, itemStack) -> {
            if (!ItemStackUtils.isDamageable(itemStack)) {
                this.module.sendPrefixed(ItemsLang.ERROR_ITEM_NOT_DAMAGEABLE, context.getSender(), builder -> builder.with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack)));
                return false;
            }

            ItemUtil.editMeta(itemStack, meta -> {
                boolean state = arguments.contains(CommandArguments.STATE) ? arguments.getBoolean(CommandArguments.STATE) : !meta.isUnbreakable();

                meta.setUnbreakable(state);

                this.module.sendPrefixed(ItemsLang.ITEM_SET_UNBREAKABLE_FEEDBACK, context.getSender(), builder -> builder
                    .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(itemStack))
                    .with(GENERIC_STATE, () -> CoreLang.STATE_ENABLED_DISALBED.get(meta.isUnbreakable()))
                );
            });

            return true;
        });
    }

    @NotNull
    private ItemStack createItem(@NotNull ParsedArguments arguments) {
        Material material = arguments.getMaterial(CommandArguments.ITEM);
        int amount = arguments.getInt(CommandArguments.AMOUNT, 1);

        int maxStackSize = material.getMaxStackSize();
        int maxStacks = this.settings.maxItemStacksAmount.get();
        int expectedStacks = (int) Math.ceil((double) amount / (double) maxStackSize);

        int itemAmount = expectedStacks > maxStacks ? (maxStackSize * maxStacks) : amount;

        return new ItemStack(material, itemAmount);
    }
}
