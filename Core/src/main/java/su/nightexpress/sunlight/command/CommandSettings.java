package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;

import java.util.Set;

public class CommandSettings extends AbstractConfig {

    private final ConfigProperty<Boolean> cooldownsEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Commands.Cooldowns.Enabled",
        true,
        "When enabled, allows to set cooldown on any of SunLight's commands."
    );

    private final ConfigProperty<Boolean> conflictUnregisterEnabled = this.addProperty(ConfigTypes.BOOLEAN, "Commands.Conflict-Unregister.Enabled",
        false,
        ""
    );

    private final ConfigProperty<Set<String>> conflictUnregisterBlacklist = this.addProperty(ConfigTypes.STRING_SET_LOWER_CASE, "Commands.Conflict-Unregister.Blacklist",
        Set.of("WorldEdit", "WorldGuard", "FastAsyncWorldEdit"),
        ""
    );

    public boolean isCooldownsEnabled() {
        return this.cooldownsEnabled.get();
    }

    public boolean isConflictUnregisterEnabled() {
        return this.conflictUnregisterEnabled.get();
    }

    @NotNull
    public Set<String> getConflictUnregisterBlacklist() {
        return this.conflictUnregisterBlacklist.get();
    }
}
