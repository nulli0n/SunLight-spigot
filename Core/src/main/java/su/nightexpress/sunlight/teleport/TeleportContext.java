package su.nightexpress.sunlight.teleport;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.sunlight.module.Module;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class TeleportContext {

    private final Module                module;
    private final CommandSender         sender;
    private final Player                target;
    private final EnumSet<TeleportFlag> flags;
    private final Runnable              callback;

    private Location destination;

    public TeleportContext(@NonNull Module module,
                           @Nullable CommandSender sender,
                           @NonNull Player target,
                           @NonNull Location destination,
                           @NonNull EnumSet<TeleportFlag> flags,
                           @Nullable Runnable callback) {
        this.module = module;
        this.sender = sender;
        this.target = target;
        this.flags = flags;
        this.callback = callback;

        this.setDestination(destination);
    }

    @NonNull
    public static Builder builder(@NonNull Module module, @NonNull Player target, @NonNull Location destination) {
        return new Builder(module, target, destination);
    }

    public void runCallback() {
        if (this.hasCallback()) {
            this.callback.run();
        }
    }

    public boolean hasSender() {
        return this.sender != null;
    }

    public boolean hasFlags() {
        return !this.flags.isEmpty();
    }

    public boolean hasFlag(@NonNull TeleportFlag flag) {
        return this.flags.contains(flag);
    }

    public boolean hasCallback() {
        return this.callback != null;
    }

    @NonNull
    public CommandSender getExecutor() {
        return this.hasSender() ? this.sender : this.target;
    }

    @NonNull
    public Module getModule() {
        return this.module;
    }

    @Nullable
    public CommandSender getSender() {
        return this.sender;
    }

    @NonNull
    public Player getTarget() {
        return this.target;
    }

    @NonNull
    public Location getDestination() {
        return this.destination;
    }

    public void setDestination(@NonNull Location destination) {
        this.destination = destination.clone();
    }

    @NonNull
    public EnumSet<TeleportFlag> getFlags() {
        return this.flags;
    }

    @Nullable
    public Runnable getCallback() {
        return this.callback;
    }
    
    public static class Builder {

        private final Module            module;
        private final Player            target;
        private final Location          destination;
        private final Set<TeleportFlag> flags;

        private CommandSender sender;
        private Runnable      callback;
        
        Builder(@NonNull Module module, @NonNull Player target, @NonNull Location destination) {
            this.module = module;
            this.target = target;
            this.destination = destination.clone();
            this.flags = new HashSet<>();
        }

        @NonNull
        public TeleportContext build() {
            EnumSet<TeleportFlag> flagSet = this.flags.isEmpty() ? EnumSet.noneOf(TeleportFlag.class) : EnumSet.copyOf(this.flags);
            return new TeleportContext(this.module, this.sender, this.target, this.destination, flagSet, this.callback);
        }

        @NonNull
        public Builder sender(@NonNull CommandSender sender) {
            if (sender != this.target) {
                this.sender = sender;
            }
            return this;
        }

        @NonNull
        public Builder withFlag(@NonNull TeleportFlag flag) {
            this.flags.add(flag);
            return this;
        }

        @NonNull
        public Builder withFlagIf(@NonNull TeleportFlag flag, @NonNull Supplier<Boolean> predicate) {
            if (predicate.get()) {
                return this.withFlag(flag);
            }
            return this;
        }

        @NonNull
        public Builder callback(@NonNull Runnable callback) {
            this.callback = callback;
            return this;
        }
    }
}
