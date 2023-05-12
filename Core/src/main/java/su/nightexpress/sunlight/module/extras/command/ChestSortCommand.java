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
import su.nightexpress.sunlight.module.extras.impl.chestsort.SortManager;

public class ChestSortCommand extends ToggleCommand {

    public static final String NAME = "chestsort";

    public ChestSortCommand(@NotNull SortManager manager, @NotNull String[] aliases) {
        super(manager.plugin(), aliases, ExtrasPerms.COMMAND_CHEST_SORT, ExtrasPerms.COMMAND_CHEST_SORT_OTHERS, 0);
        this.setAllowDataLoad();
        this.setDescription(this.plugin.getMessage(ExtrasLang.COMMAND_CHEST_SORT_DESC));
        this.setUsage(this.plugin.getMessage(ExtrasLang.COMMAND_CHEST_SORT_USAGE));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        Mode mode = this.getMode(sender, result);
        SunUser user = this.plugin.getUserManager().getUserData(target);
        boolean state = mode.apply(SortManager.isChestSortEnabled(user));

        user.getSettings().set(SortManager.SETTING_CHEST_SORT, state);
        user.saveData(this.plugin);

        if (sender != target) {
            this.plugin.getMessage(ExtrasLang.COMMAND_CHEST_SORT_TARGET)
                .replace(Placeholders.Player.replacer(target))
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            this.plugin.getMessage(ExtrasLang.COMMAND_CHEST_SORT_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(state))
                .send(target);
        }
    }
}
