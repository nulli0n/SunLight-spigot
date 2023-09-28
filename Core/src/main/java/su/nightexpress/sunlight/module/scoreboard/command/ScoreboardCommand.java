package su.nightexpress.sunlight.module.scoreboard.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.scoreboard.ScoreboardModule;
import su.nightexpress.sunlight.module.scoreboard.config.SBLang;
import su.nightexpress.sunlight.module.scoreboard.config.SBPerms;

public class ScoreboardCommand extends ToggleCommand {

    public static final String NAME = "scoreboard";

    private final ScoreboardModule module;

    public ScoreboardCommand(@NotNull ScoreboardModule module, @NotNull String[] aliases) {
        super(module.plugin(), aliases, SBPerms.COMMAND_SCOREBOARD, SBPerms.COMMAND_SCOREBOARD_OTHERS, 0);
        this.module = module;
        this.setAllowDataLoad();
        this.setUsage(this.plugin.getMessage(SBLang.COMMAND_SCOREBOARD_USAGE));
        this.setDescription(this.plugin.getMessage(SBLang.COMMAND_SCOREBOARD_DESC));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        SunUser user = plugin.getUserManager().getUserData(target);
        Mode mode = this.getMode(sender, result);
        boolean state = mode.apply(user.getSettings().get(ScoreboardModule.SETTING_SCOREBOARD));

        if (state) {
            this.module.addBoard(target);
        }
        else {
            this.module.removeBoard(target);
        }
        user.getSettings().set(ScoreboardModule.SETTING_SCOREBOARD, state);
        this.plugin.getUserManager().saveUser(user);

        if (sender != target) {
            this.plugin.getMessage(SBLang.COMMAND_SCOREBOARD_TARGET)
                .replace(Placeholders.forPlayer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            this.plugin.getMessage(SBLang.COMMAND_SCOREBOARD_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
    }
}
