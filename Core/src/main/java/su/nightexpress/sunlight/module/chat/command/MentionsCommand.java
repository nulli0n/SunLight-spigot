package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.CommandFlags;
import su.nightexpress.sunlight.command.api.ToggleCommand;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.data.impl.settings.DefaultSettings;
import su.nightexpress.sunlight.data.impl.settings.UserSetting;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;

public class MentionsCommand extends ToggleCommand {

    public static final String NAME = "mentions";

    public MentionsCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, ChatPerms.COMMAND_MENTIONS, ChatPerms.COMMAND_MENTIONS_OTHERS);
        this.setDescription(plugin.getMessage(ChatLang.COMMAND_MENTIONS_DESC));
        this.setUsage(plugin.getMessage(ChatLang.COMMAND_MENTIONS_USAGE));
        this.setAllowDataLoad();
    }

    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player target = this.getCommandTarget(sender, result);
        if (target == null) {
            return;
        }

        ToggleCommand.Mode mode = this.getMode(sender, result);
        UserSetting<Boolean> setting = DefaultSettings.MENTIONS;
        SunUser user = this.plugin.getUserManager().getUserData(target);
        user.getSettings().set(setting, mode.apply(user.getSettings().get(setting)));
        this.plugin.getUserManager().saveUser(user);

        if (sender != target) {
            this.plugin.getMessage(ChatLang.COMMAND_MENTIONS_TOGGLE_TARGET)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(user.getSettings().get(setting)))
                .replace(Placeholders.forPlayer(target))
                .send(sender);
        }

        if (!result.hasFlag(CommandFlags.SILENT)) {
            this.plugin.getMessage(ChatLang.COMMAND_MENTIONS_TOGGLE_NOTIFY)
                .replace(Placeholders.GENERIC_STATE, Lang.getEnable(user.getSettings().get(setting)))
                .send(target);
        }
    }
}

