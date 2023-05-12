package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ChangeCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;

public class ExpCommand extends ChangeCommand {

    public static final String NAME = "exp";

    private static final CommandFlag<Boolean> FLAG_LEVEL = CommandFlag.booleanFlag("l");

    public ExpCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_EXP, Perms.COMMAND_EXP_OTHERS);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_EXP_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_EXP_USAGE));
        this.addFlag(CommandFlags.SILENT, FLAG_LEVEL);
    }

    private int getExpRequired(int level) {
        if (level <= 15) return 2 * level + 7;
        if (level <= 30) return 5 * level - 38;
        return 9 * level - 158;
    }

    @Override
    protected List<String> getTab(@NotNull Player player) {
        return Arrays.asList("10", "20", "100");
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull CommandResult result, @NotNull Mode mode, @NotNull Player target, double amount1) {
        int amount = (int) amount1;
        int levelHas = target.getLevel();
        int expHas = target.getTotalExperience();

        if (result.hasFlag(FLAG_LEVEL)) {
            int levelFinal = Math.max(0, mode.modify(levelHas, amount));
            int expBasic = 0;
            int expLeft = (int) (this.getExpRequired(levelFinal) * target.getExp());
            for (int level = 0; level < levelFinal; level++) {
                expBasic += (this.getExpRequired(level));
            }
            expHas = expBasic + expLeft;
        }
        else {
            expHas = Math.max(0, mode.modify(expHas, amount));
        }
        target.setExp(0F);
        target.setTotalExperience(0);
        target.setLevel(0);
        target.giveExp(expHas);
        if (!target.isOnline()) target.saveData();

        LangMessage message = switch (mode) {
            case SET -> plugin.getMessage(Lang.COMMAND_EXP_SET_TARGET);
            case REMOVE -> plugin.getMessage(Lang.COMMAND_EXP_REMOVE_TARGET);
            case ADD -> plugin.getMessage(Lang.COMMAND_EXP_ADD_TARGET);
        };

        LangMessage notify = switch (mode) {
            case ADD -> plugin.getMessage(Lang.COMMAND_EXP_ADD_NOTIFY);
            case SET -> plugin.getMessage(Lang.COMMAND_EXP_SET_NOTIFY);
            case REMOVE -> plugin.getMessage(Lang.COMMAND_EXP_REMOVE_NOTIFY);
        };

        if (target != sender) {
            message
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(target.getTotalExperience()))
                .replace(Placeholders.GENERIC_LEVEL, NumberUtil.format(target.getLevel()))
                .replace(Placeholders.Player.replacer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            notify
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.GENERIC_TOTAL, NumberUtil.format(target.getTotalExperience()))
                .replace(Placeholders.GENERIC_LEVEL, NumberUtil.format(target.getLevel()))
                .replace(Placeholders.Player.replacer(target))
                .send(target);
        }
    }
}
