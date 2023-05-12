package su.nightexpress.sunlight.economy.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.lang.EngineLang;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.config.EconomyConfig;
import su.nightexpress.sunlight.economy.config.EconomyLang;
import su.nightexpress.sunlight.economy.manager.Currency;
import su.nightexpress.sunlight.economy.manager.EconomyManager;
import su.nightexpress.sunlight.economy.manager.EconomyPerms;

import java.util.List;
import java.util.Map;

public class PayCommand extends GeneralCommand<SunLightEconomyPlugin> {

	private final EconomyManager economyManager;

	public PayCommand(@NotNull EconomyManager economyManager) {
		super(economyManager.plugin(), new String[] {"pay"}, EconomyPerms.CMD_PAY);
		this.economyManager = economyManager;
	}
	
	@Override
	@NotNull
	public String getUsage() {
		return this.plugin.getMessage(EconomyLang.Command_Pay_Usage).getLocalized();
	}
	
	@Override
	@NotNull
	public String getDescription() {
		return this.plugin.getMessage(EconomyLang.Command_Pay_Usage).getLocalized();
	}
	
	@Override
	public boolean isPlayerOnly() {
		return true;
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
        if (args.length != 2) {
			this.printUsage(sender);
			return;
        }
        
        double amount = StringUtil.getDouble(args[1], 0);
        if (amount <= 0) return;
        
        Currency currency = EconomyConfig.CURRENCY;
        Player pFrom = (Player) sender;
            
        if (!this.economyManager.hasAccount(pFrom)) {
			this.plugin.getMessage(EconomyLang.Error_NoAccount).send(sender);
           	return;
        }
            
        Player pTarget = plugin.getServer().getPlayer(args[0]);
        if (pTarget == null) {
           	this.errorPlayer(sender);
           	return;
        }
            
        if (!this.economyManager.hasAccount(pTarget)) {
			this.plugin.getMessage(EconomyLang.Error_NoAccount).send(sender);
           	return;
        }
            
        if (pFrom.equals(pTarget)) {
           	plugin.getMessage(EngineLang.ERROR_COMMAND_SELF).send(sender);
           	return;
        }
        
        double moneyFrom = this.economyManager.getBalance(pFrom);
        if (moneyFrom < amount) {
			this.plugin.getMessage(EconomyLang.Command_Pay_Error_InsufficientFunds).send(sender);
        	return;
        }
        
        this.economyManager.setBalance(pFrom, moneyFrom - amount);
        this.economyManager.setBalance(pTarget, economyManager.getBalance(pTarget) + amount);

		this.plugin.getMessage(EconomyLang.Command_Pay_Done_In)
	        .replace("%amount%", currency.format(amount))
	        .replace("%player%", pFrom.getName())
	        .send(pTarget);

		this.plugin.getMessage(EconomyLang.Command_Pay_Done_Out)
	        .replace("%amount%", currency.format(amount))
	        .replace("%player%", pTarget.getName())
	        .send(sender);
	}
}
