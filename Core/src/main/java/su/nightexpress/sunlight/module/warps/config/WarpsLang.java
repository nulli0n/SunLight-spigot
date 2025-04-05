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
        LIGHT_GRAY.wrap("Teleported " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + " to " + LIGHT_YELLOW.wrap(WARP_NAME) + " warp.")
    );

    public static final LangText COMMAND_LIST_OTHERS = LangText.of("Warps.Command.Warps.List.Others",
        LIGHT_GRAY.wrap("Opened Warp GUI for " + LIGHT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + ".")
    );

    public static final LangText COMMAND_RESET_COOLDOWN_DONE = LangText.of("Warps.Command.Warps.ResetCooldown.Done",
        LIGHT_GRAY.wrap("Reset " + LIGHT_YELLOW.wrap(WARP_NAME) + " warp cooldown for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_WARPS_RESET_COOLDOWN_NOTIFY = LangText.of("Warps.Command.Warps.ResetCooldown.Notify",
        LIGHT_GRAY.wrap("Your " + LIGHT_YELLOW.wrap(WARP_NAME) + " warp cooldown have been reset!")
    );

    public static final LangText COMMAND_WARPS_SET_COOLDOWN_DONE = LangText.of("Warps.Command.Warps.SetCooldown.Done",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(WARP_NAME) + " warp cooldown on " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_WARPS_SET_COOLDOWN_NOTIFY = LangText.of("Warps.Command.Warps.SetCooldown.Notify",
        LIGHT_GRAY.wrap("Your " + LIGHT_YELLOW.wrap(WARP_NAME) + " warp cooldown have been set to " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + "!")
    );

    public static final LangText WARP_DELETE_DONE   = LangText.of("Warps.Delete.Done",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_GLASS_BREAK),
        LIGHT_YELLOW.wrap(BOLD.wrap("Warp Removed!")),
        LIGHT_GRAY.wrap("You removed " + LIGHT_YELLOW.wrap(WARP_NAME) + " warp.")
    );

    public static final LangText WARP_CREATE_DONE_FRESH = LangText.of("Warps.Warp.Creation.New",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_GREEN.wrap(BOLD.wrap("Warp Created!")),
        LIGHT_GRAY.wrap("Teleport: " + LIGHT_GREEN.wrap("/warp " + WARP_ID) + " | Edit in: " + LIGHT_GREEN.wrap("/warplist"))
    );

    public static final LangText WARP_CREATE_DONE_RELOCATE = LangText.of("Warps.Warp.Creation.Relocate",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_ANVIL_PLACE),
        LIGHT_GREEN.wrap(BOLD.wrap("Warp Relocated!")),
        LIGHT_GRAY.wrap("You moved out " + LIGHT_GREEN.wrap(WARP_NAME) + " warp.")
    );

    public static final LangText WARP_CREATE_ERROR_LIMIT = LangText.of("Warps.Warp.Creation.Error.Limit",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Limit Reached!")),
        LIGHT_GRAY.wrap("You can't create more warps!")
    );

    public static final LangText WARP_CREATE_ERROR_WORLD   = LangText.of("Warps.Warp.Creation.Error.World",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Forbidden World!")),
        LIGHT_GRAY.wrap("You can't create warps in this world!")
    );

    public static final LangText WARP_CREATE_ERROR_EXISTS  = LangText.of("Warps.Warp.Creation.Error.Exists",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Already Exists!")),
        LIGHT_GRAY.wrap("Warp with such name already exists.")
    );

    public static final LangText WARP_CREATE_ERROR_UNSAFE  = LangText.of("Warps.Warp.Creation.Error.Unsafe",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Unsafe Location!")),
        LIGHT_GRAY.wrap("Please, choose another place.")
    );

    public static final LangText WARP_TELEPORT_DONE = LangText.of("Warps.Warp.Teleport.Done",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_ENDERMAN_TELEPORT),
        LIGHT_YELLOW.wrap(BOLD.wrap(WARP_NAME)),
        LIGHT_GRAY.wrap(WARP_DESCRIPTION)
    );

    public static final LangText WARP_TELEPORT_ERROR_NOT_ENOUGH_FUNDS = LangText.of("Warps.Warp.Teleport.Error.NotEnoughFunds",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Not Enough Funds!")),
        LIGHT_GRAY.wrap("You need " + LIGHT_RED.wrap("$" + WARP_VISIT_COST) + " to visit this warp!")
    );

    public static final LangText WARP_TELEPORT_ERROR_NO_PERMISSION = LangText.of("Warps.Warp.Teleport.Error.NoPermission",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("No Permission!")),
        LIGHT_GRAY.wrap("You don't have permission for " + LIGHT_RED.wrap(WARP_NAME) + " warp!")
    );

    public static final LangText WARP_TELEPORT_ERROR_COOLDOWN = LangText.of("Warps.Warp.Teleport.Error.Cooldown",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Cooldown!")),
        LIGHT_GRAY.wrap("You can visit " + LIGHT_RED.wrap(WARP_NAME) + " again in " + LIGHT_RED.wrap(GENERIC_COOLDOWN))
    );

    public static final LangText WARP_TELEPORT_ERROR_DISABLED = LangText.of("Warps.Warp.Teleport.Error.Unreachable",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Unreachable Warp!")),
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(WARP_NAME) + " located in an unavailable world.")
    );

    public static final LangText WARP_TELEPORT_ERROR_TIME = LangText.of("Warps.Warp.Teleport.Error.Time",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_VILLAGER_NO),
        LIGHT_RED.wrap(BOLD.wrap("Wrong Time!")),
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(WARP_NAME) + " is not available for visits now.")
    );

    public static final LangText ERROR_COMMAND_INVALID_WARP_ARGUMENT = LangText.of("Warps.Error.Command.Argument.InvalidWarp",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid warp!"));


    public static final LangString EDITOR_ENTER_COST = LangString.of("Warps.Editor.Enter.CostMoney",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Cost]")));

    public static final LangString EDITOR_ENTER_COMMAND = LangString.of("Warps.Editor.Enter.Command",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Command Name]")));

    public static final LangString EDITOR_ENTER_TIMES = LangString.of("Warps.Editor.Enter.Times",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Times] " + LIGHT_GRAY.wrap(">") + " 10:00 14:00")));

    public static final LangString EDITOR_ENTER_COOLDOWN = LangString.of("Warps.Editor.Enter.Cooldown",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Cooldown]")));

    public static final LangString EDITOR_ENTER_NAME = LangString.of("Warps.Editor.Enter.Name",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Display Name]")));

    public static final LangString EDITOR_ENTER_DESCRIPTION = LangString.of("Warps.Editor.Enter.Description",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Description]")));

}
