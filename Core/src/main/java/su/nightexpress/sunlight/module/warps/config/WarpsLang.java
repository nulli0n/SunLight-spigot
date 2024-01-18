package su.nightexpress.sunlight.module.warps.config;

import org.bukkit.Sound;
import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.module.warps.util.Placeholders;

import static su.nexmedia.engine.utils.Colors.*;

public class WarpsLang {

    public static final LangKey COMMAND_WARPS_DESC  = LangKey.of("Warps.Command.Warps.Desc", "Warp management.");
    public static final LangKey COMMAND_WARPS_USAGE = LangKey.of("Warps.Command.Warps.Usage", "[help]");

    public static final LangKey COMMAND_WARPS_DELETE_DESC  = LangKey.of("Warps.Command.Warps.Delete.Desc", "Delete specified warp.");
    public static final LangKey COMMAND_WARPS_DELETE_USAGE = LangKey.of("Warps.Command.Warps.Delete.Usage", "<warp>");

    public static final LangKey COMMAND_WARPS_CREATE_DESC  = LangKey.of("Warps.Command.Warps.Create.Desc", "Set a new warp.");
    public static final LangKey COMMAND_WARPS_CREATE_USAGE = LangKey.of("Warps.Command.Warps.Create.Usage", "<warp>");

    public static final LangKey COMMAND_WARPS_TELEPORT_DESC   = LangKey.of("Warps.Command.Warps.Teleport.Desc", "Teleport to a specified warp.");
    public static final LangKey COMMAND_WARPS_TELEPORT_USAGE  = LangKey.of("Warps.Command.Warps.Teleport.Usage", "<warp> [player]");
    public static final LangKey COMMAND_WARPS_TELEPORT_OTHERS = LangKey.of("Warps.Command.Warps.Teleport.Others", "Player &a%player% &7teleported on warp &a%warp_name%&7...");

    public static final LangKey COMMAND_WARPS_LIST_DESC = LangKey.of("Warps.Command.Warps.List.Desc", "Open Warps GUI.");
    public static final LangKey COMMAND_WARPS_LIST_USAGE  = LangKey.of("Warps.Command.Warps.List.Usage", "[player]");
    public static final LangKey COMMAND_WARPS_LIST_OTHERS = LangKey.of("Warps.Command.Warps.List.Others", "Opened Warps GUI for " + GREEN + Placeholders.PLAYER_DISPLAY_NAME + GRAY + ".");

