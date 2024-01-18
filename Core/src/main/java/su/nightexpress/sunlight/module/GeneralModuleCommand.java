package su.nightexpress.sunlight.module;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nightexpress.sunlight.SunLight;

public abstract class GeneralModuleCommand <M extends Module> extends GeneralCommand<SunLight> {

    protected final M module;

    public GeneralModuleCommand(@NotNull M module, @NotNull String[] aliases, @Nullable Permission permission) {
        this(module, aliases, permission == null ? null : permission.getName());
    }

    public GeneralModuleCommand(@NotNull M module, @NotNull String[] aliases, @Nullable String permission) {
        super(module.plugin(), aliases, permission);
        this.module = module;
    }

    @NotNull
    public M getModule() {
        return module;
    }
}
