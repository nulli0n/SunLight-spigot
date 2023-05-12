package su.nightexpress.sunlight.module.menu;

import su.nexmedia.engine.api.lang.LangKey;

public class MenuLang {

    public static final LangKey Command_Menu_Desc        = new LangKey("Menu.Command.Menu.Desc", "Open specified menu.");
    public static final LangKey Command_Menu_Usage       = new LangKey("Menu.Command.Menu.Usage", "<id> [player]");
    public static final LangKey Command_Menu_Others_Done = new LangKey("Menu.Command.Menu.Others.Done", "{message: ~prefix: false;}&7Opened &e%menu% menu &7for &e%player%&7.");
    public static final LangKey Menu_Error_Invalid       = new LangKey("Menu.Error.Invalid", "{message: ~prefix: false;}&cNo such menu!");
    public static final LangKey Menu_Error_NoPermission  = new LangKey("Menu.Error.NoPermission", "{message: ~prefix: false;}&cYou don't have permission to use this menu!");
}
