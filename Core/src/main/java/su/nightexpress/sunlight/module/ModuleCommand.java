package su.nightexpress.sunlight.module;

import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nightexpress.sunlight.SunLight;

public abstract class ModuleCommand<M extends Module> extends AbstractCommand<SunLight> {

    protected final M module;

    public ModuleCommand(@NotNull M module, @NotNull String[] aliases, @Nullable Permission permission) {
        super(module.plugin(), aliases, permission);
        this.module = module;
    }

    @NotNull
    public M getModule() {
        return module;
    }
}
