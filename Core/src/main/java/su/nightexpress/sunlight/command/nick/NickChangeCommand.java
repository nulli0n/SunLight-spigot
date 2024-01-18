package su.nightexpress.sunlight.command.nick;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.config.JOption;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.regex.RegexUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NickChangeCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "change";

    private final int maxLength;
    private final int minLength;
    private final Set<String> bannedWords;
    private final Pattern regex;

    public NickChangeCommand(@NotNull SunLight plugin, @NotNull JYML cfg) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_NICK_CHANGE);
        this.setDescription(plugin.getMessage(Lang.COMMAND_NICK_CHANGE_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_NICK_CHANGE_USAGE));
        this.setPlayerOnly(true);

        this.minLength = JOption.create("Nick.Min_Length", 3,
            "Sets minimal nick length for the '/" + NickCommand.NAME + " " + NAME + "command.'").read(cfg);
        this.maxLength = JOption.create("Nick.Max_Length", 20,
            "Sets maximal nick length for the '/" + NickCommand.NAME + " " + NAME + "command.'").read(cfg);
        this.bannedWords = JOption.create("Nick.Banned_Words", Set.of("admin", "ass", "dick", "nigga"),
            "A list of words, that can not be used in custom nicks for the '/" + NickCommand.NAME + " " + NAME + "command.'"
        ).mapReader(set -> set.stream().map(String::toLowerCase).collect(Collectors.toSet())).read(cfg);
        this.regex = Pattern.compile(JOption.create("Nick.Regex", "[a-zA-Zа-яА-Я0-9_\\s]*",
            "Sets regex pattern that custom nick must match to be used.",
            "By default it accepts all EN-RU characters, numbers, spaces and underscore.").read(cfg));
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        String nick = Stream.of(result.getArgs()).skip(1).collect(Collectors.joining(" "));

        String raw = Colorizer.restrip(nick);
        if (!player.hasPermission(Perms.COMMAND_NICK_BYPASS_LENGTH)) {
            if (raw.length() < this.minLength) {
                plugin.getMessage(Lang.COMMAND_NICK_CHANGE_ERROR_TOO_SHORT).replace(Placeholders.GENERIC_AMOUNT, this.minLength).send(sender);
                return;
            }
            if (raw.length() > this.maxLength) {
                plugin.getMessage(Lang.COMMAND_NICK_CHANGE_ERROR_TOO_LONG).replace(Placeholders.GENERIC_AMOUNT, this.maxLength).send(sender);
                return;
            }
        }
        if (!player.hasPermission(Perms.COMMAND_NICK_BYPASS_WORDS)) {
            if (this.bannedWords.stream().anyMatch(word -> raw.toLowerCase().contains(word))) {
                plugin.getMessage(Lang.COMMAND_NICK_CHANGE_ERROR_BAD_WORDS).send(sender);
                return;
            }
            if (this.plugin.getServer().getPlayer(raw) != null) {
                plugin.getMessage(Lang.COMMAND_NICK_CHANGE_ERROR_BAD_WORDS).send(sender);
                return;
            }
        }
        if (!player.hasPermission(Perms.COMMAND_NICK_BYPASS_REGEX)) {
            if (!RegexUtil.getMatcher(this.regex, raw).matches()) {
                plugin.getMessage(Lang.COMMAND_NICK_CHANGE_ERROR_REGEX).send(sender);
                return;
            }
        }
        if (player.hasPermission(Perms.COMMAND_NICK_COLORS)) {
            nick = Colorizer.apply(nick);
        }
        else nick = raw;

        SunUser user = plugin.getUserManager().getUserData(player);
        user.setCustomName(nick);
        user.updatePlayerName();
        this.plugin.getUserManager().saveUser(user);

        plugin.getMessage(Lang.COMMAND_NICK_CHANGE_DONE)
            .replace(Placeholders.GENERIC_NAME, nick)
            .send(sender);
    }
}
