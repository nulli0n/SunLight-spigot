package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;

public class HatCommandProvider extends AbstractCommandProvider {

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Hat.Desc").text("Put item in head.");

    private static final Permission PERMISSION = EssentialPerms.COMMAND.permission("hat");

    private static final MessageLocale MESSAGE_HAT_FEEDBACK = LangEntry.builder("Command.Hat.Done").chatMessage(
        Sound.ITEM_ARMOR_EQUIP_LEATHER,
        GRAY.wrap("Enjoy your new hat!")
    );

    private static final MessageLocale MESSAGE_EMPTY_HAND = LangEntry.builder("Command.Hat.EmptyHand").chatMessage(
        Sound.ENTITY_VILLAGER_NO,
        GRAY.wrap("You must hold an item in your hand to equip it!")
    );

    private final EssentialModule module;

    public HatCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("hat", true, new String[]{"hat"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .executes(this::equipHat)
        );
    }

    private boolean equipHat(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            this.module.sendPrefixed(MESSAGE_EMPTY_HAND, player);
            return false;
        }

        EquipmentSlot slot = EquipmentSlot.HEAD;
        ItemStack oldItem = EntityUtil.getItemInSlot(player, EquipmentSlot.HEAD);
        player.getInventory().setItemInMainHand(null);
        player.getInventory().setItem(slot, item);

        if (oldItem != null && !oldItem.getType().isAir()) {
            Players.addItem(player, oldItem);
        }

        this.module.sendPrefixed(MESSAGE_HAT_FEEDBACK, player);
        return true;
    }
}
