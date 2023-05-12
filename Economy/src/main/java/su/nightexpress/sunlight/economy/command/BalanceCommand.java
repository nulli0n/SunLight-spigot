package su.nightexpress.sunlight.economy.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.config.EconomyConfig;
import su.nightexpress.sunlight.economy.config.EconomyLang;
import su.nightexpress.sunlight.economy.data.EconomyUser;
import su.nightexpress.sunlight.economy.manager.Currency;
import su.nightexpress.sunlight.economy.manager.EconomyManager;
import su.nightexpress.sunlight.economy.manager.EconomyPerms;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BalanceCommand extends GeneralCommand<SunLightEconomyPlugin> {

	public BalanceCommand(@NotNull EconomyManager economyManager) {
		super(economyManager.plugin(), new String[] {"balance", "bal", "money", "cash"}, EconomyPerms.CMD_BALANCE);
	}

	@Override
	@NotNull
	public String getUsage() {
		return this.plugin.getMessage(EconomyLang.Command_Balance_Usage).getLocalized();
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
	       	return CollectionsUtil.playerNames(player);
	    }
		return super.getTab(player, i, args);
	}
	
	@Override
	public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length == 0 && !(sender instanceof Player)) {
        	this.errorSender(sender);
			return;
        }
        
        String target = sender.getName();
        if (args.length > 0) {
        	if (!sender.hasPermission(EconomyPerms.CMD_BALANCE_OTHERS)) {
        		this.errorPermission(sender);
        		return;
        	}
            target = args[0];
        }
        
        EconomyUser user = plugin.getUserManager().getUserData(target);
        if (user == null) {
			this.plugin.getMessage(EconomyLang.Error_NoAccount).send(sender);
        	return;
        }
        
        UUID uuid = user.getId();
        Currency currency = EconomyConfig.CURRENCY;
        double balance = this.plugin.getEconomyManager().getBalance(uuid);

		this.plugin.getMessage(EconomyLang.Command_Balance_Done)
	        .replace("%player%", target)
	        .replace("%balance%", currency.format(balance))
	        .send(sender);
	}
}
