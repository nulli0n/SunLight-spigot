package su.nightexpress.sunlight.module.bans.punishment;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;

public class PunishmentReason {

    private String text;

    public PunishmentReason(@NotNull String text) {
        this.setText(text);
    }

    @NotNull
    public static PunishmentReason read(@NotNull FileConfig config, @NotNull String path) {
        String message = ConfigValue.create(path + ".Message", "Violation of the rules.").read(config);

        return new PunishmentReason(message);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Message", this.getText());
    }

    @NotNull
    public String getText() {
        return text;
    }

    public void setText(@NotNull String text) {
        this.text = text;
    }
}
