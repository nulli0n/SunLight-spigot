package su.nightexpress.sunlight.module.chat.config;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Placeholders;
import su.nexmedia.engine.utils.PlayerUtil;

import java.util.Set;
import java.util.stream.Collectors;

public class ChatMention {

    private final String      format;
    private final Set<String> groups;

    public ChatMention(@NotNull String format, @NotNull Set<String> groups) {
        this.format = Colorizer.apply(format);
        this.groups = groups.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    @NotNull
    public static ChatMention read(@NotNull JYML cfg, @NotNull String path) {
        String format = cfg.getString(path + ".Format", "");
        Set<String> groups = cfg.getStringSet(path + ".Affected_Groups");
        return new ChatMention(format, groups);
    }

    public static void write(@NotNull ChatMention mention, @NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Format", mention.getFormat());
        cfg.set(path + ".Affected_Groups", mention.getGroups());
    }

    @NotNull
    public String getFormat() {
        return format;
    }

    @NotNull
    public Set<String> getGroups() {
        return groups;
    }

    public boolean isApplicable(@NotNull Player player) {
        Set<String> userGroups = PlayerUtil.getPermissionGroups(player);
        return this.getGroups().stream().anyMatch(need -> need.equalsIgnoreCase(Placeholders.WILDCARD) || userGroups.contains(need));
    }
}
