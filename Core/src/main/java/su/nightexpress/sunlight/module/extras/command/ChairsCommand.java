package su.nightexpress.sunlight.module.extras.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.extras.config.ExtrasLang;
import su.nightexpress.sunlight.module.extras.config.ExtrasPerms;
import su.nightexpress.sunlight.module.extras.impl.chairs.ChairsManager;

public class ChairsCommand extends ToggleCommand {

    public static final String NAME = "chairs";

    public ChairsCommand(@NotNull ChairsManager chairsManager, @NotNull String[] aliases) {
        super(chairsManager.plugin(), aliases, ExtrasPerms.COMMAND_CHAIRS, ExtrasPerms.COMMAND_CHAIRS_OTHERS, 0);
        this.setAllowDataLoad();
        this.setDescription(this.plugin.getMessage(ExtrasLang.COMMAND_CHAIRS_DESC));
        this.setUsage(this.plugin.getMessage(ExtrasLang.COMMAND_CHAIRS_USAGE));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Mode mode = this.getMode(sender, result);
        SunUser user = this.plugin.getUserManager().getUserData(target);
        boolean state = mode.apply(ChairsManager.isChairsEnabled(user));

        user.getSettings().set(ChairsManager.SETTING_CHAIRS, state);
        this.plugin.getUserManager().saveUser(user);

        if (sender != target) {
            this.plugin.getMessage(ExtrasLang.COMMAND_CHAIRS_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            this.plugin.getMessage(ExtrasLang.COMMAND_CHAIRS_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
    }
}
