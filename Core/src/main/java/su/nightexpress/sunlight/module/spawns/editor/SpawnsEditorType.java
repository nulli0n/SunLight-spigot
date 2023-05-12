package su.nightexpress.sunlight.module.spawns.editor;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.editor.EditorButtonType;
import su.nexmedia.engine.utils.Colorizer;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;

import java.util.ArrayList;
import java.util.List;

public enum SpawnsEditorType implements EditorButtonType {

    SPAWN_OBJECT(Material.COMPASS, Placeholders.SPAWN_NAME + " &7(&f" + Placeholders.SPAWN_ID + "&7)",
        EditorButtonType.info("Priority: &f" + Placeholders.SPAWN_PRIORITY + "\nPermission: &f" + Placeholders.SPAWN_PERMISSION_REQUIRED),
        EditorButtonType.click("Left-Click to &fEdit\nShift-Right to &fDelete &7(No Undo)")),
    SPAWN_CHANGE_NAME(Material.NAME_TAG, "Spawn Name",
        EditorButtonType.current(Placeholders.SPAWN_NAME),
        EditorButtonType.info("Sets the spawn display name. This name is used in messages and GUIs."),
        EditorButtonType.click("Left-Click to &fChange")),
    SPAWN_CHANGE_LOCATION(Material.COMPASS, "Spawn Location",
        EditorButtonType.current(Placeholders.SPAWN_LOCATION_X + ", " + Placeholders.SPAWN_LOCATION_Y + ", " + Placeholders.SPAWN_LOCATION_Z + " in " + Placeholders.SPAWN_LOCATION_WORLD),
        EditorButtonType.info("Location, where players will be teleported when using this spawn."),
        EditorButtonType.click("Left-Click to &fSet to Current")),
    SPAWN_CHANGE_PERMISSION(Material.REDSTONE_TORCH, "Permission Requirement",
        EditorButtonType.current("Enabled: &f" + Placeholders.SPAWN_PERMISSION_REQUIRED + "\nPermission Node: &f" + Placeholders.SPAWN_PERMISSION_NODE),
        EditorButtonType.info("When enabled, players must have permission to be able to use this spawn."),
        EditorButtonType.click("Left-Click to &fToggle")),
    SPAWN_CHANGE_DEFAULT(Material.GRAY_DYE, "Default Spawn",
        EditorButtonType.current("Is Default: &f" + Placeholders.SPAWN_IS_DEFAULT),
        EditorButtonType.info("Default spawn will be used when there is no other spawns specified/available."),
        EditorButtonType.click("Left-Click to &fToggle")),
    SPAWN_CHANGE_PRIORITY(Material.COMPARATOR, "Spawn Priority",
        EditorButtonType.current(Placeholders.SPAWN_PRIORITY),
        EditorButtonType.info("When there is more than one spawn available for a player to be teleported on login/death, the one with highest priority will be used."),
        EditorButtonType.click("Left-Click to &fChange")),
    SPAWN_CHANGE_LOGIN_TELEPORT(Material.IRON_DOOR, "Teleport on Login",
        EditorButtonType.current("Global: &f" + Placeholders.SPAWN_LOGIN_TELEPORT_ENABLED + "\nFor New Players: &f" + Placeholders.SPAWN_LOGIN_TELEPORT_NEWBIES + "\nAffected Groups: &f" + Placeholders.SPAWN_LOGIN_TELEPORT_GROUPS),
        EditorButtonType.info("When 'Global' enabled, all players from the specified groups will be teleported to this spawn on login.\nWhen 'For New Players' enabled, only players joined the first time will be teleported to this spawn on login."),
        EditorButtonType.click("Left-Click to &fToggle Global\nRight-Click to &fToggle New Players\nShift-Left to &fAdd Group\nShift-Right to &fClear Groups")),
    SPAWN_CHANGE_RESPAWN_TELEPORT(Material.GUNPOWDER, "Teleport on Respawn",
        EditorButtonType.current("Enabled: &f" + Placeholders.SPAWN_RESPAWN_TELEPORT_ENABLED + "\nAffected Groups: &f" + Placeholders.SPAWN_RESPAWN_TELEPORT_GROUPS),
        EditorButtonType.info("When enabled, all players from the specified groups will be teleported to this spawn on respawn after death."),
        EditorButtonType.click("Left-Click to &fToggle\nShift-Left to &fAdd Group\nShift-Right to &fClear Groups")),
    SPAWN_CHANGE_LOGIN_TELEPORT_GROUPS,
    SPAWN_CHANGE_RESPAWN_TELEPORT_GROUPS,
    ;

    private final Material material;
    private       String   name;
    private       List<String> lore;

    SpawnsEditorType() {
        this(Material.AIR, "", "");
    }

    SpawnsEditorType(@NotNull Material material, @NotNull String name, @NotNull String... lores) {
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
