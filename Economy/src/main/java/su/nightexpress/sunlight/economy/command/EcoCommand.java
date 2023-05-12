package su.nightexpress.sunlight.economy.command;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.manager.EconomyManager;
import su.nightexpress.sunlight.economy.manager.EconomyPerms;

import java.util.Map;

public class EcoCommand extends GeneralCommand<SunLightEconomyPlugin> {

	public EcoCommand(@NotNull EconomyManager economyManager) {
		super(economyManager.plugin(), new String[] {"economy", "eco"}, EconomyPerms.CMD_ECO);
		
		this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
		this.addChildren(new EcoGiveCommand(economyManager));
		this.addChildren(new EcoTakeCommand(economyManager));
		this.addChildren(new EcoSetCommand(economyManager));
	}

	@Override
	@NotNull
	public String getUsage() {
		return "";
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
	public void onExecute(@NotNull CommandSender sender, @NotNull String label, String[] args, @NotNull Map<String, String> flags) {
		
	}
}
