package su.nightexpress.sunlight.command.item;

import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ItemCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "item";

    static final CommandFlag<String> FLAG_NAME = CommandFlag.stringFlag("name");
    static final CommandFlag<String> FLAG_LORE = CommandFlag.stringFlag("lore");
    static final CommandFlag<String> FLAG_ENCHANTS = CommandFlag.stringFlag("ench");
    static final CommandFlag<Integer> FLAG_MODEL = CommandFlag.intFlag("model");

    public ItemCommand(@NotNull SunLight plugin, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.COMMAND_ITEM);
        this.setDescription(plugin.getMessage(Lang.COMMAND_ITEM_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_ITEM_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(plugin));
        this.addChildren(new ItemAmountCommand(plugin, cfg));
        this.addChildren(new ItemDamageCommand(plugin));
        this.addChildren(new ItemEnchantCommand(plugin));
        this.addChildren(new ItemFlagCommand(plugin));
        this.addChildren(new ItemGetCommand(plugin));
        this.addChildren(new ItemGiveCommand(plugin));
        this.addChildren(new ItemLoreCommand(plugin));
        this.addChildren(new ItemModelCommand(plugin));
        this.addChildren(new ItemNameCommand(plugin));
        this.addChildren(new ItemTakeCommand(plugin));
        this.addChildren(new ItemSpawnCommand(plugin));
        this.addChildren(new ItemPotionCommand(plugin));
        this.addChildren(new ItemUnbreakableCommand(plugin));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }

    @NotNull
    static List<String> parseFlagLore(@NotNull String flagLore) {
        return Stream.of(flagLore.split("\\|")).toList();
    }

    @NotNull
    static Map<Enchantment, Integer> parseFlagEnchants(@NotNull String flagEnchants) {
        Map<Enchantment, Integer> checkEnchants = new HashMap<>();
        for (String enchantRaw : flagEnchants.split(" ")) {
            if (enchantRaw.isEmpty()) continue;

            String[] enchantSplit = enchantRaw.split(":");
            if (enchantSplit.length == 0) continue;

            Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(enchantSplit[0]));
            if (enchantment == null) continue;

            int level = enchantSplit.length >= 2 ? StringUtil.getInteger(enchantSplit[1], -1) : 0;
            if (level <= 0) continue;

            checkEnchants.put(enchantment, level);
        }
        return checkEnchants;
    }
}
