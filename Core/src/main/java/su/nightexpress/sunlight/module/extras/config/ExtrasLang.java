package su.nightexpress.sunlight.module.extras.config;

import su.nexmedia.engine.api.lang.LangColors;
import su.nexmedia.engine.api.lang.LangKey;
import su.nightexpress.sunlight.Placeholders;

public class ExtrasLang implements LangColors {

    public static final LangKey COMMAND_CHAIRS_DESC   = LangKey.of("Extras.Command.Chairs.Desc", "Toggle [player's] chairs feature.");
    public static final LangKey COMMAND_CHAIRS_USAGE  = LangKey.of("Extras.Command.Chairs.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_CHAIRS_NOTIFY = LangKey.of("Extras.Command.Chairs.Notify", LIGHT_YELLOW + "Auto-Chairs " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_CHAIRS_TARGET = LangKey.of("Extras.Command.Chairs.Target", LIGHT_YELLOW + "Auto-Chairs " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");

    public static final LangKey COMMAND_SIT_DESC   = LangKey.of("Extras.Command.Sit.Desc", "Force [player] to sit.");
    public static final LangKey COMMAND_SIT_USAGE  = LangKey.of("Extras.Command.Sit.Usage", "[player] [-s]");
    public static final LangKey COMMAND_SIT_NOTIFY = LangKey.of("Extras.Command.Sit.Notify", LIGHT_YELLOW + "You are sitting now.");
    public static final LangKey COMMAND_SIT_TARGET = LangKey.of("Extras.Command.Sit.Target", LIGHT_YELLOW + "Made " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + " to sit.");

    public static final LangKey COMMAND_CHEST_SORT_DESC   = LangKey.of("Extras.Command.ChestSort.Desc", "Toggle [player's] chest sort feature.");
    public static final LangKey COMMAND_CHEST_SORT_USAGE  = LangKey.of("Extras.Command.ChestSort.Usage", "[player] [-on] [-off] [-s]");
    public static final LangKey COMMAND_CHEST_SORT_NOTIFY = LangKey.of("Extras.Command.ChestSort.Notify", LIGHT_YELLOW + "Chest Sort " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + ".");
    public static final LangKey COMMAND_CHEST_SORT_TARGET = LangKey.of("Extras.Command.ChestSort.Target", LIGHT_YELLOW + "Chest Sort " + ORANGE + Placeholders.GENERIC_STATE + LIGHT_YELLOW + " for " + ORANGE + Placeholders.PLAYER_NAME + LIGHT_YELLOW + ".");

}
