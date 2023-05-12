package su.nightexpress.sunlight.module.menu;

import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.module.ModuleId;
import su.nightexpress.sunlight.module.menu.command.MenuCommand;

public class MenuPerms {

    private static final String PREFIX = Perms.PREFIX + ModuleId.MENU + ".";

    public static final String MENU            = PREFIX + "menu.";
    public static final String CMD_MENU        = PREFIX + "cmd." + MenuCommand.NAME;
    public static final String CMD_MENU_OTHERS = PREFIX + "cmd." + MenuCommand.NAME + ".others";
}
