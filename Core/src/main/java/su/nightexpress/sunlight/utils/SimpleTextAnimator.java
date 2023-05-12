package su.nightexpress.sunlight.utils;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;

import java.util.List;

public class SimpleTextAnimator {

    private final String   id;
    private final String[] lines;
    private final int      interval;
    private final String   placeholder;

    public SimpleTextAnimator(@NotNull String id, @NotNull List<String> messages, int interval) {
        this.id = id.toLowerCase();
        this.lines = Colorizer.apply(messages).toArray(new String[messages.size()]);
        this.interval = Math.max(1, interval);
        this.placeholder = "%animation:" + getId() + "%";
    }

    @NotNull
    public static SimpleTextAnimator read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        int interval = cfg.getInt(path + ".Update_Interval_MS");
        List<String> messages = cfg.getStringList(path + ".Texts");

        return new SimpleTextAnimator(id, messages, interval);
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getMessage() {
        return this.lines[(int) (System.currentTimeMillis() % (this.lines.length * this.interval) / this.interval)];
    }

    @NotNull
    public final String getPlaceholder() {
        return placeholder;
    }

    @NotNull
    public final String replace(@NotNull String text) {
        return text.replace(this.getPlaceholder(), this.getMessage());
    }
}
