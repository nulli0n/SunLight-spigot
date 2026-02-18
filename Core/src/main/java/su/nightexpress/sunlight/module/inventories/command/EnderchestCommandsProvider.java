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
import su.nightexpress.sunlight.SLPlaceholders;
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

public class EnderchestCommandsProvider extends AbstractCommandProvider {

    private static final String COMMAND_CLEAR  = "clear";
    private static final String COMMAND_COPY   = "copy";
    private static final String COMMAND_FILL   = "fill";
    private static final String COMMAND_OPEN   = "open";
    private static final String COMMAND_REPAIR = "repair";

    private static final Permission PERMISSION_HUB           = InventoriesPerms.COMMAND.permission("enderchest.hub");
    private static final Permission PERMISSION_CLEAR         = InventoriesPerms.COMMAND.permission("enderchest.clear");
    private static final Permission PERMISSION_CLEAR_OTHERS  = InventoriesPerms.COMMAND.permission("enderchest.clear.others");
    private static final Permission PERMISSION_COPY          = InventoriesPerms.COMMAND.permission("enderchest.copy");
    private static final Permission PERMISSION_FILL          = InventoriesPerms.COMMAND.permission("enderchest.fill");
    private static final Permission PERMISSION_OPEN          = InventoriesPerms.COMMAND.permission("enderchest.open");
    private static final Permission PERMISSION_OPEN_OTHERS   = InventoriesPerms.COMMAND.permission("enderchest.open.others");
    private static final Permission PERMISSION_REPAIR        = InventoriesPerms.COMMAND.permission("enderchest.repair");
    private static final Permission PERMISSION_REPAIR_OTHERS = InventoriesPerms.COMMAND.permission("enderchest.repair.others");

    private static final TextLocale DESCRIPTION_HUB    = LangEntry.builder("Command.Enderchest.Hub.Desc").text("Ender Chest commands.");
    private static final TextLocale DESCRIPTION_CLEAR  = LangEntry.builder("Command.Enderchest.Clear.Desc").text("Clear Ender Chest.");
    private static final TextLocale DESCRIPTION_COPY   = LangEntry.builder("Command.Enderchest.Copy.Desc").text("Copy player's Ender Chest.");
    private static final TextLocale DESCRIPTION_FILL   = LangEntry.builder("Command.Enderchest.Fill.Desc").text("Fill Ender Chest with specified item.");
    private static final TextLocale DESCRIPTION_OPEN   = LangEntry.builder("Command.Enderchest.Open.Desc").text("Open Ender Chest.");
    private static final TextLocale DESCRIPTION_REPAIR = LangEntry.builder("Command.Enderchest.Repair.Desc").text("Repair all Ender Chest items.");

    private static final MessageLocale MESSAGE_CLEAR_TARGETTED = LangEntry.builder("Command.Enderchest.Clear.Done.Target").chatMessage(
        Sound.BLOCK_FIRE_EXTINGUISH,
        GRAY.wrap("You have cleared " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + SOFT_PURPLE.wrap("Ender Chest."))
    );

    private static final MessageLocale MESSAGE_CLEAR_NOTIFY = LangEntry.builder("Command.Enderchest.Clear.Done.Notify").chatMessage(
        Sound.BLOCK_FIRE_EXTINGUISH,
        GRAY.wrap("Your " + SOFT_PURPLE.wrap("Ender Chest") + " has been cleared.")
    );


    private static final MessageLocale MESSAGE_COPY_NOTIFY = LangEntry.builder("Command.Enderchest.Copy.Done.Executor").chatMessage(
        Sound.ITEM_ARMOR_EQUIP_LEATHER,
        GRAY.wrap("You have copied " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + SOFT_PURPLE.wrap("Ender Chest") + " content to yours.")
    );

