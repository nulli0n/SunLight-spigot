package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.message.NexParser;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;
import su.nightexpress.sunlight.module.chat.config.ChatLang;

import java.util.List;

@Deprecated // TODO Make a part of the 'Roleplay' commands.
public class MeCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "me";

    private final String format;

    public MeCommand(@NotNull ChatModule chatModule, @NotNull JYML cfg, @NotNull String[] aliases) {
        super(chatModule.plugin(), aliases, ChatPerms.COMMAND_ME);

        this.format = JOption.create("Me.Format",
            "&e&o* &6&o" + Placeholders.PLAYER_DISPLAY_NAME + " &e&o" + Placeholders.GENERIC_MESSAGE,
            "Sets the format for action message.",
            "Use " + Placeholders.GENERIC_MESSAGE + " placeholder for the action text.",
            "You can use 'Player' placeholders: " + Placeholders.ENGINE_URL_PLACEHOLDERS,
            "JSON Formatting is allowed: " + Placeholders.ENGINE_URL_LANG_JSON
        ).read(cfg);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(ChatLang.COMMAND_ME_USAGE).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(ChatLang.COMMAND_ME_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        return super.getTab(player, arg, args);
    }

    @Override
    public void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() == 0) {
            this.printUsage(sender);
            return;
        }
        String text = NexParser.removeFrom(Colorizer.strip(String.join(" ", result.getArgs())).trim());
        String message = Placeholders.forSender(sender).apply(format.replace(Placeholders.GENERIC_MESSAGE, text));
        this.plugin.getServer().getOnlinePlayers().forEach(player -> PlayerUtil.sendRichMessage(player, message));
    }
}
