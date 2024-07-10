package su.nightexpress.sunlight.module.warps.config;

import org.bukkit.Sound;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.module.warps.type.SortType;
import su.nightexpress.sunlight.module.warps.type.WarpType;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.warps.util.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;

public class WarpsLang extends Lang {

    public static final LangEnum<WarpType> WARP_TYPE = LangEnum.of("Warps.WarpType", WarpType.class);
    public static final LangEnum<SortType> SORT_TYPE = LangEnum.of("Warps.SortType", SortType.class);

    public static final LangString COMMAND_DELETE_WARP_DESC         = LangString.of("Warps.Command.Warps.Delete.Desc", "Delete a warp.");
    public static final LangString COMMAND_CREATE_WARP_DESC         = LangString.of("Warps.Command.Warps.Create.Desc", "Create a new warp.");
    public static final LangString COMMAND_WARP_DESC                = LangString.of("Warps.Command.Warps.Teleport.Desc", "Teleport to a warp.");
    public static final LangString COMMAND_DIRECT_WARP_DESC         = LangString.of("Warps.Command.Warps.Teleport.Desc", "Teleport to the " + WARP_NAME + ".");
    public static final LangString COMMAND_RESET_WARP_COOLDOWN_DESC = LangString.of("Warps.Command.Warps.ResetCooldown.Desc", "Reset warp cooldown.");
    public static final LangString COMMAND_SET_WARP_COOLDOWN_DESC   = LangString.of("Warps.Command.Warps.SetCooldown.Desc", "Set warp cooldown.");
    public static final LangString COMMAND_WARP_LIST_DESC           = LangString.of("Warps.Command.Warps.List.Desc", "Open Warps GUI.");

    public static final LangText COMMAND_TELEPORT_OTHERS = LangText.of("Warps.Command.Warps.Teleport.Others",
        LIGHT_GRAY.enclose("Teleported " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + " to " + LIGHT_YELLOW.enclose(WARP_NAME) + " warp.")
    );

