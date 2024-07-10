package su.nightexpress.sunlight.module.customtext.config;

import su.nightexpress.nightcore.util.wrapper.UniPermission;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Perms;

public class CTextPerms {

    public static final String PREFIX = Perms.PREFIX + "customtext.";
    public static final String PREFIX_COMMAND = PREFIX + "command.";
    public static final String PREFIX_TEXT = PREFIX + "text.";

    public static final UniPermission MODULE = new UniPermission(PREFIX + Placeholders.WILDCARD);
    public static final UniPermission COMMAND = new UniPermission(PREFIX_COMMAND + Placeholders.WILDCARD);
    public static final UniPermission TEXTS = new UniPermission(PREFIX_TEXT + Placeholders.WILDCARD);

    public static final UniPermission COMMAND_TEXT = new UniPermission(PREFIX_COMMAND + "text");

    static {
        Perms.PLUGIN.addChildren(MODULE);

        MODULE.addChildren(COMMAND, TEXTS);

        COMMAND.addChildren(
            COMMAND_TEXT
        );
    }
}
