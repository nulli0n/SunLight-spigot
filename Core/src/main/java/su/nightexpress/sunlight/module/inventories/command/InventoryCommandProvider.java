package su.nightexpress.sunlight.module.inventories.command;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.inventories.InventoriesModule;
import su.nightexpress.sunlight.module.inventories.InventoriesPerms;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.ItemStackUtils;

import java.util.Map;
import java.util.Optional;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_ITEM;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class InventoryCommandProvider extends AbstractCommandProvider {

    private static final String COMMAND_CLEAR  = "clear";
    private static final String COMMAND_COPY   = "copy";
    private static final String COMMAND_FILL   = "fill";
    private static final String COMMAND_OPEN   = "open";
    private static final String COMMAND_REPAIR = "repair";

    private static final Permission PERMISSION_ROOT          = InventoriesPerms.COMMAND.permission("inventory.root");
    private static final Permission PERMISSION_CLEAR         = InventoriesPerms.COMMAND.permission("inventory.clear");
    private static final Permission PERMISSION_CLEAR_OTHERS  = InventoriesPerms.COMMAND.permission("inventory.clear.others");
    private static final Permission PERMISSION_COPY          = InventoriesPerms.COMMAND.permission("inventory.copy");
    private static final Permission PERMISSION_COPY_OTHERS   = InventoriesPerms.COMMAND.permission("inventory.copy.others");
    private static final Permission PERMISSION_FILL          = InventoriesPerms.COMMAND.permission("inventory.fill");
    private static final Permission PERMISSION_OPEN          = InventoriesPerms.COMMAND.permission("inventory.open");
    private static final Permission PERMISSION_REPAIR        = InventoriesPerms.COMMAND.permission("inventory.repair");
    private static final Permission PERMISSION_REPAIR_OTHERS = InventoriesPerms.COMMAND.permission("inventory.repair.others");

    private static final TextLocale DESCRIPTION_ROOT   = LangEntry.builder("Command.Inventory.Root.Desc").text("Inventory management commands.");
    private static final TextLocale DESCRIPTION_CLEAR  = LangEntry.builder("Command.Inventory.Clear.Desc").text("Clear inventory.");
    private static final TextLocale DESCRIPTION_COPY   = LangEntry.builder("Command.Inventory.Copy.Desc").text("Copy player's inventory.");
    private static final TextLocale DESCRIPTION_FILL   = LangEntry.builder("Command.Inventory.Fill.Desc").text("Fill player's inventory with certain item.");
    private static final TextLocale DESCRIPTION_OPEN   = LangEntry.builder("Command.Inventory.Open.Desc").text("Open player's inventory.");
    private static final TextLocale DESCRIPTION_REPAIR = LangEntry.builder("Command.Inventory.Repair.Desc").text("Repair all items in the inventory.");

    private static final MessageLocale MESSAGE_CLEAR_FEEDBACK = LangEntry.builder("Command.Inventory.Clear.Done.Target").chatMessage(
        Sound.BLOCK_FIRE_EXTINGUISH,
        GRAY.wrap("You have cleared " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s inventory.")
    );

    private static final MessageLocale MESSAGE_CLEAR_NOTIFY = LangEntry.builder("Command.Inventory.Clear.Done.Notify").chatMessage(
        Sound.BLOCK_FIRE_EXTINGUISH,
        GRAY.wrap("Your inventory has been cleared.")
    );

    private static final MessageLocale MESSAGE_COPY_NOTIFY = LangEntry.builder("Command.Inventory.Copy.Done.Notify").chatMessage(
        Sound.ITEM_ARMOR_EQUIP_LEATHER,
        GRAY.wrap("You have copied " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s inventory contents.")
    );

    private static final MessageLocale MESSAGE_COPY_YOURSELF = LangEntry.builder("Inventories.Command.Inventory.Copy.NotYourself").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can not copy your own " + ORANGE.wrap("Inventory") + ".")
    );

    private static final MessageLocale MESSAGE_FILL_FEEDBACK = LangEntry.builder("Command.Inventory.Fill.Done").chatMessage(
        Sound.ENTITY_ITEM_PICKUP,
        GRAY.wrap("You have filled up all empty slots in the " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s inventory with " + SOFT_YELLOW.wrap(GENERIC_ITEM) + ".")
    );

    private static final MessageLocale MESSAGE_OPEN_FEEDBACK = LangEntry.builder("Command.Inventory.Open.Done").chatMessage(
        Sound.BLOCK_CHEST_OPEN,
        GRAY.wrap("You have opened " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s inventory.")
    );

    private static final MessageLocale MESSAGE_OPEN_YOURSELF = LangEntry.builder("Inventories.Command.Inventory.Open.NotYourself").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can not open your own " + ORANGE.wrap("Inventory") + " using this command.")
    );

    private static final MessageLocale MESSAGE_REPAIR_NOTIFY = LangEntry.builder("Command.Inventory.Repair.Notify").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("All items in your inventory have been repaired.")
    );

    private static final MessageLocale MESSAGE_REPAIR_FEEDBACK = LangEntry.builder("Command.Inventory.Repair.Target").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have repaired all items in the " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s inventory.")
    );

    private final InventoriesModule module;
    private final UserManager userManager;
    private final SunNMS internals;

    public InventoryCommandProvider(@NotNull SunLightPlugin plugin, @NotNull InventoriesModule module, @NotNull UserManager userManager, @Nullable SunNMS internals) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
        this.internals = internals;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_CLEAR, true, new String[]{"clearinv", "clearinventory", "ci"}, builder -> builder
            .description(DESCRIPTION_CLEAR)
            .permission(PERMISSION_CLEAR)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_CLEAR_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::clearInventory)
        );

        this.registerLiteral(COMMAND_COPY, false, new String[]{"copyinv", "copyinventory"}, builder -> builder
            .description(DESCRIPTION_COPY)
            .permission(PERMISSION_COPY)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.playerName(CommandArguments.TARGET).permission(PERMISSION_COPY_OTHERS).optional()
            )
            .executes(this::copyInventory)
        );

        this.registerLiteral(COMMAND_FILL, false, new String[]{"fillinv", "fillinventory"}, builder -> builder
            .description(DESCRIPTION_FILL)
            .permission(PERMISSION_FILL)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.itemType(CommandArguments.ITEM)
            )
            .executes(this::fillInventory)
        );

        this.registerLiteral(COMMAND_OPEN, true, new String[]{"openinv", "openinventory", "invsee"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION_OPEN)
            .permission(PERMISSION_OPEN)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER))
            .executes(this::openInventory)
        );

        this.registerLiteral(COMMAND_REPAIR, true, new String[]{"repairinv", "repairinventory", "fixall", "repairall"}, builder -> builder
            .description(DESCRIPTION_REPAIR)
            .permission(PERMISSION_REPAIR)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_REPAIR_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::repairInventoryItems)
        );

        this.registerRoot("Inventory", true, new String[]{"inventory", "inv"},
            Map.of(
                COMMAND_CLEAR, "clear",
                COMMAND_COPY, "copy",
                COMMAND_FILL, "fill",
                COMMAND_OPEN, "open",
                COMMAND_REPAIR, "repair"
            ),
            builder -> builder.description(DESCRIPTION_ROOT).permission(PERMISSION_ROOT)
        );
    }

    @NonNull
    private Optional<Inventory> getInventory(@NonNull CommandContext context, @NonNull Player target) {
        if (this.internals != null) {
            return Optional.of(this.internals.getPlayerInventory(target));
        }

        if (!target.isOnline()) {
            this.module.sendPrefixed(Lang.ERROR_NO_INTERNALS_HANDLER, context.getSender());
            return Optional.empty();
        }

        return Optional.of(target.getInventory());
    }

    private boolean clearInventory(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            this.getInventory(context, target).ifPresent(inventory -> {
                inventory.clear();

                if (context.getSender() != target) {
                    this.module.sendPrefixed(MESSAGE_CLEAR_FEEDBACK, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
                }
                if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                    this.module.sendPrefixed(MESSAGE_CLEAR_NOTIFY, target);
                }
            });
        });
    }

    private boolean copyInventory(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            if (target == player) {
                this.module.sendPrefixed(MESSAGE_COPY_YOURSELF, context.getSender());
                return;
            }

            Inventory sourceInventory = this.getInventory(context, target).orElse(null);
            Inventory targetInventory = this.getInventory(context, player).orElse(null);
            if (sourceInventory == null || targetInventory == null) return;

            for (int slot = 0; slot < targetInventory.getSize(); slot++) {
                targetInventory.setItem(slot, sourceInventory.getItem(slot));
            }

            this.module.sendPrefixed(MESSAGE_COPY_NOTIFY, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
        });
    }

    private boolean fillInventory(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            this.getInventory(context, target).ifPresent(inventory -> {
                Material material = arguments.getMaterial(CommandArguments.ITEM);

                for (int slot = 0; slot < 36; slot++) {
                    ItemStack has = inventory.getItem(slot);
                    if (has == null || has.getType().isAir()) {
                        inventory.setItem(slot, new ItemStack(material));
                    }
                }

                this.module.sendPrefixed(MESSAGE_FILL_FEEDBACK, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(GENERIC_ITEM, () -> LangUtil.getSerializedName(material))
                );
            });
        });
    }

    private boolean openInventory(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            if (this.internals == null) {
                this.module.sendPrefixed(Lang.ERROR_NO_INTERNALS_HANDLER, context.getSender());
                return;
            }

            if (target == context.getSender()) {
                this.module.sendPrefixed(MESSAGE_OPEN_YOURSELF, context.getSender());
                return;
            }

            this.internals.openPlayerInventory(player, target);
            this.module.sendPrefixed(MESSAGE_OPEN_FEEDBACK, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
        });
    }

    private boolean repairInventoryItems(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            this.getInventory(context, target).ifPresent(inventory -> {
                inventory.forEach(ItemStackUtils::repairItem);

                if (context.getSender() != target) {
                    this.module.sendPrefixed(MESSAGE_REPAIR_FEEDBACK, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
                }
                if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                    this.module.sendPrefixed(MESSAGE_REPAIR_NOTIFY, target);
                }
            });
        });
    }
}
