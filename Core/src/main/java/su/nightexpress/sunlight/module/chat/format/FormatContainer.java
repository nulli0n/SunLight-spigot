package su.nightexpress.sunlight.module.chat.format;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Set;

public class FormatContainer {

    private final int         priority;
    private final String      nameFormat;
    private final String      messageFormat;
    private final Set<String> applicableRanks;

    public FormatContainer(int priority, @NotNull String nameFormat, @NotNull String messageFormat, @NotNull Set<String> applicableRanks) {
        this.priority = priority;
        this.nameFormat = nameFormat;
        this.messageFormat = messageFormat;
        this.applicableRanks = applicableRanks;
    }

    @NotNull
    public static FormatContainer read(@NotNull FileConfig config, @NotNull String path) {
        int priority = config.getInt(path + ".Priority");
        String format = config.getString(path + ".Name", "");
        String message = config.getString(path + ".Message", "");
        Set<String> applicableRanks = config.getStringSet(path + ".Ranks");

        return new FormatContainer(priority, format, message, applicableRanks);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Priority", this.getPriority());
        config.set(path + ".Name", this.getNameFormat());
        config.set(path + ".Message", this.getMessageFormat());
        config.set(path + ".Ranks", this.getApplicableRanks());
    }

    public boolean isApplicable(@NotNull Player player) {
        if (this.applicableRanks.isEmpty()) return false;
        if (this.applicableRanks.contains(Placeholders.WILDCARD)) return true;

        Set<String> groups = Players.getPermissionGroups(player);
        return this.applicableRanks.stream().anyMatch(groups::contains);
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getNameFormat() {
        return nameFormat;
    }

    @NotNull
    public String getMessageFormat() {
        return messageFormat;
    }

    @NotNull
    public Set<String> getApplicableRanks() {
        return applicableRanks;
    }
}
