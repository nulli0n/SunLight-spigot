package su.nightexpress.sunlight.module.bans.punishment;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;

public class PunishmentReason {

    private final String id;
    private       String message;

    public PunishmentReason(@NotNull String id, @NotNull String message) {
        this.id = id.toLowerCase();
        this.setMessage(message);
    }

    @NotNull
    public static PunishmentReason read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        String message = cfg.getString(path + ".Message", "");
        return new PunishmentReason(id, message);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Message", this.getMessage());
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    public void setMessage(@NotNull String message) {
        this.message = Colorizer.apply(message);
    }
}
