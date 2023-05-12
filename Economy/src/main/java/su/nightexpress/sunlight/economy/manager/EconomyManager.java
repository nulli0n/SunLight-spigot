package su.nightexpress.sunlight.economy.manager;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.api.task.AbstractTask;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.economy.command.EcoCommand;
import su.nightexpress.sunlight.economy.command.PayCommand;
import su.nightexpress.sunlight.economy.config.EconomyConfig;
import su.nightexpress.sunlight.economy.data.EconomyUser;
import su.nightexpress.sunlight.economy.SunLightEconomyPlugin;
import su.nightexpress.sunlight.economy.command.BalanceCommand;
import su.nightexpress.sunlight.economy.command.BalanceTopCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EconomyManager extends AbstractManager<SunLightEconomyPlugin> {

	private List<Map.Entry<String, Double>> balanceTop;
	private TopTask topTask;

	public EconomyManager(@NotNull SunLightEconomyPlugin plugin) {
		super(plugin);
	}

	@Override
	public void onLoad() {
		this.balanceTop = new ArrayList<>();
		
		this.plugin.getCommandManager().registerCommand(new EcoCommand(this));
		this.plugin.getCommandManager().registerCommand(new BalanceCommand(this));
		this.plugin.getCommandManager().registerCommand(new BalanceTopCommand(this));
		this.plugin.getCommandManager().registerCommand(new PayCommand(this));

		this.topTask = new TopTask(this.plugin, EconomyConfig.BALANCE_TOP_UPDATE_INTERVAL);
		this.topTask.start();
	}

	@Override
	public void onShutdown() {
		if (this.topTask != null) {
			this.topTask.stop();
			this.topTask = null;
		}
		if (this.balanceTop != null) {
			this.balanceTop.clear();
			this.balanceTop = null;
		}
	}

	public void setBalanceTop(@NotNull Map<String, Double> map) {
		this.balanceTop.clear();
		this.balanceTop.addAll(map.entrySet());
	}
	
	@NotNull
	public List<Map.Entry<String, Double>> getBalanceTop() {
		return this.balanceTop;
	}
	
	@Deprecated
    public boolean withdraw(String name, double amount) {
        if (this.getBalance(name) >= amount) {
            this.setBalance(name, this.getBalance(name) - amount);
            return true;
        }
        return false;
    }
	
    public boolean withdraw(@NotNull OfflinePlayer player, double amount) {
        return this.withdraw(player.getUniqueId(), amount);
    }
    
    public boolean withdraw(@NotNull UUID uuid, double amount) {
        if (this.getBalance(uuid) >= amount) {
            this.setBalance(uuid, this.getBalance(uuid) - amount);
            return true;
        }
        return false;
    }
    
    @Deprecated
    public boolean deposit(String name, double amount) {
        this.setBalance(name, this.getBalance(name) + amount);
        return true;
    }
    
    public boolean deposit(@NotNull OfflinePlayer player, double amount) {
        return this.deposit(player.getUniqueId(), amount);
    }
    
    public boolean deposit(@NotNull UUID uuid, double amount) {
        this.setBalance(uuid, this.getBalance(uuid) + amount);
        return true;
    }
    
    @Deprecated
    public void setBalance(String name, double amount) {
    	EconomyUser user = plugin.getUserManager().getUserData(name);
    	if (user == null) return;
    	
        this.setBalance(user, Math.max(0, amount));
    }
    
    public void setBalance(@NotNull OfflinePlayer player, double amount) {
    	this.setBalance(player.getUniqueId(), amount);
    }
    
    public void setBalance(@NotNull UUID uuid, double amount) {
    	EconomyUser user = plugin.getUserManager().getUserData(uuid);
    	if (user == null) return;

        this.setBalance(user, Math.max(0, amount));
    }

    public void setBalance(@NotNull EconomyUser user, double amount) {
		user.setBalance(amount);
	}
    
    @Deprecated
    public double getBalance(String name) {
    	EconomyUser user = plugin.getUserManager().getUserData(name);
		return user == null ? 0 : this.getBalance(user);
    }
    
    public double getBalance(@NotNull OfflinePlayer player) {
    	return this.getBalance(player.getUniqueId());
    }
    
    public double getBalance(@NotNull UUID uuid) {
    	EconomyUser user = plugin.getUserManager().getUserData(uuid);
        return user == null ? 0 : this.getBalance(user);
    }

    public double getBalance(@NotNull EconomyUser user) {
		return user.getBalance();
	}
    
    @Deprecated
    public boolean hasEnough(String name, double amount) {
    	EconomyUser user = plugin.getUserManager().getUserData(name);
    	if (user == null) return false;
    	
        return amount < this.getBalance(user);
    }
    
    public boolean hasEnough(@NotNull OfflinePlayer player, double amount) {
    	return this.hasEnough(player.getUniqueId(), amount);
    }
    
    public boolean hasEnough(@NotNull UUID uuid, double amount) {
    	EconomyUser user = plugin.getUserManager().getUserData(uuid.toString());
    	if (user == null) return false;
    	
        return amount < this.getBalance(user);
    }

    @Deprecated
    public boolean hasAccount(String name) {
    	return plugin.getData().isUserExists(name);
    }
    
    public boolean hasAccount(@NotNull OfflinePlayer player) {
    	return this.hasAccount(player.getUniqueId());
    }
    
    public boolean hasAccount(@NotNull UUID uuid) {
    	return plugin.getData().isUserExists(uuid);
    }
    
    class TopTask extends AbstractTask<SunLightEconomyPlugin> {
    	
    	TopTask(@NotNull SunLightEconomyPlugin plugin, int sec) {
    		super(plugin, sec, true);
    	}
    	
    	@Override
		public void action() {
    		plugin.info("Updating balance top...");
    		long ms = System.currentTimeMillis();
    		
    		Map<String, Double> map = plugin.getData().getUserBalance();
    		map = CollectionsUtil.sortDescent(map);
    		setBalanceTop(map);
    		
    		ms = System.currentTimeMillis() - ms;
    		plugin.info("Balance top updated in " + ms + " ms!");
    	}
    }
}
