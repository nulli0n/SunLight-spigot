package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.ItemUtil;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.user.UserManager;

import static su.nightexpress.nightcore.util.Placeholders.PLAYER_DISPLAY_NAME;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class EnchantCommandsProvider extends AbstractCommandProvider {

    private static final String ARG_SLOT    = "slot";
    private static final String ARG_ENCHANT = "enchant";
    private static final String ARG_LEVEL   = "level";

    private static final Permission PERMISSION_ENCHANT           = EssentialPerms.COMMAND.permission("enchant");
    private static final Permission PERMISSION_ENCHANT_OTHERS    = EssentialPerms.COMMAND.permission("enchant.others");
    private static final Permission PERMISSION_DISENCHANT        = EssentialPerms.COMMAND.permission("disenchant");
    private static final Permission PERMISSION_DISENCHANT_OTHERS = EssentialPerms.COMMAND.permission(
        "disenchant.others");

    private static final TextLocale DESCRIPTION_ENCHANT    = LangEntry.builder("Command.Enchant.Desc").text(
        "Enchant item in a slot.");
    private static final TextLocale DESCRIPTION_DISENCHANT = LangEntry.builder("Command.Disenchant.Desc").text(
        "Disenchant item in a slot.");

    private static final MessageLocale MESSAGE_ERROR_NO_ITEM = LangEntry.builder("Command.Enchant.EmptySlot")
        .chatMessage(
            GRAY.wrap("There is no item in the " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + SOFT_RED.wrap(
                GENERIC_SLOT) + " slot.")
        );

    private static final MessageLocale MESSAGE_ERROR_NO_ENCHANT = LangEntry.builder("Command.Disenchant.NotEnchanted")
        .chatMessage(
            GRAY.wrap("The " + SOFT_RED.wrap(GENERIC_ITEM) + " item in the " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " +
                SOFT_RED.wrap(GENERIC_SLOT) + " slot does not contain " + SOFT_RED.wrap(GENERIC_ENCHANTMENT) +
                " enchantment to remove.")
        );

    private static final MessageLocale MESSAGE_ENCHANTED_FEEDBACK = LangEntry.builder(
        "Command.Enchant.Enchanted.Target").chatMessage(
            Sound.BLOCK_ENCHANTMENT_TABLE_USE,
            GRAY.wrap("You have enchanted " + WHITE.wrap(GENERIC_ITEM) + " with " +
                ORANGE.wrap(GENERIC_NAME + " " + GENERIC_AMOUNT) + " in " +
                WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + WHITE.wrap(GENERIC_TYPE) + " slot.")
        );

    private static final MessageLocale MESSAGE_ENCHANTED_NOTIFY = LangEntry.builder("Command.Enchant.Enchanted.Notify")
        .chatMessage(
            Sound.BLOCK_ENCHANTMENT_TABLE_USE,
            GRAY.wrap("Your " + WHITE.wrap(GENERIC_ITEM) + " has been enchanted with " + ORANGE.wrap(GENERIC_NAME +
                " " + GENERIC_AMOUNT) + ".")
        );

    private static final MessageLocale MESSAGE_DISENCHANTED_SPECIFIC_FEEDBACK = LangEntry.builder(
        "Command.Disenchant.Specific.Target").chatMessage(
            Sound.BLOCK_GRINDSTONE_USE,
            GRAY.wrap("You have removed " + ORANGE.wrap(GENERIC_ENCHANTMENT) + " from " +
                WHITE.wrap(GENERIC_ITEM) + " in " +
                WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + WHITE.wrap(GENERIC_TYPE) + " slot.")
        );

    private static final MessageLocale MESSAGE_DISENCHANTED_SPECIFIC_NOTIFY = LangEntry.builder(
        "Command.Disenchant.Specific.Notify").chatMessage(
            Sound.BLOCK_GRINDSTONE_USE,
            GRAY.wrap("Your " + WHITE.wrap(GENERIC_ITEM) + " has been disenchanted from " + ORANGE.wrap(
                GENERIC_ENCHANTMENT) + ".")
        );

    private static final MessageLocale MESSAGE_DISENCHANTED_ALL_FEEDBACK = LangEntry.builder(
        "Command.Disenchant.All.Target").chatMessage(
            Sound.BLOCK_GRINDSTONE_USE,
            GRAY.wrap("You have disenchanted " + WHITE.wrap(GENERIC_ITEM) + " in " + WHITE.wrap(PLAYER_DISPLAY_NAME) +
                "'s " + WHITE.wrap(GENERIC_TYPE) + " slot.")
        );

    private static final MessageLocale MESSAGE_DISENCHANTED_ALL_NOTIFY = LangEntry.builder(
        "Command.Disenchant.All.Notify").chatMessage(
            Sound.BLOCK_GRINDSTONE_USE,
            GRAY.wrap("Your " + WHITE.wrap(GENERIC_ITEM) + " has been disenchanted.")
        );

    private final EssentialModule module;
    private final UserManager     userManager;

    public EnchantCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("enchant", true, new String[]{"enchant"}, builder -> builder
            .description(DESCRIPTION_ENCHANT)
            .permission(PERMISSION_ENCHANT)
            .withArguments(
                CommandArguments.slot(ARG_SLOT),
                Arguments.enchantment(ARG_ENCHANT),
                Arguments.integer(ARG_LEVEL, 1).suggestions((reader, context) -> Lists.newList("0", "1", "5", "10",
                    "127")).optional(),
                Arguments.playerName(CommandArguments.PLAYER).optional().permission(PERMISSION_ENCHANT_OTHERS)
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::enchantSlot)
        );

        this.registerLiteral("disenchant", true, new String[]{"disenchant"}, builder -> builder
            .description(DESCRIPTION_DISENCHANT)
            .permission(PERMISSION_DISENCHANT)
            .withArguments(
                CommandArguments.slot(ARG_SLOT),
                Arguments.enchantment(ARG_ENCHANT).optional(),
                Arguments.playerName(CommandArguments.PLAYER).optional().permission(PERMISSION_DISENCHANT_OTHERS)
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::disenchantSlot)
        );
    }

    private boolean enchantSlot(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            EquipmentSlot slot = arguments.get(ARG_SLOT, EquipmentSlot.class);

            ItemStack item = target.getInventory().getItem(slot);
            if (item.getType().isAir() || item.getItemMeta() == null) {
                this.module.sendPrefixed(MESSAGE_ERROR_NO_ITEM, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(GENERIC_SLOT, () -> Lang.EQUIPMENT_SLOT.getLocalized(slot))
                );
                return;
            }

            Enchantment enchant = arguments.getEnchantment(ARG_ENCHANT);
            int level = arguments.getInt(ARG_LEVEL);

            ItemMeta meta = item.getItemMeta();
            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                storageMeta.addStoredEnchant(enchant, level, true);
                item.setItemMeta(storageMeta);
            }
            else {
                meta.addEnchant(enchant, level, true);
                item.setItemMeta(meta);
            }

            if (target != context.getSender()) {
                this.module.sendPrefixed(MESSAGE_ENCHANTED_FEEDBACK, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_TYPE, () -> Lang.EQUIPMENT_SLOT.getLocalized(slot))
                    .with(SLPlaceholders.GENERIC_ITEM, () -> ItemUtil.getNameSerialized(item))
                    .with(SLPlaceholders.GENERIC_NAME, () -> LangUtil.getSerializedName(enchant))
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> LangUtil.getEnchantmentLevelLang(level))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_ENCHANTED_NOTIFY, target, replacer -> replacer
                    .with(SLPlaceholders.GENERIC_TYPE, () -> Lang.EQUIPMENT_SLOT.getLocalized(slot))
                    .with(SLPlaceholders.GENERIC_ITEM, () -> ItemUtil.getNameSerialized(item))
                    .with(SLPlaceholders.GENERIC_NAME, () -> LangUtil.getSerializedName(enchant))
                    .with(SLPlaceholders.GENERIC_AMOUNT, () -> LangUtil.getEnchantmentLevelLang(level))
                );
            }
        });
    }

    private boolean disenchantSlot(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            EquipmentSlot slot = arguments.get(ARG_SLOT, EquipmentSlot.class);

            ItemStack item = target.getInventory().getItem(slot);
            if (item.getType().isAir() || item.getItemMeta() == null) {
                this.module.sendPrefixed(MESSAGE_ERROR_NO_ITEM, context.getSender(), replacer -> replacer
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(GENERIC_SLOT, () -> Lang.EQUIPMENT_SLOT.getLocalized(slot))
                );
                return;
            }

            ItemMeta meta = item.getItemMeta();
            Enchantment enchant = arguments.contains(ARG_ENCHANT) ? arguments.getEnchantment(ARG_ENCHANT) : null;
            boolean hasEnchant = enchant != null;

            if (hasEnchant) {
                boolean isEnchanted = (meta instanceof EnchantmentStorageMeta storageMeta && storageMeta
                    .hasStoredEnchant(enchant) || meta.hasEnchant(enchant));
                if (!isEnchanted) {
                    this.module.sendPrefixed(MESSAGE_ERROR_NO_ENCHANT, context.getSender(), replacer -> replacer
                        .with(CommonPlaceholders.PLAYER.resolver(target))
                        .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(item))
                        .with(GENERIC_SLOT, () -> Lang.EQUIPMENT_SLOT.getLocalized(slot))
                        .with(GENERIC_ENCHANTMENT, () -> LangUtil.getSerializedName(enchant))
                    );
                    return;
                }
            }

            if (meta instanceof EnchantmentStorageMeta storageMeta) {
                if (hasEnchant) storageMeta.removeStoredEnchant(enchant);
                else storageMeta.getStoredEnchants().keySet().forEach(storageMeta::removeStoredEnchant);
            }
            else {
                if (hasEnchant) meta.removeEnchant(enchant);
                else meta.removeEnchantments();
            }

            item.setItemMeta(meta);

            if (target != context.getSender()) {
                this.module.sendPrefixed(
                    (hasEnchant ? MESSAGE_DISENCHANTED_SPECIFIC_FEEDBACK : MESSAGE_DISENCHANTED_ALL_FEEDBACK), context
                        .getSender(), replacer -> replacer
                            .with(CommonPlaceholders.PLAYER.resolver(target))
                            .with(GENERIC_SLOT, () -> Lang.EQUIPMENT_SLOT.getLocalized(slot))
                            .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(item))
                            .with(GENERIC_ENCHANTMENT, () -> hasEnchant ? LangUtil.getSerializedName(enchant) : "")
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(
                    hasEnchant ? MESSAGE_DISENCHANTED_SPECIFIC_NOTIFY : MESSAGE_DISENCHANTED_ALL_NOTIFY, target,
                    replacer -> replacer
                        .with(GENERIC_SLOT, () -> Lang.EQUIPMENT_SLOT.getLocalized(slot))
                        .with(GENERIC_ITEM, () -> ItemUtil.getNameSerialized(item))
                        .with(GENERIC_ENCHANTMENT, () -> hasEnchant ? LangUtil.getSerializedName(enchant) : "")
                );
            }
        });
    }
}
