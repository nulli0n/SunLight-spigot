package su.nightexpress.sunlight.module.spawns.editor;

import su.nexmedia.engine.api.editor.EditorLocale;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

public class EditorLocales extends su.nexmedia.engine.api.editor.EditorLocales {

    private static final String PREFIX_OLD = "Editor.SpawnsEditorType.";

    public static final EditorLocale SPAWN_OBJECT = builder(PREFIX_OLD + "SPAWN_OBJECT")
        .name(Placeholders.SPAWN_NAME + GRAY + " (&f" + Placeholders.SPAWN_ID + GRAY + ")")
        .current("Priority", Placeholders.SPAWN_PRIORITY)
        .current("Permission", Placeholders.SPAWN_PERMISSION_REQUIRED).breakLine()
        .actionsHeader().action("Left-Click", "Edit").action("Shift-Right", "Delete " + RED + "(No Undo)")
        .build();

    public static final EditorLocale SPAWN_NAME = builder(PREFIX_OLD + "SPAWN_CHANGE_NAME")
        .name("Display Name")
        .text("Sets the spawn display name.", "It's used in messages and GUIs.").breakLine()
        .currentHeader().current("Display Name", Placeholders.SPAWN_NAME).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale SPAWN_LOCATION = builder(PREFIX_OLD + "SPAWN_CHANGE_LOCATION")
        .name("Location")
        .text("This is where players will be teleported.").breakLine()
        .currentHeader().current("Location", Placeholders.SPAWN_LOCATION_X + ", " + Placeholders.SPAWN_LOCATION_Y + ", " + Placeholders.SPAWN_LOCATION_Z + " in " + Placeholders.SPAWN_LOCATION_WORLD).breakLine()
        .actionsHeader().action("Left-Click", "Set to Your Position").action("Right-Click", "Sync with World Spawn")
        .build();

    public static final EditorLocale SPAWN_PERMISSION = builder(PREFIX_OLD + "SPAWN_CHANGE_PERMISSION")
        .name("Permission Requirement")
        .text("Sets whether or not players", "must have permission in order to", "use this spawn.").breakLine()
        .currentHeader().current("Enabled", Placeholders.SPAWN_PERMISSION_REQUIRED)
        .current("Permission Node", Placeholders.SPAWN_PERMISSION_NODE).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale SPAWN_DEFAULT = builder(PREFIX_OLD + "SPAWN_CHANGE_DEFAULT")
        .name("Is Default?")
        .text("Sets whether or not this spawn", "is the default one.")
        .text("Default spawn used when there are no", "other spawns set, specified or available.").breakLine()
        .currentHeader().current("Is Default", Placeholders.SPAWN_IS_DEFAULT).breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale SPAWN_PRIORITY = builder(PREFIX_OLD + "SPAWN_CHANGE_PRIORITY")
        .name("Priority")
        .text("When there are multiple spawns available", "for a player, the one with the greatest priority", "will be used.").breakLine()
        .noteHeader().text("This only have effect for login/death spawns.").breakLine()
        .currentHeader().current("Priority", Placeholders.SPAWN_PRIORITY).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale SPAWN_LOGIN_TELEPORT = builder(PREFIX_OLD + "SPAWN_CHANGE_LOGIN_TELEPORT")
        .name("Teleport on Login")
        .text("When " + YELLOW + "'Global'" + GRAY + " enabled, all players from", "specified groups will be teleported", "to this spawn on login.").breakLine()
        .text("When " + YELLOW + "'For New Players'" + GRAY + " enabled,", "only players joined the first time", "will be teleported on login.").breakLine()
        .currentHeader()
        .current("Global", Placeholders.SPAWN_LOGIN_TELEPORT_ENABLED)
        .current("For New Players", Placeholders.SPAWN_LOGIN_TELEPORT_NEWBIES)
        .current("Affected Groups", Placeholders.SPAWN_LOGIN_TELEPORT_GROUPS).breakLine()
        .actionsHeader()
        .action("Left-Click", "Toggle Global")
        .action("Right-Click", "Toggle New Players")
        .action("Shift-Left", "Add Group")
        .action("Shift-Right", "Clear Groups")
        .build();

    public static final EditorLocale SPAWN_RESPAWN_TELEPORT = builder(PREFIX_OLD + "SPAWN_CHANGE_RESPAWN_TELEPORT")
        .name("Teleport on Respawn")
        .text("When enabled, all players from specified groups", "will be teleported to this spawn", "on respawn after death.").breakLine()
        .noteHeader().text("This will not work for players", "with a home selected for respawn.").breakLine()
        .currentHeader()
        .current("Enabled", Placeholders.SPAWN_RESPAWN_TELEPORT_ENABLED)
        .current("Affected Groups", Placeholders.SPAWN_RESPAWN_TELEPORT_GROUPS).breakLine()
        .actionsHeader()
        .action("Left-Click", "Toggle")
        .action("Shift-Left", "Add Group")
        .action("Shift-Right", "Clear Groups")
        .build();
}
