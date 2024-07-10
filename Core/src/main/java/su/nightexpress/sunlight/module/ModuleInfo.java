package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.Nullable;

public class ModuleInfo {

    private Class<?> configClass;
    private Class<?> langClass;
    private Class<?> permissionsClass;

    public ModuleInfo() {

    }

    @Nullable
    public Class<?> getConfigClass() {
        return configClass;
    }

    public ModuleInfo setConfigClass(@Nullable Class<?> configClass) {
        this.configClass = configClass;
        return this;
    }

    @Nullable
    public Class<?> getLangClass() {
        return langClass;
    }

    public ModuleInfo setLangClass(@Nullable Class<?> langClass) {
        this.langClass = langClass;
        return this;
    }

    @Nullable
    public Class<?> getPermissionsClass() {
        return permissionsClass;
    }

    public ModuleInfo setPermissionsClass(@Nullable Class<?> permissionsClass) {
        this.permissionsClass = permissionsClass;
        return this;
    }
}
