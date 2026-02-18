package su.nightexpress.sunlight.module.items;

import org.bukkit.permissions.Permission;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.config.Perms;

public class ItemsPerms {

    public static final PermissionTree MODULE  = Perms.detached("items");
    public static final PermissionTree COMMAND = MODULE.branch("command");

    public static final Permission COMMAND_ITEM_ROOT              = COMMAND.permission("item.root");
    public static final Permission COMMAND_ITEM_AMOUNT            = COMMAND.permission("item.amount");
    public static final Permission COMMAND_ITEM_DAMAGE            = COMMAND.permission("item.damage");
    public static final Permission COMMAND_ITEM_UNBREAKABLE       = COMMAND.permission("item.unbreakable");
    public static final Permission COMMAND_ITEM_ENCHANT           = COMMAND.permission("item.enchant");
    public static final Permission COMMAND_ITEM_ENCHANT_UNLIMITED = COMMAND.permission("item.enchant.unlimited");
    public static final Permission COMMAND_ITEM_DISENCHANT        = COMMAND.permission("item.disenchant");
    public static final Permission COMMAND_ITEM_GET               = COMMAND.permission("item.get");
    public static final Permission COMMAND_ITEM_GIVE              = COMMAND.permission("item.give");
    public static final Permission COMMAND_ITEM_MODEL             = COMMAND.permission("item.model");
    public static final Permission COMMAND_ITEM_NAME              = COMMAND.permission("item.name");
    public static final Permission COMMAND_ITEM_POTION            = COMMAND.permission("item.potion");
    public static final Permission COMMAND_ITEM_REPAIR            = COMMAND.permission("item.repair");
    public static final Permission COMMAND_ITEM_SPAWN             = COMMAND.permission("item.spawn");
}
