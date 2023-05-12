package su.nightexpress.sunlight.command.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ChangeCommand;
import su.nightexpress.sunlight.config.Lang;

import java.util.List;
import java.util.stream.IntStream;

public class FoodCommand extends ChangeCommand {

    public static final String NAME = "food";

    public FoodCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_FOOD, Perms.COMMAND_FOOD_OTHERS);
        this.setAllowDataLoad();
        this.setDescription(plugin.getMessage(Lang.COMMAND_FOOD_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_FOOD_USAGE));
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    protected List<String> getTab(@NotNull Player player) {
        return IntStream.range(0, 21).boxed().map(String::valueOf).toList();
    }

    @Override
    protected void run(@NotNull CommandSender sender, @NotNull CommandResult result, @NotNull Mode mode, @NotNull Player target, double amount) {
        int has = target.getFoodLevel();
        int set = mode.modify(has, (int) amount);

        target.setFoodLevel(set);
        if (!target.isOnline()) target.saveData();

        LangMessage message = switch (mode) {
            case SET -> plugin.getMessage(Lang.COMMAND_FOOD_SET_TARGET);
            case REMOVE -> plugin.getMessage(Lang.COMMAND_FOOD_REMOVE_TARGET);
            case ADD -> plugin.getMessage(Lang.COMMAND_FOOD_ADD_TARGET);
        };

        LangMessage notify = switch (mode) {
            case ADD -> plugin.getMessage(Lang.COMMAND_FOOD_ADD_NOTIFY);
            case SET -> plugin.getMessage(Lang.COMMAND_FOOD_SET_NOTIFY);
            case REMOVE -> plugin.getMessage(Lang.COMMAND_FOOD_REMOVE_NOTIFY);
        };

        int current = target.getFoodLevel();
        int max = 20;

        if (target != sender) {
            message
                .replace(Placeholders.GENERIC_CURRENT, String.valueOf(current))
                .replace(Placeholders.GENERIC_MAX, String.valueOf(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.Player.replacer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            notify
                .replace(Placeholders.GENERIC_CURRENT, String.valueOf(current))
                .replace(Placeholders.GENERIC_MAX, String.valueOf(max))
                .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(amount))
                .replace(Placeholders.Player.replacer(target))
                .send(target);
        }
    }
}
