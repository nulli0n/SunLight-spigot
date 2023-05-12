package su.nightexpress.sunlight.module.kits.command.kits;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.command.list.HelpSubCommand;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;

import java.util.Map;

public class KitsCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "kits";

    public KitsCommand(@NotNull KitsModule kitsModule, @NotNull String[] aliases) {
        super(kitsModule.plugin(), aliases);
        this.addDefaultCommand(new HelpSubCommand<>(this.plugin));
        this.addChildren(new KitsEditorCommand(kitsModule));
        this.addChildren(new KitsPreviewCommand(kitsModule));
        this.addChildren(new KitsGiveCommand(kitsModule));
        this.addChildren(new KitsGetCommand(kitsModule));
        this.addChildren(new KitsListCommand(kitsModule));
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(KitsLang.COMMAND_KITS_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(KitsLang.COMMAND_KITS_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {

    }
}
