package su.nightexpress.sunlight.module.kits.editor;

import su.nexmedia.engine.api.editor.EditorLocale;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import static su.nexmedia.engine.utils.Colors.*;

public class EditorLocales extends su.nexmedia.engine.api.editor.EditorLocales {

    private static final String PREFIX_OLD = "Editor.KitsEditorType.";

    public static final EditorLocale KIT_OBJECT = builder(PREFIX_OLD + "KIT_OBJECT")
        .name(Placeholders.KIT_NAME + GRAY + " (&f" + Placeholders.KIT_ID + GRAY + ")")
        .current("Priority", Placeholders.KIT_PRIORITY)
        .current("Permission", Placeholders.KIT_PERMISSION_REQUIRED).breakLine()
        .actionsHeader()
        .action("Left-Click", "Edit")
        .action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale KIT_CREATE = builder(PREFIX_OLD).name("Create Kit").build();

    public static final EditorLocale KIT_NAME = builder(PREFIX_OLD + "KIT_CHANGE_NAME")
        .name("Display Name")
        .text("Sets the kit display name.", "It's used in GUIs, messages, etc.").breakLine()
        .currentHeader().current("Display Name", Placeholders.KIT_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale KIT_DESCRIPTION = builder(PREFIX_OLD + "KIT_CHANGE_DESCRIPTION")
        .name("Description")
        .text("Sets the kit description.", "Provide a brief showcase for players.").breakLine()
        .currentHeader().text(Placeholders.KIT_DESCRIPTION).breakLine()
        .actionsHeader().action("Left-Click", "Add Line").action("Right-Click", "Clear")
        .build();

    public static final EditorLocale KIT_COOLDOWN = builder(PREFIX_OLD + "KIT_CHANGE_COOLDOWN")
        .name("Cooldown")
        .text("Sets usage cooldown (in seconds).", "Players will have to wait specified time", "before they can use it again.").breakLine()
        .currentHeader().current("Cooldown", Placeholders.KIT_COOLDOWN).breakLine()
        .noteHeader()
        .notes("Set " + ORANGE + "-1" + GRAY + " for one-time usage.")
        .notes("Set " + ORANGE + "0" + GRAY + " to disable.").breakLine()
        .actionsHeader().action("Left-Click", "Change").action("Right-Click", "One-Timed")
        .build();

    public static final EditorLocale KIT_PERMISSION = builder(PREFIX_OLD + "KIT_CHANGE_PERMISSION")
        .name("Permission Requirement")
        .text("Sets whether or not player must", "have permission in order to", "use this kit.").breakLine()
        .currentHeader()
        .current("Required", Placeholders.KIT_PERMISSION_REQUIRED)
        .current("Node", Placeholders.KIT_PERMISSION_NODE).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale KIT_COST = builder(PREFIX_OLD + "KIT_CHANGE_COST")
        .name("Usage Cost")
        .text("Sets how much player have to pay", "in order to use this kit.").breakLine()
        .currentHeader().current("Amount", Placeholders.KIT_COST_MONEY).breakLine()
        .warningHeader().warning("You must have Vault & Economy installed.").breakLine()
        .actionsHeader().action("Left-Click", "Change").action("Right-Click", "Disable")
        .build();

    public static final EditorLocale KIT_PRIORITY = builder(PREFIX_OLD + "KIT_CHANGE_PRIORITY")
        .name("Priority")
        .text("Sets the kit priority.", "This option is useful to define kit's order.").breakLine()
        .currentHeader().current("Priority", Placeholders.KIT_PRIORITY).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale KIT_ICON = builder(PREFIX_OLD + "KIT_CHANGE_ICON")
        .name("Icon")
        .text("Sets the kit showcase icon.", "It's used in GUIs to display kits.").breakLine()
        .actionsHeader().action("Drag & Drop", "Replace").build();

    public static final EditorLocale KIT_COMMANDS = builder(PREFIX_OLD + "KIT_CHANGE_COMMANDS")
        .name("Commands")
        .text("List of commands to be executed", "player uses this kit.").breakLine()
        .currentHeader().text(Placeholders.KIT_COMMANDS).breakLine()
        .actionsHeader().action("Left-Click", "Add Command").action("Right-Click", "Clear")
        .build();

    public static final EditorLocale KIT_INVENTORY = builder(PREFIX_OLD + "KIT_CHANGE_INVENTORY")
        .name("Inventory Content")
        .text("Items to be added to player's inventory.").breakLine()
        .noteHeader().notes("Check plugin wiki for slots info.").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale KIT_ARMOR = builder(PREFIX_OLD + "KIT_CHANGE_ARMOR")
        .name("Armor Content")
        .text("Items to be equipped to player's armor slots.").breakLine()
        .noteHeader().notes("Boots → Legs → Chest → Helmet → OffHand").breakLine()
        .actionsHeader().action("Left-Click", "Navigate").build();
}