    public static final LangKey COMMAND_WARPS_RESET_COOLDOWN_DESC   = LangKey.of("Warps.Command.Warps.ResetCooldown.Desc", "Reset [player's] warp cooldown.");
    public static final LangKey COMMAND_WARPS_RESET_COOLDOWN_USAGE  = LangKey.of("Warps.Command.Warps.ResetCooldown.Usage", "<warp> [player] [-s]");
    public static final LangKey COMMAND_WARPS_RESET_COOLDOWN_DONE   = LangKey.of("Warps.Command.Warps.ResetCooldown.Done", LIGHT_YELLOW + "Reset " + ORANGE + Placeholders.WARP_NAME + LIGHT_YELLOW + " warp cooldown for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_WARPS_RESET_COOLDOWN_NOTIFY = LangKey.of("Warps.Command.Warps.ResetCooldown.Notify", LIGHT_YELLOW + "Your " + ORANGE + Placeholders.WARP_NAME + LIGHT_YELLOW + " warp cooldown have been reset!");

    public static final LangKey COMMAND_WARPS_SET_COOLDOWN_DESC   = LangKey.of("Warps.Command.Warps.SetCooldown.Desc", "Set [player's] warp cooldown.");
    public static final LangKey COMMAND_WARPS_SET_COOLDOWN_USAGE  = LangKey.of("Warps.Command.Warps.SetCooldown.Usage", "<warp> <amount> [player] [-s]");
    public static final LangKey COMMAND_WARPS_SET_COOLDOWN_DONE   = LangKey.of("Warps.Command.Warps.SetCooldown.Done", LIGHT_YELLOW + "Set " + ORANGE + Placeholders.WARP_NAME + LIGHT_YELLOW + " warp cooldown with " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_WARPS_SET_COOLDOWN_NOTIFY = LangKey.of("Warps.Command.Warps.SetCooldown.Notify", LIGHT_YELLOW + "Your " + ORANGE + Placeholders.WARP_NAME + LIGHT_YELLOW + " warp cooldown have been set to " + ORANGE + Placeholders.GENERIC_AMOUNT + LIGHT_YELLOW + "!");

    public static final LangKey WARP_ERROR_INVALID = LangKey.of("Warps.Warp.Error.Invalid", RED + "Invalid warp!");

    public static final LangKey WARP_DELETE_DONE   = LangKey.of("Warps.Delete.Done",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.BLOCK_GLASS_BREAK.name() + "\" !>" +
        "\n" + RED + "&lWarp Deleted" +
        "\n" + GRAY + "Warp " + RED + Placeholders.WARP_NAME + GRAY + " has been deleted.");

    public static final LangKey WARP_CREATE_DONE_FRESH    = LangKey.of("Warps.Warp.Creation.New",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.BLOCK_ANVIL_PLACE.name() + "\" !>" +
            "\n" + GREEN + "&lWarp Created!" +
            "\n" + GRAY + "Teleport: " + GREEN + "/warp " + Placeholders.WARP_ID + GRAY + " | Edit in: " + GREEN + " /warplist");

    public static final LangKey WARP_CREATE_DONE_RELOCATE = LangKey.of("Warps.Warp.Creation.Relocate",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.BLOCK_ANVIL_PLACE.name() + "\" !>" +
            "\n" + GREEN + "&lWarp Relocated!" +
            "\n" + GRAY + "Warp " + GREEN + Placeholders.WARP_NAME + GRAY + " has been moved to here.");

    public static final LangKey WARP_CREATE_ERROR_LIMIT   = LangKey.of("Warps.Warp.Creation.Error.Limit",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED + "&lLimit Reached!" +
            "\n" + GRAY + "You have reached your warps limit.");

    public static final LangKey WARP_CREATE_ERROR_WORLD   = LangKey.of("Warps.Warp.Creation.Error.World",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED + "&lForbidden World!" +
            "\n" + RED + "You can't create warps in this world.");

    public static final LangKey WARP_CREATE_ERROR_EXISTS  = LangKey.of("Warps.Warp.Creation.Error.Exists",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED+ "&lAlready Exists!" +
            "\n" + GRAY + "Warp with such name already exists!");

    public static final LangKey WARP_CREATE_ERROR_UNSAFE  = LangKey.of("Warps.Warp.Creation.Error.Unsafe",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED + "&lUnsafe Location!" +
            "\n" + GRAY + "You can't create warps here!");

    public static final LangKey WARP_TELEPORT_DONE = LangKey.of("Warps.Warp.Teleport.Done",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_ENDERMAN_TELEPORT.name() + "\" !>" +
            "\n" + YELLOW + "&l" + Placeholders.WARP_NAME +
            "\n" + GRAY + Placeholders.WARP_DESCRIPTION);

    public static final LangKey WARP_TELEPORT_ERROR_NOT_ENOUGH_FUNDS = LangKey.of("Warps.Warp.Teleport.Error.NotEnoughFunds",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED +"&lNot Enough Money!" +
            "\n" + GRAY + "You need " + RED + "$" + Placeholders.WARP_VISIT_COST + GRAY + " to visit this warp!");

    public static final LangKey WARP_TELEPORT_ERROR_NO_PERMISSION = LangKey.of("Warps.Warp.Teleport.Error.NoPermission",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED + "&lNo Permission!" +
            "\n" + GRAY + "You don't have permission for " + Placeholders.WARP_NAME + " warp!");

    public static final LangKey WARP_TELEPORT_ERROR_COOLDOWN = LangKey.of("Warps.Warp.Teleport.Error.Cooldown",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED + "&lCooldown!" +
            "\n" + GRAY + "You can visit " + RED + Placeholders.WARP_NAME + GRAY + " again in " + RED + Placeholders.GENERIC_COOLDOWN);

    public static final LangKey WARP_TELEPORT_ERROR_TIME = LangKey.of("Warps.Warp.Teleport.Error.Time",
        "<! type:\"titles:20:50:30\" sound:\"" + Sound.ENTITY_VILLAGER_NO.name() + "\" !>" +
            "\n" + RED + "&lWrong Time!" +
            "\n" + ORANGE + Placeholders.WARP_NAME + GRAY + " is not available for visits now.");

    public static final LangKey EDITOR_ENTER_COST        = LangKey.of("Warps.Editor.Enter.CostMoney", GRAY + "Enter " + GREEN + "[Cost]");
    public static final LangKey EDITOR_ENTER_COMMAND     = LangKey.of("Warps.Editor.Enter.Command", GRAY + "Enter " + GREEN + "[Command Name]");
    public static final LangKey EDITOR_ENTER_TIMES       = LangKey.of("Warps.Editor.Enter.Times", GRAY+ "Enter " + GREEN + "[Times] " + ORANGE + " (10:00 14:00)");
    public static final LangKey EDITOR_ENTER_COOLDOWN    = LangKey.of("Warps.Editor.Enter.Cooldown", GRAY + "Enter " + GREEN + "[Cooldown]");
    public static final LangKey EDITOR_ENTER_NAME        = LangKey.of("Warps.Editor.Enter.Name", GRAY + "Enter " + GREEN + "[Display Name]");
    public static final LangKey EDITOR_ENTER_DESCRIPTION = LangKey.of("Warps.Editor.Enter.Description", GRAY+ "Enter " + GREEN + "[Description]");

}