    private static final MessageLocale MESSAGE_COPY_YOURSELF = LangEntry.builder("Inventories.Command.Enderchest.Copy.NotYourself").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You can not copy your own " + SOFT_PURPLE.wrap("Ender Chest") + ".")
    );


    private static final MessageLocale MESSAGE_FILL_TARGETTED = LangEntry.builder("Command.Enderchest.Fill.Done.Executor").chatMessage(
        Sound.ENTITY_ITEM_PICKUP,
        GRAY.wrap("You have filled " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + SOFT_PURPLE.wrap("Ender Chest") + " with " + WHITE.wrap(GENERIC_ITEM + "."))
    );


    private static final MessageLocale MESSAGE_OPEN_NOTIFY = LangEntry.builder("Command.Enderchest.Open.Notify").chatMessage(
        Sound.BLOCK_ENDER_CHEST_OPEN,
        GRAY.wrap("You have opened your " + SOFT_PURPLE.wrap("Ender Chest."))
    );

    private static final MessageLocale MESSAGE_OPEN_TARGETTED = LangEntry.builder("Command.Enderchest.Open.Targetted").chatMessage(
        Sound.BLOCK_ENDER_CHEST_OPEN,
        GRAY.wrap("You have opened " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + SOFT_PURPLE.wrap("Ender Chest."))
    );


    private static final MessageLocale MESSAGE_REPAIR_NOTIFY = LangEntry.builder("Command.Enderchest.Repair.Notify").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("All items in your " + SOFT_PURPLE.wrap("Ender Chest") + " has been repaired.")
    );

    private static final MessageLocale MESSAGE_REPAIR_TARGETTED = LangEntry.builder("Command.Enderchest.Repair.Target").chatMessage(
        Sound.BLOCK_ANVIL_USE,
        GRAY.wrap("You have repaired all items in " + WHITE.wrap(PLAYER_DISPLAY_NAME) + "'s " + SOFT_PURPLE.wrap("Ender Chest."))
    );

    private final InventoriesModule module;
    private final UserManager userManager;
    private final SunNMS internals;

    public EnderchestCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull InventoriesModule module, @NotNull UserManager userManager, @Nullable SunNMS internals) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
        this.internals = internals;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_CLEAR, false, new String[]{"enderclear", "clearender"}, builder -> builder
            .description(DESCRIPTION_CLEAR)
            .permission(PERMISSION_CLEAR)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_CLEAR_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::executeClear)
        );

        this.registerLiteral(COMMAND_COPY, false, new String[]{"endercopy", "copyender"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION_COPY)
            .permission(PERMISSION_COPY)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER))
            .executes(this::executeCopy)
        );

        this.registerLiteral(COMMAND_FILL, false, new String[]{"enderfill", "fillender"}, builder -> builder
            .description(DESCRIPTION_FILL)
            .permission(PERMISSION_FILL)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.itemType(CommandArguments.ITEM)
            )
            .executes(this::executeFill)
        );

        this.registerLiteral(COMMAND_OPEN, true, new String[]{"ec", "endersee"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION_OPEN)
            .permission(PERMISSION_OPEN)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_OPEN_OTHERS).optional())
            .executes(this::executeOpen)
        );

        this.registerLiteral(COMMAND_REPAIR, false, new String[]{"repairender", "fixender"}, builder -> builder
            .description(DESCRIPTION_REPAIR)
            .permission(PERMISSION_REPAIR)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_REPAIR_OTHERS).optional())
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::executeRepair)
        );

        this.registerRoot("EnderChest", true, new String[]{"enderchest", "echest"},
            Map.of(
                COMMAND_CLEAR, "clear",
                COMMAND_COPY, "copy",
                COMMAND_FILL, "fill",
                COMMAND_OPEN, "open",
                COMMAND_REPAIR, "repair"
            ), builder -> builder
            .description(DESCRIPTION_HUB)
            .permission(PERMISSION_HUB)
        );
    }

    @NonNull
    private Optional<Inventory> getEnderChest(@NonNull CommandContext context, @NonNull Player target) {
        if (this.internals != null) {
            return Optional.of(this.internals.getPlayerEnderChest(target));
        }

        if (!target.isOnline()) {
            this.module.sendPrefixed(Lang.ERROR_NO_INTERNALS_HANDLER, context.getSender());
            return Optional.empty();
        }

        return Optional.of(target.getEnderChest());
    }

    public boolean executeClear(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            this.getEnderChest(context, target).ifPresent(inventory -> {
                inventory.clear();

                if (context.getSender() != target) {
                    this.module.sendPrefixed(MESSAGE_CLEAR_TARGETTED, context.getSender(), builder -> builder
                        .with(CommonPlaceholders.PLAYER.resolver(target))
                    );
                }

                if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                    this.module.sendPrefixed(MESSAGE_CLEAR_NOTIFY, target);
                }
            });
        });
    }

    public boolean executeCopy(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            if (target == player) {
                this.module.sendPrefixed(MESSAGE_COPY_YOURSELF, context.getSender());
                return;
            }

            Inventory invFrom = this.getEnderChest(context, target).orElse(null);
            Inventory invTo = this.getEnderChest(context, player).orElse(null);
            if (invFrom == null || invTo == null) return;

            for (int slot = 0; slot < invTo.getSize(); slot++) {
                invTo.setItem(slot, invFrom.getItem(slot));
            }

            this.module.sendPrefixed(MESSAGE_COPY_NOTIFY, context.getSender(), builder -> builder
                .with(CommonPlaceholders.PLAYER.resolver(target))
            );
        });
    }

    public boolean executeFill(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            this.getEnderChest(context, target).ifPresent(inventory -> {
                Material material = arguments.getMaterial(CommandArguments.ITEM);

                for (int slot = 0; slot < inventory.getSize(); slot++) {
                    ItemStack has = inventory.getItem(slot);
                    if (has == null || has.getType().isAir()) {
                        inventory.setItem(slot, new ItemStack(material));
                    }
                }

                this.module.sendPrefixed(MESSAGE_FILL_TARGETTED, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_ITEM, () -> LangUtil.getSerializedName(material))
                );
            });
        });
    }

    public boolean executeOpen(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            this.getEnderChest(context, target).ifPresent(inventory -> {
                Player player = context.getPlayerOrThrow();
                player.openInventory(inventory);

                if (player != target) {
                    this.module.sendPrefixed(MESSAGE_OPEN_TARGETTED, player, builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
                }
                else {
                    this.module.sendPrefixed(MESSAGE_OPEN_NOTIFY, player);
                }
            });
        });
    }

    public boolean executeRepair(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerAndRunInMainThread(context, arguments, this.module, this.userManager, target -> {
            this.getEnderChest(context, target).ifPresent(inventory -> {
                inventory.forEach(ItemStackUtils::repairItem);

                if (context.getSender() != target) {
                    this.module.sendPrefixed(MESSAGE_REPAIR_TARGETTED, context.getSender(), builder -> builder.with(CommonPlaceholders.PLAYER.resolver(target)));
                }

                if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                    this.module.sendPrefixed(MESSAGE_REPAIR_NOTIFY, target);
                }
            });
        });
    }
}
