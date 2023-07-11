package su.nightexpress.sunlight.module.kits.config;

import org.bukkit.Sound;
import su.nexmedia.engine.api.lang.LangColors;
import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

public class KitsLang implements LangColors {

    public static final LangKey COMMAND_KITS_DESC  = LangKey.of("Kits.Command.Kits.Desc", "Kits Management.");
    public static final LangKey COMMAND_KITS_USAGE = LangKey.of("Kits.Command.Kits.Usage", "[help]");

    public static final LangKey COMMAND_KITS_EDITOR_DESC = LangKey.of("Kits.Command.Kits.Editor.Desc", "Open Kit Editor.");

    public static final LangKey COMMAND_KITS_PREVIEW_DESC  = LangKey.of("Kits.Command.Kits.Preview.Desc", "Preview content of specified kit.");
    public static final LangKey COMMAND_KITS_PREVIEW_USAGE = LangKey.of("Kits.Command.Kits.Preview.Usage", "<kit> [player]");

    public static final LangKey COMMAND_KITS_GIVE_DESC   = LangKey.of("Kits.Command.Kits.Give.Desc", "Give kit to a player.");
    public static final LangKey COMMAND_KITS_GIVE_USAGE  = LangKey.of("Kits.Command.Kits.Give.Usage", "<kit> <player>");
    public static final LangKey COMMAND_KITS_GIVE_DONE   = LangKey.of("Kits.Command.Kits.Give.Done", "Given &a" + Placeholders.KIT_NAME + " &7kit to &a" + Placeholders.PLAYER_DISPLAY_NAME + "&7!");
    public static final LangKey COMMAND_KITS_GIVE_NOTIFY = LangKey.of("Kits.Command.Kits.Give.Notify", "<! prefix:\"false\" !>#5d6d7e&oYou were given #aeb6bf&o" + Placeholders.KIT_NAME + " #5d6d7e&okit from #aeb6bf&o" + Placeholders.PLAYER_DISPLAY_NAME + "#5d6d7e&o.");

    public static final LangKey COMMAND_KITS_GET_DESC  = LangKey.of("Kits.Command.Kits.Get.Desc", "Get specified kit.");
    public static final LangKey COMMAND_KITS_GET_USAGE = LangKey.of("Kits.Command.Kits.Get.Usage", "<kit>");

    public static final LangKey COMMAND_KITS_LIST_DESC  = LangKey.of("Kits.Command.Kits.List.Desc", "Open Kits GUI.");
    public static final LangKey COMMAND_KITS_LIST_USAGE = LangKey.of("Kits.Command.Kits.List.Usage", "[player]");

    public static final LangKey KIT_GET = LangKey.of("Kits.Kit.Get", "<! type:\"titles:20:60:20\" sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>&a&lKit Received!\n&7You got the &a" + Placeholders.KIT_NAME + "&7 kit!");

    public static final LangKey KIT_ERROR_NO_KITS            = LangKey.of("Kits.Kit.Error.NoKits", "&cNo kits are available.");
    public static final LangKey KIT_ERROR_INVALID_KIT        = LangKey.of("Kits.Kit.Error.InvalidKit", "&cNo such kit!");
    public static final LangKey KIT_ERROR_NO_PERMISSION      = LangKey.of("Kits.Kit.Error.NoPermission", "&cYou don't have permissions to use this kit!");
    public static final LangKey KIT_ERROR_NOT_ENOUGH_FUNDS   = LangKey.of("Kits.Kit.Error.NotEnoughFunds", "&cYou can't afford this kit! You need &c$" + Placeholders.KIT_COST_MONEY + "&c.");
    public static final LangKey KIT_ERROR_COOLDOWN_EXPIRABLE = LangKey.of("Kits.Kit.Error.Cooldown.Expirable", "&cYou have to wait &e" + Placeholders.GENERIC_COOLDOWN + " &cbefore you can use this kit again.");
    public static final LangKey KIT_ERROR_COOLDOWN_ONE_TIMED = LangKey.of("Kits.Kit.Error.Cooldown.OneTimed", "&cYou already have used this kit. You can not use it more.");

    public static final LangKey EDITOR_ENTER_KIT_ID         = LangKey.of("Kits.Editor.Enter.Id", "&7Enter &aunique &7kit &aidentifier...");
    public static final LangKey EDITOR_ENTER_COMMAND        = LangKey.of("Kits.Editor.Enter.Command", "&7Enter &acommand&7...");
    public static final LangKey EDITOR_ENTER_COOLDOWN       = LangKey.of("Kits.Editor.Enter.Cooldown", "&7Enter &acooldown&7... &a(in seconds)");
    public static final LangKey EDITOR_ENTER_COST           = LangKey.of("Kits.Editor.Enter.Cost", "&7Enter &amoney cost&7...");
    public static final LangKey EDITOR_ENTER_NAME           = LangKey.of("Kits.Editor.Enter.Name", "&7Enter display &aname&7...");
    public static final LangKey EDITOR_ENTER_DESCRIPTION    = LangKey.of("Kits.Editor.Enter.Description", "&7Enter &adescription&7...");
    public static final LangKey EDITOR_ENTER_PRIORITY       = LangKey.of("Kits.Editor.Enter.Priority", "&7Enter &apriority&7...");
    public static final LangKey EDITOR_ERROR_ALREADY_EXISTS = LangKey.of("Kits.Editor.Error.AlreadyExists", "&cKit already exists!");
}