    public static final LangText COMMAND_LIST_OTHERS = LangText.of("Warps.Command.Warps.List.Others",
        LIGHT_GRAY.enclose("Opened Warp GUI for " + LIGHT_YELLOW.enclose(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_RESET_COOLDOWN_DONE = LangText.of("Warps.Command.Warps.ResetCooldown.Done",
        LIGHT_GRAY.enclose("Reset " + LIGHT_YELLOW.enclose(WARP_NAME) + " warp cooldown for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_WARPS_RESET_COOLDOWN_NOTIFY = LangText.of("Warps.Command.Warps.ResetCooldown.Notify",
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(WARP_NAME) + " warp cooldown have been reset!")
    );

    public static final LangText COMMAND_WARPS_SET_COOLDOWN_DONE = LangText.of("Warps.Command.Warps.SetCooldown.Done",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(WARP_NAME) + " warp cooldown on " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_WARPS_SET_COOLDOWN_NOTIFY = LangText.of("Warps.Command.Warps.SetCooldown.Notify",
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(WARP_NAME) + " warp cooldown have been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + "!")
    );

    public static final LangText WARP_DELETE_DONE   = LangText.of("Warps.Delete.Done",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_GLASS_BREAK),
        LIGHT_YELLOW.enclose(BOLD.enclose("Warp Removed!")),
        LIGHT_GRAY.enclose("You removed " + LIGHT_YELLOW.enclose(WARP_NAME) + " warp.")
    );

    public static final LangText WARP_CREATE_DONE_FRESH = LangText.of("Warps.Warp.Creation.New",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_GREEN.enclose(BOLD.enclose("Warp Created!")),
        LIGHT_GRAY.enclose("Teleport: " + LIGHT_GREEN.enclose("/warp " + WARP_ID) + " | Edit in: " + LIGHT_GREEN.enclose("/warplist"))
    );

    public static final LangText WARP_CREATE_DONE_RELOCATE = LangText.of("Warps.Warp.Creation.Relocate",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_GREEN.enclose(BOLD.enclose("Warp Relocated!")),
        LIGHT_GRAY.enclose("You moved out " + LIGHT_GREEN.enclose(WARP_NAME) + " warp.")
    );

    public static final LangText WARP_CREATE_ERROR_LIMIT = LangText.of("Warps.Warp.Creation.Error.Limit",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Limit Reached!")),
        LIGHT_GRAY.enclose("You can't create more warps!")
    );

    public static final LangText WARP_CREATE_ERROR_WORLD   = LangText.of("Warps.Warp.Creation.Error.World",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Forbidden World!")),
        LIGHT_GRAY.enclose("You can't create warps in this world!")
    );

    public static final LangText WARP_CREATE_ERROR_EXISTS  = LangText.of("Warps.Warp.Creation.Error.Exists",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Already Exists!")),
        LIGHT_GRAY.enclose("Warp with such name already exists.")
    );

    public static final LangText WARP_CREATE_ERROR_UNSAFE  = LangText.of("Warps.Warp.Creation.Error.Unsafe",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Unsafe Location!")),
        LIGHT_GRAY.enclose("Please, choose another place.")
    );

    public static final LangText WARP_TELEPORT_DONE = LangText.of("Warps.Warp.Teleport.Done",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_YELLOW.enclose(BOLD.enclose(WARP_NAME)),
        LIGHT_GRAY.enclose(WARP_DESCRIPTION)
    );

    public static final LangText WARP_TELEPORT_ERROR_NOT_ENOUGH_FUNDS = LangText.of("Warps.Warp.Teleport.Error.NotEnoughFunds",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Not Enough Funds!")),
        LIGHT_GRAY.enclose("You need " + LIGHT_RED.enclose("$" + WARP_VISIT_COST) + " to visit this warp!")
    );

    public static final LangText WARP_TELEPORT_ERROR_NO_PERMISSION = LangText.of("Warps.Warp.Teleport.Error.NoPermission",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("No Permission!")),
        LIGHT_GRAY.enclose("You don't have permission for " + LIGHT_RED.enclose(WARP_NAME) + " warp!")
    );

    public static final LangText WARP_TELEPORT_ERROR_COOLDOWN = LangText.of("Warps.Warp.Teleport.Error.Cooldown",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Cooldown!")),
        LIGHT_GRAY.enclose("You can visit " + LIGHT_RED.enclose(WARP_NAME) + " again in " + LIGHT_RED.enclose(GENERIC_COOLDOWN))
    );

    public static final LangText WARP_TELEPORT_ERROR_DISABLED = LangText.of("Warps.Warp.Teleport.Error.Unreachable",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Unreachable Warp!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(WARP_NAME) + " located in an unavailable world.")
    );

    public static final LangText WARP_TELEPORT_ERROR_TIME = LangText.of("Warps.Warp.Teleport.Error.Time",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.enclose(BOLD.enclose("Wrong Time!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(WARP_NAME) + " is not available for visits now.")
    );

    public static final LangText ERROR_COMMAND_INVALID_WARP_ARGUMENT = LangText.of("Warps.Error.Command.Argument.InvalidWarp",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid warp!"));


    public static final LangString EDITOR_ENTER_COST = LangString.of("Warps.Editor.Enter.CostMoney",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Cost]")));

    public static final LangString EDITOR_ENTER_COMMAND = LangString.of("Warps.Editor.Enter.Command",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Command Name]")));

    public static final LangString EDITOR_ENTER_TIMES = LangString.of("Warps.Editor.Enter.Times",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Times] " + LIGHT_GRAY.enclose(">") + " 10:00 14:00")));

    public static final LangString EDITOR_ENTER_COOLDOWN = LangString.of("Warps.Editor.Enter.Cooldown",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Cooldown]")));

    public static final LangString EDITOR_ENTER_NAME = LangString.of("Warps.Editor.Enter.Name",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Display Name]")));

    public static final LangString EDITOR_ENTER_DESCRIPTION = LangString.of("Warps.Editor.Enter.Description",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Description]")));

}
