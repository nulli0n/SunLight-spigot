package su.nightexpress.sunlight.economy.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.config.EconomyConfig;
import su.nightexpress.sunlight.economy.config.EconomyLang;
import su.nightexpress.sunlight.economy.manager.Currency;
import su.nightexpress.sunlight.economy.manager.EconomyManager;
import su.nightexpress.sunlight.economy.manager.EconomyPerms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BalanceTopCommand extends GeneralCommand<SunLightEconomyPlugin> {

	public BalanceTopCommand(@NotNull EconomyManager economyManager) {
		super(economyManager.plugin(), new String[] {"balancetop", "baltop", "moneytop"}, EconomyPerms.CMD_BALTOP);
	}

	@Override
	@NotNull
	public String getUsage() {
		return this.plugin.getMessage(EconomyLang.Command_BalanceTop_Usage).getLocalized();
	}
	
	@Override
	@NotNull
	public String getDescription() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public boolean isPlayerOnly() {
		return false;
	}

	@Override
	@NotNull
	public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
		if (i == 1) {
	       	return Arrays.asList("1", "2", "<page>");
	    }
		return super.getTab(player, i, args);
	}
	
	@Override
	public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
		int page = 0;
		if (args.length == 1) {
			page = StringUtil.getInteger(args[0], 1) - 1;
		}
		
		List<List<Map.Entry<String, Double>>> total = CollectionsUtil.split(plugin.getEconomyManager().getBalanceTop(), 10);
		int pages = total.size();
		
		if (page >= pages) page = pages - 1;
		if (page < 0) page = 0;
		
		List<Map.Entry<String, Double>> list = new ArrayList<>();
		if (!total.isEmpty()) {
			list = total.get(page);
		}
		
		Currency currency = EconomyConfig.CURRENCY;
		int position = 1 + 10 * page;

		this.plugin.getMessage(EconomyLang.Command_BalanceTop_Header).send(sender);
		for (Map.Entry<String, Double> entry : list) {
			this.plugin.getMessage(EconomyLang.Command_BalanceTop_Format)
					.replace("%pos%", String.valueOf(position))
					.replace("%money%", currency.format(entry.getValue()))
					.replace("%player%", entry.getKey())
					.send(sender);
			position++;
		}
	}
}
