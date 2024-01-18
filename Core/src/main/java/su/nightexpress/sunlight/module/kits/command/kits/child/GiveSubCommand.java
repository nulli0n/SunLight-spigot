package su.nightexpress.sunlight.module.kits.command.kits.child;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.module.ModuleCommand;
import su.nightexpress.sunlight.module.kits.Kit;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;
import su.nightexpress.sunlight.module.kits.config.KitsPerms;
import su.nightexpress.sunlight.module.kits.util.Placeholders;

import java.util.List;

public class GiveSubCommand extends ModuleCommand<KitsModule> {

    public GiveSubCommand(@NotNull KitsModule module) {
        super(module, new String[]{"give"}, KitsPerms.COMMAND_KITS_GIVE);
        this.setDescription(plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_DESC));
        this.setUsage(plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_USAGE));

        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return this.module.getKitIds();
        }
        if (arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Kit kit = this.module.getKitById(result.getArg(1));
        if (kit == null) {
            plugin.getMessage(KitsLang.KIT_ERROR_INVALID).send(sender);
            return;
        }

        Player player = plugin.getServer().getPlayer(result.getArg(2));
        if (player == null) {
            this.errorPlayer(sender);
            return;
        }

        kit.give(player, true);

        if (sender != player) {
            plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_DONE)
                .replace(kit.replacePlaceholders())
                .replace(Placeholders.forPlayer(player))
                .send(sender);
        }

        if (!result.hasFlag(CommandFlags.SILENT)) {
            plugin.getMessage(KitsLang.COMMAND_KITS_GIVE_NOTIFY)
                .replace(kit.replacePlaceholders())
                .replace(Placeholders.forSender(sender))
                .send(player);
        }
    }
}
