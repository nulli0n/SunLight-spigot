package su.nightexpress.sunlight.module.homes.command.admin;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.config.HomesLang;

import java.util.Map;

public class HomesAdminCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "homesadmin";

    public HomesAdminCommand(@NotNull HomesModule module, @NotNull String[] aliases) {
        super(module.plugin(), aliases);
        this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
        this.addChildren(new HomesAdminDeleteCommand(module));
        this.addChildren(new HomesAdminCreateCommand(module));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(HomesLang.COMMAND_HOMES_ADMIN_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {

    }
}
