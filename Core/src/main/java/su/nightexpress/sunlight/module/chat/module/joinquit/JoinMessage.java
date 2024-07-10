package su.nightexpress.sunlight.module.chat.module.joinquit;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.Plugins;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.chat.util.Placeholders;

import java.util.Set;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;
import static su.nightexpress.sunlight.module.chat.util.Placeholders.*;

public class JoinMessage {

    private final Set<String> ranks;
    private final int         priority;
    private final String      message;
    private final DisplayMode displayMode;

    public JoinMessage(@NotNull Set<String> ranks, int priority, @NotNull String message, @NotNull DisplayMode displayMode) {
        this.ranks = ranks;
        this.priority = priority;
        this.message = message;
        this.displayMode = displayMode;
    }

    @NotNull
    public static JoinMessage getDefaultJoins() {
        return new JoinMessage(Lists.newSet(Placeholders.WILDCARD), 0, GRAY.enclose("[" + GREEN.enclose("+") + "]" + " " + "%vault_prefix%" + PLAYER_DISPLAY_NAME), DisplayMode.CHAT);
    }

    @NotNull
    public static JoinMessage getDefaultQuits() {
        return new JoinMessage(Lists.newSet(Placeholders.WILDCARD), 0, GRAY.enclose("[" + RED.enclose("-") + "]" + " " + "%vault_prefix%" + PLAYER_DISPLAY_NAME), DisplayMode.CHAT);
    }

    @NotNull
    public static JoinMessage read(@NotNull FileConfig config, @NotNull String path) {
        Set<String> ranks = config.getStringSet(path + ".Ranks");
        int priority = config.getInt(path + ".Priority", 0);
        String message = config.getString(path + ".Message", "");
        DisplayMode displayMode = config.getEnum(path + ".DisplayMode", DisplayMode.class, DisplayMode.CHAT);

        return new JoinMessage(ranks, priority, message, displayMode);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Ranks", this.ranks);
        config.set(path + ".Priority", this.priority);
        config.set(path + ".Message", this.message);
        config.set(path + ".DisplayMode", this.displayMode.name());
    }

    public void display(@NotNull SunLightPlugin plugin, @NotNull Player player) {
        String message = Placeholders.forPlayer(player).apply(this.getMessage());

        if (Plugins.hasPlaceholderAPI()) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        String finalMessage = message;

        plugin.getServer().getOnlinePlayers().forEach(other -> {
            if (this.displayMode == DisplayMode.CHAT) {
                Players.sendModernMessage(other, finalMessage);
            }
            else if (this.displayMode == DisplayMode.ACTION_BAR) {
                Players.sendActionBarText(other, finalMessage);
            }
        });
    }

    public boolean isAvailable(@NotNull Player player) {
        if (this.ranks.isEmpty()) return false;
        if (this.ranks.contains(WILDCARD)) return true;

        Set<String> groups = Players.getPermissionGroups(player);
        return this.ranks.stream().anyMatch(groups::contains);
    }

    @NotNull
    public Set<String> getRanks() {
        return ranks;
    }

    public int getPriority() {
        return priority;
    }

    @NotNull
    public String getMessage() {
        return message;
    }

    @NotNull
    public DisplayMode getDisplayMode() {
        return displayMode;
    }
}
