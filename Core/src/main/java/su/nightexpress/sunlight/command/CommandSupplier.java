package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JYML;
import su.nightexpress.sunlight.SunLight;

public interface CommandSupplier {

    @NotNull GeneralCommand<SunLight> supply(@NotNull JYML cfg, @NotNull String[] aliases);

}
