package su.nightexpress.sunlight.module.worlds.editor;

import net.md_5.bungee.api.ChatColor;
import su.nexmedia.engine.api.editor.EditorLocale;
import su.nightexpress.sunlight.module.worlds.util.Placeholders;

import static su.nexmedia.engine.utils.Colors.*;

public class EditorLocales extends su.nexmedia.engine.api.editor.EditorLocales {

    private static final String PREFIX = "Worlds.Editor.";

    public static final String TITLE_WORLDS_EDITOR = "Worlds Editor";
    public static final String TITLE_WORLD_RULES = "World Rules: " + Placeholders.GENERIC_WORLD;

    public static final EditorLocale WORLD_OBJECT = builder(PREFIX + "World.Object")
        .name("World: " + ChatColor.WHITE + Placeholders.WORLD_ID)
        .currentHeader()
        .current("Created", Placeholders.WORLD_IS_CREATED)
        .current("Loaded", Placeholders.WORLD_IS_LOADED)
        .current("Auto-Load", Placeholders.WORLD_AUTO_LOAD).breakLine()
        .actionsHeader().action("Left-Click", "Settings")
        .action("Shift-Right", "Delete " + RED + "(If not created)")
        .build();

    public static final EditorLocale WORLD_AUTO_LOAD = builder(PREFIX + "World.AutoLoad")
        .name("Auto-Load")
        .current("Enabled", Placeholders.WORLD_AUTO_LOAD).breakLine()
        .text("Sets whether this world will be", "auto-loaded on server startup.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale WORLD_LOAD = builder(PREFIX + "World.Load")
        .name("Load World")
        .build();

    public static final EditorLocale WORLD_UNLOAD = builder(PREFIX + "World.Unload")
        .name("Unload World")
        .build();

    public static final EditorLocale WORLD_DELETE = builder(PREFIX + "World.Delete")
        .name("Delete World")
        .text("Delete world's config and/or world files." + RED + " (No Undo)").breakLine()
        .actionsHeader().action("[Q/Drop] Key", "Delete Files")
        .action("Shift-Right", "Delete Config & Files")
        .build();

    public static final EditorLocale WORLD_GENERATOR = builder(PREFIX + "World.Generator")
        .name("[Creator] Chunk Generator")
        .current("Current", Placeholders.WORLD_GENERATOR).breakLine()
        .text("Sets chunk generator for this world.", "It will suggest you all available generators", "including ones from other plugins.").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale WORLD_ENVIRONMENT = builder(PREFIX + "World.Environment")
        .name("[Creator] Environment")
        .current("Current", Placeholders.WORLD_ENVIRONMENT).breakLine()
        .text("Sets world's environment.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale WORLD_STRUCTURES = builder(PREFIX + "World.Structures")
        .name("[Creator] Structure Generation")
        .current("Enabled", Placeholders.WORLD_STRUCTURES).breakLine()
        .text("Sets whether structures should be", "generated in this world.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();


    public static final EditorLocale WORLD_GAME_RULES = builder(PREFIX + "World.GameRules")
        .name("Game Rules")
        .text("Manage world's Game Rules here!").breakLine()
        .actionsHeader().action("Left-Click", "Navigate")
        .build();

    public static final EditorLocale WORLD_AUTO_WIPE = builder(PREFIX + "World.AutoWipe")
        .name("Auto Wipe")
        .text("Sets whether or not this world", "should auto reset with", "certain interval.").breakLine()
        .currentHeader().current("Enabled", Placeholders.WORLD_AUTO_WIPE_ENABLED)
        .current("Interval", Placeholders.WORLD_AUTO_WIPE_INTERVAL)
        .current("Last Wipe", Placeholders.WORLD_AUTO_WIPE_LAST_WIPE)
        .current("Next Wipe", Placeholders.WORLD_AUTO_WIPE_NEXT_WIPE).breakLine()
        .actionsHeader().action("Left-Click", "Toggle").action("Right-Click", "Change Interval")
        .action("[Q/Drop] Key", "Set Last Time")
        .build();

    public static final EditorLocale WORLD_AUTO_SAVE = builder(PREFIX + "World.AutoSave")
        .name("Auto Save")
        .current("Enabled", Placeholders.WORLD_AUTO_SAVE).breakLine()
        .text("Sets whether or not the world", "will automatically save.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale WORLD_PVP_ALLOWED = builder(PREFIX + "World.PvPAllowed")
        .name("PvP Allowed")
        .current("Enabled", Placeholders.WORLD_PVP_ALLOWED).breakLine()
        .text("Sets the PVP setting for this world.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale WORLD_DIFFICULTY = builder(PREFIX + "World.Difficulty")
        .name("Difficulty")
        .current("Current", Placeholders.WORLD_DIFFICULTY).breakLine()
        .text("Sets world's difficulty.").breakLine()
        .actionsHeader().action("Left-Click", "Toggle")
        .build();

    public static final EditorLocale WORLD_SPAWN_LIMITS = builder(PREFIX + "World.SpawnLimits")
        .name("Spawn Limits")
        .text("Sets the limit for number of Spawn Category", "entities that can spawn in a chunk", "in this world.").breakLine()
        .currentHeader().text(Placeholders.WORLD_SPAWN_LIMITS).breakLine()
        .noteHeader().notes("Use " + ORANGE + "-1" + GRAY + " for no limit.").breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();

    public static final EditorLocale WORLD_SPAWN_TICKS = builder(PREFIX + "World.SpawnTicks")
        .name("Ticks Per Spawn")
        .text("Sets the world's ticks per", "Spawn Category mob spawns value.").breakLine()
        .currentHeader().text(Placeholders.WORLD_SPAWN_TICKS).breakLine()
        .actionsHeader().action("Left-Click", "Change")
        .build();


    public static final EditorLocale WORLD_RULE_OBJECT = builder(PREFIX + "World.Rule.Object")
        .name("Rule: &f" + Placeholders.GENERIC_NAME)
        .current("Value", Placeholders.GENERIC_VALUE).breakLine()
        .actionsHeader().action("Left-Click", "Toggle / Change")
        .build();
}
