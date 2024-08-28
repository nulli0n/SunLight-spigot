package su.nightexpress.sunlight.command.cooldown;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.nightcore.util.Players;

import java.util.*;

public class CommandCooldown {

    private final String               id;
    private final Set<String>          commandNames;
    private final Set<ArgumentPattern> patterns;
    private final Map<String, Integer> cooldownMap;

    public CommandCooldown(@NotNull String id, @NotNull Set<String> commandNames, @NotNull Set<ArgumentPattern> patterns, @NotNull Map<String, Integer> cooldownMap) {
        this.id = id.toLowerCase();
        this.commandNames = Lists.modify(commandNames, String::toLowerCase);
        this.patterns = new HashSet<>(patterns);
        this.cooldownMap = cooldownMap;
    }

    @NotNull
    public static CommandCooldown read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String[] rawNames = config.getString(path + ".Commands", "").split(",");
        Set<String> commandNames = new HashSet<>(Arrays.asList(rawNames));

        Set<ArgumentPattern> patterns = new HashSet<>();
        for (String pattern : config.getStringList(path + ".Patterns")) {
            patterns.add(ArgumentPattern.from(pattern));
        }

        Map<String, Integer> cooldownMap = new HashMap<>();
        for (String rank : config.getSection(path + ".Cooldown")) {
            cooldownMap.put(rank.toLowerCase(), config.getInt(path + ".Cooldown." + rank));
        }

        return new CommandCooldown(id, commandNames, patterns, cooldownMap);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Commands", String.join(",", this.getCommandNames()));
        config.set(path + ".Patterns", Lists.modify(this.patterns, ArgumentPattern::asString));
        config.remove(path + ".Cooldown");
        this.cooldownMap.forEach((rank, time) -> {
            config.set(path + ".Cooldown." + rank, time);
        });
    }

    public int getCooldown(@NotNull Player player) {
        Set<String> ranks = Players.getPermissionGroups(player);

        return this.cooldownMap.entrySet().stream()
            .filter(entry -> entry.getKey().equalsIgnoreCase(Placeholders.DEFAULT) || ranks.contains(entry.getKey()))
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getValue)
            .orElse(0);
    }

    public boolean isApplicable(@NotNull String command, @NotNull String commandMessage) {
        if (this.commandNames.isEmpty()) return false;
        if (this.patterns.isEmpty()) return false;

        String[] commandLine = commandMessage.split(" ");
        commandLine[0] = command; // Replace slashed/semicoloned command with a pure name extracted before.
        if (command.isBlank()) return false;

        Set<String> aliases = CommandUtil.getAliases(command, true);
        if (aliases.stream().noneMatch(this.commandNames::contains)) return false;

        return this.patterns.stream().anyMatch(pattern -> pattern.isApplicable(commandLine));
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Set<String> getCommandNames() {
        return commandNames;
    }

    @NotNull
    public Set<ArgumentPattern> getPatterns() {
        return patterns;
    }

    @NotNull
    public Map<String, Integer> getCooldownMap() {
        return cooldownMap;
    }
}
