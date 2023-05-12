package su.nightexpress.sunlight.module.kits.editor;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.ArrayList;
import java.util.List;

public enum KitsEditorType implements EditorButtonType {

    KIT_OBJECT(Material.ARMOR_STAND, Placeholders.KIT_NAME + " &7(&f" + Placeholders.KIT_ID + "&7)",
        EditorButtonType.info("Priority: &f" + Placeholders.KIT_PRIORITY + "\nPermission: &f" + Placeholders.KIT_PERMISSION_REQUIRED),
        EditorButtonType.click("Left-Click to &fEdit\nShift-Right to &fDelete &7(No Undo)")),
    KIT_CREATE(Material.ANVIL, "Create Kit",
        EditorButtonType.info("Create a new kit."),
        EditorButtonType.click("Left-Click to &fCreate")),
    KIT_CHANGE_NAME(Material.NAME_TAG, "Kit Display Name",
        EditorButtonType.current(Placeholders.KIT_NAME),
        EditorButtonType.info("Sets the kit display name. It's used in GUIs, messages, etc."),
        EditorButtonType.click("Left-Click to &fChange")),
    KIT_CHANGE_DESCRIPTION(Material.MAP, "Kit Description",
        EditorButtonType.current(Placeholders.KIT_DESCRIPTION),
        EditorButtonType.info("Sets the kit description. Provide a brief explain of the kit for the players."),
        EditorButtonType.click("Left-Click to &fAdd Line\nRight-Click to &fClear")),
    KIT_CHANGE_COOLDOWN(Material.CLOCK, "Usage Cooldown",
        EditorButtonType.current(Placeholders.KIT_COOLDOWN),
        EditorButtonType.info("Sets the kit usage cooldown (in seconds). Players will have to wait certain time before they can use it again."),
        EditorButtonType.note("Set this to -1 for one-time usage.\nSet this to 0 for no cooldown."),
        EditorButtonType.click("Left-Click to &fChange\nRight-Click to &fOne-Timed")),
    KIT_CHANGE_PERMISSION(Material.REDSTONE_TORCH, "Permission Requirement",
        EditorButtonType.current("Required: &f" + Placeholders.KIT_PERMISSION_REQUIRED + "\nNode: &f" + Placeholders.KIT_PERMISSION_NODE),
        EditorButtonType.info("Sets whether the player must have permission to be able to use this kit."),
        EditorButtonType.click("Left-Click to &fToggle")),
    KIT_CHANGE_COST(Material.GOLD_NUGGET, "Usage Cost",
        EditorButtonType.current("$" + Placeholders.KIT_COST_MONEY),
        EditorButtonType.info("Sets how much money player must have to pay to use this kit."),
        EditorButtonType.warn("You must have Vault & Economy installed."),
        EditorButtonType.click("Left-Click to &fChange\nRight-Click to &fDisable")),
    KIT_CHANGE_PRIORITY(Material.REPEATER, "Kit Priority",
        EditorButtonType.current(Placeholders.KIT_PRIORITY),
        EditorButtonType.info("Sets the kit priority. This option is useful to sort your kits in GUIs."),
        EditorButtonType.click("Left-Click to &fChange")),
    KIT_CHANGE_ICON(Material.ITEM_FRAME, "Kit Icon",
        EditorButtonType.current(Placeholders.KIT_ICON),
        EditorButtonType.info("Sets the icon that will represent this kit in GUIs."),
        EditorButtonType.click("Drag & Drop to &fReplace")),
    KIT_CHANGE_COMMANDS(Material.COMMAND_BLOCK, "Kit Commands",
        EditorButtonType.current(Placeholders.KIT_COMMANDS),
        EditorButtonType.info("A list of commands, that will be executed when kit is given to a player."),
        EditorButtonType.click("Left-Click to &fAdd Command\nRight-Click to &fClear")),
    KIT_CHANGE_INVENTORY(Material.CHEST_MINECART, "Kit Items",
        EditorButtonType.info("Items that will be added to player's inventory."),
        EditorButtonType.note("Simply add/take items and press ESC to save."),
        EditorButtonType.click("Left-Click to &fNavigate")),
    KIT_CHANGE_ARMOR(Material.ARMOR_STAND, "Kit Armor",
        EditorButtonType.info("Armors that will be added to player's armor slots."),
        EditorButtonType.note("Simply add/take items and press ESC to save."),
        EditorButtonType.warn("Please, follow the order:\nBoots → Legs → Chest → Helmet → OffHand"),
        EditorButtonType.click("Left-Click to &fNavigate")),
    ;

    private final Material material;
    private       String   name;
    private       List<String> lore;

    KitsEditorType() {
        this(Material.AIR, "", "");
    }

    KitsEditorType(@NotNull Material material, @NotNull String name, @NotNull String... lores) {
        this.material = material;
        this.setName(name);
        this.setLore(EditorButtonType.fineLore(lores));
    }

    @NotNull
    @Override
    public Material getMaterial() {
        return material;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = Colorizer.apply(name);
    }

    @NotNull
    public List<String> getLore() {
        return lore;
    }

    public void setLore(@NotNull List<String> lore) {
        this.lore = Colorizer.apply(new ArrayList<>(lore));
    }
}
