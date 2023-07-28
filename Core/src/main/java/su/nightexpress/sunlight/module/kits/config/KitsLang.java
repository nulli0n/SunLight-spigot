package su.nightexpress.sunlight.module.kits.config;

import org.bukkit.Sound;
import su.nexmedia.engine.api.lang.LangColors;
import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

public class KitsLang implements LangColors {

    public static final LangKey COMMAND_KITS_DESC  = LangKey.of("Kits.Command.Kits.Desc", "Kits Management.");
    public static final LangKey COMMAND_KITS_USAGE = LangKey.of("Kits.Command.Kits.Usage", "[help]");

    public static final LangKey COMMAND_KITS_EDITOR_DESC = LangKey.of("Kits.Command.Kits.Editor.Desc", "Open Kit Editor.");

    public static final LangKey COMMAND_KITS_PREVIEW_DESC   = LangKey.of("Kits.Command.Kits.Preview.Desc", "Preview content of specified kit.");
    public static final LangKey COMMAND_KITS_PREVIEW_USAGE  = LangKey.of("Kits.Command.Kits.Preview.Usage", "<kit> [player]");
    public static final LangKey COMMAND_KITS_PREVIEW_OTHERS = LangKey.of("Kits.Command.Kits.Preview.Others", LIGHT_YELLOW + "Opened " + ORANGE + Placeholders.KIT_NAME + LIGHT_YELLOW + " kit preview for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_KITS_GIVE_DESC   = LangKey.of("Kits.Command.Kits.Give.Desc", "Give kit to a player.");
    public static final LangKey COMMAND_KITS_GIVE_USAGE  = LangKey.of("Kits.Command.Kits.Give.Usage", "<kit> <player> [-s]");
    public static final LangKey COMMAND_KITS_GIVE_DONE   = LangKey.of("Kits.Command.Kits.Give.Done", LIGHT_YELLOW + "Given " + ORANGE + Placeholders.KIT_NAME + LIGHT_YELLOW + " kit to " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + "!");
    public static final LangKey COMMAND_KITS_GIVE_NOTIFY = LangKey.of("Kits.Command.Kits.Give.Notify", "<! prefix:\"false\" !>" + LIGHT_YELLOW + "&oYou were given " + ORANGE + "&o" + Placeholders.KIT_NAME + " " + LIGHT_YELLOW + "&okit from " + ORANGE + "&o" + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_KITS_GET_DESC  = LangKey.of("Kits.Command.Kits.Get.Desc", "Get specified kit.");
    public static final LangKey COMMAND_KITS_GET_USAGE = LangKey.of("Kits.Command.Kits.Get.Usage", "<kit>");

    public static final LangKey COMMAND_KITS_LIST_DESC   = LangKey.of("Kits.Command.Kits.List.Desc", "Open Kits GUI.");
    public static final LangKey COMMAND_KITS_LIST_USAGE  = LangKey.of("Kits.Command.Kits.List.Usage", "[player]");
    public static final LangKey COMMAND_KITS_LIST_OTHERS = LangKey.of("Kits.Command.Kits.List.Others", LIGHT_YELLOW + "Opened Kits GUI for " + ORANGE + Placeholders.PLAYER_DISPLAY_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_KITS_RESET_COOLDOWN_DESC   = LangKey.of("Kits.Command.Kits.ResetCooldown.Desc", "Reset [player's] kit cooldown.");
    public static final LangKey COMMAND_KITS_RESET_COOLDOWN_USAGE  = LangKey.of("Kits.Command.Kits.ResetCooldown.Usage", "<kit> [player] [-s]");
    public static final LangKey COMMAND_KITS_RESET_COOLDOWN_DONE   = LangKey.of("Kits.Command.Kits.ResetCooldown.Done", LIGHT_YELLOW + "Reset " + ORANGE + Placeholders.KIT_NAME + LIGHT_YELLOW + " kit cooldown for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_KITS_RESET_COOLDOWN_NOTIFY = LangKey.of("Kits.Command.Kits.ResetCooldown.Notify", LIGHT_YELLOW + "Your " + ORANGE + Placeholders.KIT_NAME + LIGHT_YELLOW + " kit cooldown have been reset!");

    public static final LangKey COMMAND_KITS_SET_COOLDOWN_DESC   = LangKey.of("Kits.Command.Kits.SetCooldown.Desc", "Set [player's] kit cooldown.");
    public static final LangKey COMMAND_KITS_SET_COOLDOWN_USAGE  = LangKey.of("Kits.Command.Kits.SetCooldown.Usage", "<kit> <amount> [player] [-s]");
    public static final LangKey COMMAND_KITS_SET_COOLDOWN_DONE   = LangKey.of("Kits.Command.Kits.SetCooldown.Done", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.KIT_NAME + LIGHT_YELLOW + " kit cooldown with " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_KITS_SET_COOLDOWN_NOTIFY = LangKey.of("Kits.Command.Kits.SetCooldown.Notify", LIGHT_YELLOW + "Your " + ORANGE + Placeholders.KIT_NAME + LIGHT_YELLOW + " kit cooldown have been set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + "!");

    public static final LangKey KIT_GET = LangKey.of("Kits.Kit.Get",
        "<! type:\"titles:20:60:20\" sound:\"" + Sound.ITEM_ARMOR_EQUIP_LEATHER.name() + "\" !>" +
            "\n" + GREEN + BOLD + "Kit Received!" +
            "\n" + GRAY + "You got the " + GREEN + Placeholders.KIT_NAME + GRAY + " kit!");

    public static final LangKey KIT_ERROR_NO_KITS            = LangKey.of("Kits.Kit.Error.NoKits", RED + "No kits are available.");
    public static final LangKey KIT_ERROR_INVALID            = LangKey.of("Kits.Kit.Error.InvalidKit", RED + "No such kit.");
    public static final LangKey KIT_ERROR_NO_PERMISSION      = LangKey.of("Kits.Kit.Error.NoPermission", RED + "You don't have permissions to use this kit!");
    public static final LangKey KIT_ERROR_NOT_ENOUGH_FUNDS   = LangKey.of("Kits.Kit.Error.NotEnoughFunds", RED + "You can't afford this kit! You need " + ORANGE + "$" + Placeholders.KIT_COST_MONEY + RED + ".");
    public static final LangKey KIT_ERROR_COOLDOWN_EXPIRABLE = LangKey.of("Kits.Kit.Error.Cooldown.Expirable", RED + "You have to wait " + ORANGE + Placeholders.GENERIC_COOLDOWN + RED + " before you can use this kit again.");
    public static final LangKey KIT_ERROR_COOLDOWN_ONE_TIMED = LangKey.of("Kits.Kit.Error.Cooldown.OneTimed", RED + "You already have used this kit. You can not use it more.");

    public static final LangKey EDITOR_ENTER_KIT_ID         = LangKey.of("Kits.Editor.Enter.Id", GRAY + "Enter " + GREEN + "[Kit Identifier]");
    public static final LangKey EDITOR_ENTER_COMMAND        = LangKey.of("Kits.Editor.Enter.Command", GRAY + "Enter " + GREEN + "[Command]");
    public static final LangKey EDITOR_ENTER_COOLDOWN       = LangKey.of("Kits.Editor.Enter.Cooldown", GRAY + "Enter " + GREEN + "[Seconds Amount]");
    public static final LangKey EDITOR_ENTER_COST           = LangKey.of("Kits.Editor.Enter.Cost", GRAY + "Enter " + GREEN + "[Money Cost]");
    public static final LangKey EDITOR_ENTER_NAME           = LangKey.of("Kits.Editor.Enter.Name", GRAY + "Enter " + GREEN + "[Display Name]");
    public static final LangKey EDITOR_ENTER_DESCRIPTION    = LangKey.of("Kits.Editor.Enter.Description", GRAY + "Enter " + GREEN + "[Description]");
    public static final LangKey EDITOR_ENTER_PRIORITY       = LangKey.of("Kits.Editor.Enter.Priority", GRAY + "Enter " + GREEN + "[Priority]");
    public static final LangKey EDITOR_ERROR_ALREADY_EXISTS = LangKey.of("Kits.Editor.Error.AlreadyExists", RED + "Kit already exists!");
}
