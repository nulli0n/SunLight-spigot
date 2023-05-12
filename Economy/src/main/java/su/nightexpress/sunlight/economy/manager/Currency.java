package su.nightexpress.sunlight.economy.manager;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.NumberUtil;

public class Currency {
	
    private final String nameSingular;
    private final String namePlural;
    private final String nameFormat;
    private final double defaultBalance;
    
    public Currency(
    		@NotNull String nameSingular,
    	    @NotNull String namePlural,
    	    @NotNull String nameFormat,
    		double defaultBalance) {
    	
        this.nameSingular = Colorizer.apply(nameSingular);
        this.namePlural = Colorizer.apply(namePlural);
        this.nameFormat = Colorizer.apply(nameFormat);
        this.defaultBalance = defaultBalance;
    }
    
    public double getDefaultBalance() {
        return this.defaultBalance;
    }
    
    @NotNull
    public String format(double amount) {
        String name = amount > 1D ? this.getNamePlural() : this.getNameSingular();
        return this.nameFormat.replace("%name%", name).replace("%amount%", NumberUtil.format(amount));
    }
    
    @NotNull
    public String getNameSingular() {
    	return this.nameSingular;
    }
    
    @NotNull
    public String getNamePlural() {
    	return this.namePlural;
    }
}
