package su.nightexpress.sunlight.module.chat.format;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.config.Writeable;

public class FormatComponent implements Writeable {

    private final String text;

    public FormatComponent(@NotNull String text) {
        this.text = text;
    }

    @NotNull
    public static FormatComponent read(@NotNull FileConfig config, @NotNull String path) {
        String text = config.getString(path + ".Text", "");
        return new FormatComponent(text);
    }

    @Override
    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Text", this.text);
    }

    @NotNull
    public String getText() {
        return this.text;
    }
}
