package su.nightexpress.sunlight.module.kits.command.kits.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.List;

public class PreviewSubCommand extends ModuleCommand<KitsModule> {

    public PreviewSubCommand(KitsModule module) {
        super(module, new String[]{"preview"}, KitsPerms.COMMAND_KITS_PREVIEW);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_PREVIEW_DESC));
        this.setUsage(plugin.getMessage(KitsLang.COMMAND_KITS_PREVIEW_USAGE));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getKitIds();
        }
        if (arg == 2 && player.hasPermission(KitsPerms.COMMAND_KITS_PREVIEW_OTHERS)) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }
        if (result.length() >= 3 && !sender.hasPermission(KitsPerms.COMMAND_KITS_PREVIEW_OTHERS)) {
            this.errorPermission(sender);
            return;
        }

        String kitId = result.getArg(1);
        Kit kit = this.module.getKitById(kitId);
        if (kit == null) {
            this.plugin.getMessage(KitsLang.KIT_ERROR_INVALID).replace(Placeholders.KIT_ID, kitId).send(sender);
            return;
        }

        Player target = plugin.getServer().getPlayer(result.getArg(2, sender.getName()));
        if (target == null) {
            this.errorPlayer(sender);
            return;
        }

        kit.getPreview().open(target, 1);

        if (sender != target) {
            this.plugin.getMessage(KitsLang.COMMAND_KITS_PREVIEW_OTHERS)
                .replace(kit.replacePlaceholders())
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }
    }
}
