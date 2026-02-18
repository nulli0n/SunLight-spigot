package su.nightexpress.sunlight.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.sunlight.hook.HookId;
import su.nightexpress.sunlight.SLUtils;

import java.util.Optional;

public class LoadCondition {

    private static final LoadCondition SUCCESS = new LoadCondition(true, null);

    private final boolean success;
    private final String  reason;

    private LoadCondition(boolean success, @Nullable String reason) {
        this.success = success;
        this.reason = reason;
    }

    @NotNull
    public static LoadCondition success() {
        return SUCCESS;
    }

    @NotNull
    public static LoadCondition failure(@NotNull String reason) {
        return new LoadCondition(false, reason);
    }

    @NotNull
    public static LoadCondition packetLibrary() {
        return SLUtils.hasPacketLibrary() ? LoadCondition.success() : LoadCondition.failure("No packet library plugin installed. Install %s or %s for the module to work."
            .formatted(HookId.PACKET_EVENTS, HookId.PROTOCOL_LIB)
        );
    }

    public boolean isSuccess() {
        return this.success;
    }

    @NotNull
    public Optional<String> reason() {
        return Optional.ofNullable(this.reason);
    }
}
