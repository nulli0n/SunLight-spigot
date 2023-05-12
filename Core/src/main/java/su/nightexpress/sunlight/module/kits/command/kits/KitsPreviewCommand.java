package su.nightexpress.sunlight.module.kits.command.kits;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.util.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.List;
import java.util.Map;

public class KitsPreviewCommand extends ModuleCommand<KitsModule> {

    public KitsPreviewCommand(KitsModule module) {
        super(module, new String[]{"preview"}, KitsPerms.COMMAND_KITS_PREVIEW);
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.plugin.getMessage(KitsLang.COMMAND_KITS_PREVIEW_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.plugin.getMessage(KitsLang.COMMAND_KITS_PREVIEW_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
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
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length < 2) {
            this.printUsage(sender);
            return;
        }

        String kitId = args[1];
        Kit kit = this.module.getKitById(kitId);
        if (kit == null) {
            this.plugin.getMessage(KitsLang.KIT_ERROR_INVALID_KIT).replace(Placeholders.KIT_ID, kitId).send(sender);
            return;
        }

        Player pTarget = plugin.getServer().getPlayer(args.length >= 3 && sender.hasPermission(KitsPerms.COMMAND_KITS_PREVIEW_OTHERS) ? args[2] : sender.getName());
        if (pTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        kit.getPreview().open(pTarget, 1);
    }
}
