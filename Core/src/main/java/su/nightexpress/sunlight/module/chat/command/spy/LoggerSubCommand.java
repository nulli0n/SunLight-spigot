package su.nightexpress.sunlight.module.chat.command.spy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.module.chat.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.util.ChatSpyType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LoggerSubCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "logger";

    private static final String ADD = "add";
    private static final String REMOVE = "remove";

    public LoggerSubCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, ChatPerms.COMMAND_SPY_MODE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(ChatLang.COMMAND_SPY_LOGGER_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(ChatLang.COMMAND_SPY_LOGGER_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return Arrays.asList(ADD, REMOVE);
        }
        if (arg == 2) {
            return CollectionsUtil.getEnumsList(ChatSpyType.class);
        }
        if (arg == 3) {
            return CollectionsUtil.playerNames(player);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args,
                             @NotNull Map<String, String> flags) {

        if (args.length < 4) {
            this.printUsage(sender);
            return;
        }

        boolean isAdd = args[1].equalsIgnoreCase(ADD);
        boolean isRemove = args[1].equals(REMOVE);
        if (!isAdd && !isRemove) {
            this.printUsage(sender);
            return;
        }

        ChatSpyType spyType = CollectionsUtil.getEnum(args[2], ChatSpyType.class);
        if (spyType == null) {
            this.printUsage(sender);
            return;
        }

        SunUser userTarget = plugin.getUserManager().getUserData(args[3]);
        if (userTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        userTarget.getSettings().set(spyType.getSettingLog(), isAdd);

        Player targetPlayer = userTarget.getPlayer();
        plugin.getMessage(ChatLang.COMMAND_SPY_LOGGER_DONE)
            .replace(Placeholders.replacer(targetPlayer, userTarget))
            .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(spyType))
            .replace(Placeholders.GENERIC_STATE, plugin.getMessage(Lang.getEnabled(isAdd)).getLocalized())
            .send(sender);
    }
}
