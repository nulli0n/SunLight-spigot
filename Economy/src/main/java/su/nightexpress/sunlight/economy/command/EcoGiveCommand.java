package su.nightexpress.sunlight.economy.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.config.EconomyConfig;
import su.nightexpress.sunlight.economy.config.EconomyLang;
import su.nightexpress.sunlight.economy.data.EconomyUser;
import su.nightexpress.sunlight.economy.manager.EconomyManager;
import su.nightexpress.sunlight.economy.manager.EconomyPerms;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EcoGiveCommand extends AbstractCommand<SunLightEconomyPlugin> {

	private final EconomyManager economyManager;
	
	public EcoGiveCommand(@NotNull EconomyManager economyManager) {
		super(economyManager.plugin(), new String[] {"give"}, EconomyPerms.CMD_ECO_GIVE);
		this.economyManager = economyManager;
	}

	@Override
	@NotNull
	public String getUsage() {
		return this.plugin.getMessage(EconomyLang.Command_Eco_Give_Usage).getLocalized();
	}

	@Override
	@NotNull
	public String getDescription() {
		return this.plugin.getMessage(EconomyLang.Command_Eco_Give_Desc).getLocalized();
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
		if (i == 2) {
			return Arrays.asList("<amount>");
		}
		return super.getTab(player, i, args);
	}

	@Override
	public void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length != 3) {
        	this.printUsage(sender);
            return;
        }
        
        double amount = StringUtil.getDouble(args[2], -1D);
        if (amount <= 0D) {
           	return;
        }
        
        String target = args[1];
        EconomyUser user = plugin.getUserManager().getUserData(target);
        if (user == null) {
			this.plugin.getMessage(EconomyLang.Error_NoAccount).send(sender);
        	return;
        }
        
        UUID uuid = user.getId();
        this.economyManager.setBalance(uuid, this.economyManager.getBalance(uuid) + amount);
		this.plugin.getMessage(EconomyLang.Command_Eco_Give_Done)
            .replace("%player%", target)
            .replace("%amount%", EconomyConfig.CURRENCY.format(amount))
            .send(sender);
	}
}
