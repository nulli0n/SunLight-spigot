package su.nightexpress.sunlight.module.afk.config;

import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.LangColors;

public class AfkLang implements LangColors {

    public static final LangKey COMMAND_AFK_DESC  = LangKey.of("Afk.Command.Afk.Desc", "Toggle [player's] AFK mode.");
    public static final LangKey COMMAND_AFK_USAGE = LangKey.of("Afk.Command.Afk.Usage", "[player] [-on] [-off] [-s]");

    public static final LangKey AFK_ENTER = LangKey.of("Afk.Mode.Enter",
        "<! prefix:\"false\" !>" + ORANGE + "[AFK]" + LIGHT_YELLOW + " Player " + ORANGE + Placeholders.Player.DISPLAY_NAME + LIGHT_YELLOW + " is AFK now.");
    public static final LangKey AFK_EXIT  = LangKey.of("Afk.Mode.Exit",
        "<! prefix:\"false\" !>" + ORANGE + "[AFK]" + LIGHT_YELLOW + " Player " + ORANGE + Placeholders.Player.DISPLAY_NAME + LIGHT_YELLOW + " returned " + ORANGE + Placeholders.GENERIC_TIME + LIGHT_YELLOW + " later.");

    public static final LangKey AFK_NOTIFY_PM       = LangKey.of("Afk.Notify.PrivateMessage",
        "<! prefix:\"false\" !>" + RED + "[AFK]" + LIGHT_YELLOW + " Player " + ORANGE + Placeholders.Player.DISPLAY_NAME + LIGHT_YELLOW + " is AFK and can miss your message.");
    public static final LangKey AFK_NOTIFY_TELEPORT = LangKey.of("Afk.Notify.TeleportRequest",
        "<! prefix:\"false\" !>" + RED + "[AFK]" + LIGHT_YELLOW + " Player " + ORANGE + Placeholders.Player.DISPLAY_NAME + LIGHT_YELLOW + " is AFK and can miss your request.");

}
