package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.nightcore.util.RankMap;

import java.util.List;
import java.util.Set;

public class CommandCooldown {

    private final String                 id;
    private final String                 commandName;
    private final List<String[]>   arguments;
    private final RankMap<Integer> cooldown;

    public CommandCooldown(@NotNull String id, @NotNull String commandName, @NotNull List<String[]> arguments,
                           @NotNull RankMap<Integer> cooldown) {
        this.id = id.toLowerCase();
        this.commandName = commandName.toLowerCase();
        this.arguments = arguments;
        this.cooldown = cooldown;
    }

    @NotNull
    public static CommandCooldown read(@NotNull FileConfig cfg, @NotNull String path, @NotNull String id) {
        String commandName = cfg.getString(path + ".Command", "");
        List<String[]> arguments = cfg.getStringList(path + ".Arguments").stream().map(str -> str.split(",")).toList();
        RankMap<Integer> cooldown = RankMap.readInt(cfg, path + ".Cooldown", 0); // TODO

        return new CommandCooldown(id, commandName, arguments, cooldown);
    }

    public void write(@NotNull FileConfig cfg, @NotNull String path) {
        cfg.set(path + ".Command", this.getCommandName());
        cfg.set(path + ".Arguments", this.getArguments().stream().map(arr -> String.join(",", arr)).toList());
        this.getCooldown().write(cfg, path + ".Cooldown");
    }

    public boolean isApplicable(@NotNull String command, @NotNull String[] args) {
        Set<String> aliases = CommandUtil.getAliases(command, true);
        if (!aliases.contains(this.getCommandName())) return false;

        if (this.getArguments().isEmpty()) return true;
        if (this.getArguments().size() > args.length) return false;

        for (int index = 0; index < this.getArguments().size(); index++) {
            String[] argsCheck = this.getArguments().get(index);
            String realArg = args[index];
            if (!Lists.contains(argsCheck, realArg)) return false;
        }

        return true;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public String getCommandName() {
        return commandName;
    }

    @NotNull
    public List<String[]> getArguments() {
        return arguments;
    }

    @NotNull
    public RankMap<Integer> getCooldown() {
        return cooldown;
    }
}
