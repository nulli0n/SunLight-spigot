package su.nightexpress.sunlight.command.list;

import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.EntityUtil;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.TargetCommand;
import su.nightexpress.sunlight.config.Lang;

public class HealCommand extends TargetCommand {

    public static final String NAME = "heal";

    private static final CommandFlag<Boolean> FLAG_EFFECTS = CommandFlag.booleanFlag("eff");

    public HealCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_HEAL, Perms.COMMAND_HEAL_OTHERS, 0);
        this.setAllowDataLoad();
        this.setUsage(plugin.getMessage(Lang.COMMAND_HEAL_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_HEAL_DESC));
        this.addFlag(CommandFlags.SILENT, FLAG_EFFECTS);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) return;

        double maxHealth = EntityUtil.getAttribute(target, Attribute.GENERIC_MAX_HEALTH);
        target.setHealth(maxHealth);
        if (!target.isOnline()) target.saveData();

        if (result.hasFlag(FLAG_EFFECTS)) {
            for (PotionEffect potionEffect : target.getActivePotionEffects()) {
                target.removePotionEffect(potionEffect.getType());
            }
        }

        if (sender != target) {
            plugin.getMessage(Lang.COMMAND_HEAL_TARGET)
                .replace(Placeholders.Player.replacer(target))
                .send(sender);
        }
        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(Lang.COMMAND_HEAL_NOTIFY).send(target);
        }
    }
}
