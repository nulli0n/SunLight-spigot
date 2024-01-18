package su.nightexpress.sunlight.module.kits.command.kits;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.command.kits.child.*;
import su.nightexpress.sunlight.module.kits.config.KitsLang;

public class KitsCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "kits";

    public KitsCommand(@NotNull KitsModule module, @NotNull String[] aliases) {
        super(module.plugin(), aliases);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_DESC));
        this.setUsage(plugin.getMessage(KitsLang.COMMAND_KITS_USAGE));

        this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
        this.addChildren(new EditorSubCommand(module));
        this.addChildren(new PreviewSubCommand(module));
        this.addChildren(new GiveSubCommand(module));
        this.addChildren(new GetSubCommand(module));
        this.addChildren(new ListSubCommand(module));
        this.addChildren(new ResetCooldownSubCommand(module));
        this.addChildren(new SetCooldownSubCommand(module));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {

    }
}
