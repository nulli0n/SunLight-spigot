package su.nightexpress.sunlight.module.kits.command.kits.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.List;

public class GetSubCommand extends ModuleCommand<KitsModule> {

    public GetSubCommand(@NotNull KitsModule kitsModule) {
        super(kitsModule, new String[]{"get"}, KitsPerms.COMMAND_KITS_GET);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_GET_DESC));
        this.setUsage(plugin.getMessage(KitsLang.COMMAND_KITS_GET_USAGE));
        this.setPlayerOnly(true);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getKitIds(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        String kitId = result.getArg(1);
        Kit kit = this.module.getKitById(kitId);
        if (kit == null) {
            this.plugin.getMessage(KitsLang.KIT_ERROR_INVALID).replace(Placeholders.KIT_ID, kitId).send(sender);
            return;
        }

        Player player = (Player) sender;
        kit.give(player, false);
    }
}
