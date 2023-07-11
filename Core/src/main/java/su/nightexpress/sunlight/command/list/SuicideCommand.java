package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

public class SuicideCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "suicide";

    public SuicideCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_SUICIDE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_SUICIDE_DESC));
        this.setPlayerOnly(true);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = (Player) sender;
        target.setHealth(0);
        plugin.getMessage(Lang.COMMAND_SUICIDE_DONE).replace(Placeholders.forPlayer(target)).broadcast();
    }
}
