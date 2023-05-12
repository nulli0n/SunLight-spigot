package su.nightexpress.sunlight.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.command.list.ReloadSubCommand;
import su.nightexpress.sunlight.economy.config.EconomyConfig;
import su.nightexpress.sunlight.economy.config.EconomyLang;
import su.nightexpress.sunlight.economy.data.EconomyDataHandler;
import su.nightexpress.sunlight.economy.data.EconomyUser;
import su.nightexpress.sunlight.economy.data.EconomyUserManager;
import su.nightexpress.sunlight.economy.manager.EconomyManager;

import java.sql.SQLException;

public class SunLightEconomyPlugin extends NexPlugin<SunLightEconomyPlugin> implements UserDataHolder<SunLightEconomyPlugin, EconomyUser> {

	private EconomyManager  economyManager;
	private SunLightEconomy economy;

	private EconomyDataHandler dataHandler;
	private EconomyUserManager userManager;

	@Override
	@NotNull
	protected SunLightEconomyPlugin getSelf() {
		return this;
	}

	@Override
	public void enable() {
		this.economyManager = new EconomyManager(this);
		this.economyManager.setup();

		this.economy = new SunLightEconomy(this.economyManager);

		ServicesManager services = this.getServer().getServicesManager();
		services.register(Economy.class, this.economy, this, ServicePriority.High);
	}

	@Override
	public void disable() {
		if (this.economy != null) {
			ServicesManager services = this.getServer().getServicesManager();
			services.unregister(Economy.class, this.economy);
			this.economy = null;
		}
	}

	@Override
	public void loadConfig() {
		EconomyConfig.load(this);
	}

	@Override
	public void loadLang() {
		this.getLangManager().loadMissing(EconomyLang.class);
	}

	@Override
	public void registerHooks() {

	}

	@Override
	public void registerCommands(@NotNull GeneralCommand<SunLightEconomyPlugin> mainCommand) {
		mainCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
	}

	@Override
	public void registerPermissions() {
		this.registerPermissions(Perms.class);
	}

	@Override
	public boolean setupDataHandlers() {
		try {
			this.dataHandler = EconomyDataHandler.getInstance(this);
			this.dataHandler.setup();
		}
		catch (SQLException e) {
			return false;
		}

		this.userManager = new EconomyUserManager(this);
		this.userManager.setup();
		return true;
	}

	@Override
	@NotNull
	public EconomyDataHandler getData() {
		return this.dataHandler;
	}

	@Override
	@NotNull
	public EconomyUserManager getUserManager() {
		return this.userManager;
	}

	@NotNull
	public EconomyManager getEconomyManager() {
		return this.economyManager;
	}
}
