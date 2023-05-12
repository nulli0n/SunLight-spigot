package su.nightexpress.sunlight.economy.config;

import su.nexmedia.engine.api.lang.LangKey;

public class EconomyLang {

    public static final LangKey Command_Balance_Usage = new LangKey("Command.Balance.Usage", "[player]");
    public static final LangKey Command_Balance_Done  = new LangKey("Command.Balance.Done", "&a%player%'s &7balance: &a%balance%");

    public static final LangKey Command_BalanceTop_Usage          = new LangKey("Command.BalanceTop.Usage", "[player]");
    public static final LangKey Command_BalanceTop_Header = new LangKey("Command.BalanceTop.Header", "{message: ~prefix: false;}&6&m               &6&l[&e&l Balance Top &6&l]&m               &7");
    public static final LangKey Command_BalanceTop_Format = new LangKey("Command.BalanceTop.Format", "{message: ~prefix: false;}&6%pos%. &e%player%: &c%money%");

    public static final LangKey Error_NoAccount = new LangKey("Error.NoAccount", "&cUser account does not exists!");

    public static final LangKey Command_Eco_Give_Usage = new LangKey("Command.Eco.Give.Usage", "<player> <amount>");
    public static final LangKey Command_Eco_Give_Desc  = new LangKey("Command.Eco.Give.Desc", "Adds specified amount of money to a player's balance.");
    public static final LangKey Command_Eco_Give_Done = new LangKey("Command.Eco.Give.Done", "&7Given &a%amount% &7to &a%player%&7.");

    public static final LangKey Command_Eco_Take_Usage        = new LangKey("Command.Eco.Take.Usage", "<player> <amount>");
    public static final LangKey Command_Eco_Take_Desc = new LangKey("Command.Eco.Take.Desc", "Takes specified amount of money from a player's balance.");
    public static final LangKey Command_Eco_Take_Done = new LangKey("Command.Eco.Take.Done", "&7Taken &a%amount% &7from &a%player%&7.");

    public static final LangKey Command_Eco_Set_Usage        = new LangKey("Command.Eco.Set.Usage", "<player> <amount>");
    public static final LangKey Command_Eco_Set_Desc = new LangKey("Command.Eco.Set.Desc", "Sets player's balance to specified amount.");
    public static final LangKey Command_Eco_Set_Done = new LangKey("Command.Eco.Set.Done", "&7Set &a%player% &7balance on &a%amount%&7.");

    public static final LangKey Command_Pay_Usage           = new LangKey("Command.Pay.Usage", "<player> <amount>");
    public static final LangKey Command_Pay_Desc             = new LangKey("Command.Pay.Desc", "Send money to a player.");
    public static final LangKey Command_Pay_Done_In              = new LangKey("Command.Pay.Done.In", "&7Received &a%amount% &7from &a%player%&7.");
    public static final LangKey Command_Pay_Done_Out                = new LangKey("Command.Pay.Done.Out", "&7Sent &a%amount% &7to &a%player%&7.");
    public static final LangKey Command_Pay_Error_InsufficientFunds = new LangKey("Command.Pay.Error.InsufficientFunds", "&cYou don't have enough money!");

}
