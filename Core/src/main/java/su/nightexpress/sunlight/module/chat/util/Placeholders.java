package su.nightexpress.sunlight.module.chat.util;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.module.chat.format.FormatComponent;

import java.util.Collection;
import java.util.function.Function;

public class Placeholders extends su.nightexpress.sunlight.Placeholders {

    public static final String CHANNEL_ID     = "%channel_id%";
    public static final String CHANNEL_NAME   = "%channel_name%";
    public static final String CHANNEL_RADIUS = "%channel_radius%";

    public static final String ITEM_NAME  = "%item_name%";
    public static final String ITEM_VALUE = "%item_value%";

    public static final String PLAYER_PREFIX           = "%player_prefix%";
    public static final String PLAYER_SUFFIX                   = "%player_suffix%";
    public static final String PLAYER_NAME              = "%player_name%";
    public static final String PLAYER_DISPLAY_NAME = "%player_display_name%";
    public static final String PLAYER_WORLD        = "%player_world%";

    @Deprecated
    public static final String FORMAT_PLAYER_NAME    = "%format_player_name%";
    @Deprecated
    public static final String FORMAT_PLAYER_MESSAGE = "%format_player_message%";
    @Deprecated
    public static final String FORMAT_PLAYER_COLOR   = "%format_player_color%";

    public static final Function<FormatComponent, String> FORMAT_COMPONENT = component -> "%" + component.getId() + "%";

    @NotNull
    public static PlaceholderMap forComponent(@NotNull FormatComponent component) {
        return new PlaceholderMap().add(FORMAT_COMPONENT.apply(component), component.getText());
    }

    @NotNull
    public static PlaceholderMap forComponents(@NotNull Collection<FormatComponent> components) {
        PlaceholderMap map = new PlaceholderMap();
        components.forEach(component -> map.add(FORMAT_COMPONENT.apply(component), component.getText()));
        return map;
    }
}
