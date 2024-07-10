package su.nightexpress.sunlight.module.chat.format;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;

public class FormatComponent {

    private final String id;
    private final String text;

    public FormatComponent(@NotNull String id, @NotNull String text) {
        this.id = id.toLowerCase();
        this.text = text;
    }

    @NotNull
    public static FormatComponent read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String text = config.getString(path + ".Text", "");
        return new FormatComponent(id, text);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Text", this.text);
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getText() {
        return text;
    }
}
