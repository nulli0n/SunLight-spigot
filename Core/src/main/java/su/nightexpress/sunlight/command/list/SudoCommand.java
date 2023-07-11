package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SudoCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "sudo";

    private static final CommandFlag<Boolean> FLAG_CHAT = CommandFlag.booleanFlag("c");

    public SudoCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_SUDO);
        this.setUsage(plugin.getMessage(Lang.COMMAND_SUDO_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_SUDO_DESC));
        this.addFlag(FLAG_CHAT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return Collections.singletonList("<command>");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player target = plugin.getServer().getPlayer(result.getArg(0));
        if (target == null) {
            this.errorPlayer(sender);
            return;
        }
        if (sender == target) {
            this.plugin.getMessage(Lang.ERROR_COMMAND_SELF).send(sender);
            return;
        }
        if (target.hasPermission(Perms.COMMAND_SUDO_BYPASS)) {
            this.errorPermission(sender);
            return;
        }

        String command = Stream.of(result.getArgs()).skip(1).collect(Collectors.joining(" "));
        LangMessage message;
        if (result.hasFlag(FLAG_CHAT)) {
            target.chat(command);
            message = plugin.getMessage(Lang.COMMAND_SUDO_DONE_CHAT);
        }
        else {
            target.performCommand(command);
            message = plugin.getMessage(Lang.COMMAND_SUDO_DONE_COMMAND);
        }
        message.replace(Placeholders.GENERIC_COMMAND, command).replace(Placeholders.forPlayer(target)).send(sender);
    }
}
