package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.utils.ArrayUtil;
import su.nexmedia.engine.utils.PlayerRankMap;

import java.util.List;
import java.util.Set;

public class CommandCooldown {

    private final String                 id;
    private final String                 commandName;
    private final List<String[]>         arguments;
    private final PlayerRankMap<Integer> cooldown;

    public CommandCooldown(@NotNull String id, @NotNull String commandName, @NotNull List<String[]> arguments,
                           @NotNull PlayerRankMap<Integer> cooldown) {
        this.id = id.toLowerCase();
        this.commandName = commandName.toLowerCase();
        this.arguments = arguments;
        this.cooldown = cooldown;
    }

    @NotNull
    public static CommandCooldown read(@NotNull JYML cfg, @NotNull String path, @NotNull String id) {
        String commandName = cfg.getString(path + ".Command", "");
        List<String[]> arguments = cfg.getStringList(path + ".Arguments").stream().map(str -> str.split(",")).toList();
        PlayerRankMap<Integer> cooldown = PlayerRankMap.read(cfg, path + ".Cooldown", Integer.class);

        return new CommandCooldown(id, commandName, arguments, cooldown);
    }

    public void write(@NotNull JYML cfg, @NotNull String path) {
        cfg.set(path + ".Command", this.getCommandName());
        cfg.set(path + ".Arguments", this.getArguments().stream().map(arr -> String.join(",", arr)).toList());
        this.getCooldown().write(cfg, path + ".Cooldown");
    }

    public boolean isApplicable(@NotNull String command, @NotNull String[] args) {
        Set<String> aliases = CommandRegister.getAliases(command, true);
        if (!aliases.contains(this.getCommandName())) return false;

        if (this.getArguments().isEmpty()) return true;
        if (this.getArguments().size() > args.length) return false;

        for (int index = 0; index < this.getArguments().size(); index++) {
            String[] argsCheck = this.getArguments().get(index);
            String realArg = args[index];
            if (!ArrayUtil.contains(argsCheck, realArg)) return false;
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
    public PlayerRankMap<Integer> getCooldown() {
        return cooldown;
    }
}
