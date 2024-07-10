package su.nightexpress.sunlight.module.chat.module.deathmessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.random.Rnd;

import java.util.List;

public class DeathMessage {

    private final List<String> messages;

    public DeathMessage(@NotNull List<String> messages) {
        this.messages = messages;
    }

    @NotNull
    public static DeathMessage simple(@NotNull String message) {
        return new DeathMessage(Lists.newList(message));
    }

    @NotNull
    public static DeathMessage read(@NotNull FileConfig config, @NotNull String path) {
        List<String> messages = config.getStringList(path + ".Messages");
        return new DeathMessage(messages);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Messages", this.messages);
    }

    @Nullable
    public String selectMessage() {
        return this.messages.isEmpty() ? null : Rnd.get(this.messages);
    }

    @NotNull
    public List<String> getMessages() {
        return messages;
    }
}
