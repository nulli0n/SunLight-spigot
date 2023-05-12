package su.nightexpress.sunlight.module.chat.command.spy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.StringUtil;
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

public class ModeSubCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "mode";

    public ModeSubCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, ChatPerms.COMMAND_SPY_MODE);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(ChatLang.COMMAND_SPY_MODE_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(ChatLang.COMMAND_SPY_MODE_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.getEnumsList(ChatSpyType.class);
        }
        if (arg == 2) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 3) {
            return Arrays.asList("0", "1");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args,
                             @NotNull Map<String, String> flags) {

        if (args.length < 2) {
            this.printUsage(sender);
            return;
        }

        ChatSpyType spyType = CollectionsUtil.getEnum(args[1], ChatSpyType.class);
        if (spyType == null) {
            this.printUsage(sender);
            return;
        }

        String targetName = sender.getName();
        boolean isReversed = args.length == 2;
        boolean stateNew = false;

        if (args.length >= 3) {
            if (args.length >= 4) {
                targetName = args[2];
                stateNew = StringUtil.parseCustomBoolean(args[3]);
            }
            else {
                if (StringUtil.isCustomBoolean(args[2])) {
                    stateNew = StringUtil.parseCustomBoolean(args[2]);
                }
                else {
                    isReversed = true;
                    targetName = args[2];
                }
            }
        }

        if (!sender.hasPermission(ChatPerms.COMMAND_SPY_MODE_OTHERS) && !targetName.equalsIgnoreCase(sender.getName())) {
            this.errorPermission(sender);
            return;
        }

        SunUser userTarget = plugin.getUserManager().getUserData(targetName);
        if (userTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        Player targetPlayer = userTarget.getPlayer();

        boolean stateHas = userTarget.getSettings().get(spyType.getSettingChat());
        if (isReversed) stateNew = !stateHas;
        userTarget.getSettings().set(spyType.getSettingChat(), stateNew);

        if (!sender.equals(targetPlayer)) {
            plugin.getMessage(ChatLang.COMMAND_SPY_MODE_DONE_OTHERS)
                .replace(Placeholders.replacer(targetPlayer, userTarget))
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(spyType))
                .replace(Placeholders.GENERIC_STATE, plugin.getMessage(Lang.getEnabled(stateNew)).getLocalized())
                .send(sender);
        }
        if (targetPlayer != null) {
            plugin.getMessage(ChatLang.COMMAND_SPY_MODE_DONE_NOTIFY)
                .replace(Placeholders.Player.replacer(targetPlayer))
                .replace(Placeholders.GENERIC_TYPE, plugin.getLangManager().getEnum(spyType))
                .replace(Placeholders.GENERIC_STATE, plugin.getMessage(Lang.getEnabled(stateNew)).getLocalized())
                .send(targetPlayer);
        }
    }
}
