package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.EntityUtil;
import su.nightexpress.nightcore.util.bridge.wrapper.NightComponent;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.regex.TimedMatcher;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.nightcore.util.text.tag.TagPool;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialSettings;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.UserManager;

import java.util.Map;
import java.util.regex.Pattern;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class NickCommandsProvider extends AbstractCommandProvider {

    private static final String COMMAND_CHANGE = "change";
    private static final String COMMAND_CLEAR  = "clear";
    private static final String COMMAND_SET    = "set";

    private static final Permission NICK_ROOT          = EssentialPerms.COMMAND.permission("nick.root");
    private static final Permission NICK_SET           = EssentialPerms.COMMAND.permission("nick.set");
    public static final  Permission NICK_CHANGE        = EssentialPerms.COMMAND.permission("nick.change");
    private static final Permission NICK_CLEAR         = EssentialPerms.COMMAND.permission("nick.clear");
    private static final Permission NICK_CLEAR_OTHERS  = EssentialPerms.COMMAND.permission("nick.clear.others");
    private static final Permission NICK_COLORS        = EssentialPerms.COMMAND.permission("nick.colors");
    private static final Permission NICK_BYPASS_WORDS  = EssentialPerms.COMMAND.permission("nick.bypass.words");
    private static final Permission NICK_BYPASS_REGEX  = EssentialPerms.COMMAND.permission("nick.bypass.regex");
    private static final Permission NICK_BYPASS_LENGTH = EssentialPerms.COMMAND.permission("nick.bypass.length");

    private static final TextLocale COMMAND_NICK_ROOT_DESC  = LangEntry.builder("Command.Nick.Root.Desc").text("Nick commands.");
    private static final TextLocale COMMAND_NICK_CLEAR_DESC  = LangEntry.builder("Command.Nick.Clear.Desc").text("Remove custom name.");
    private static final TextLocale COMMAND_NICK_SET_DESC    = LangEntry.builder("Command.Nick.Set.Desc").text("Set player's custom name.");
    private static final TextLocale COMMAND_NICK_CHANGE_DESC = LangEntry.builder("Command.Nick.Change.Desc").text("Set custom name.");

    private static final MessageLocale COMMAND_NICK_CLEAR_TARGET = LangEntry.builder("Command.Nick.Clear.Target").chatMessage(
        GRAY.wrap("Removed " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME) + "'s custom name."));

    private static final MessageLocale COMMAND_NICK_CLEAR_NOTIFY = LangEntry.builder("Command.Nick.Clear.Notify").chatMessage(
        GRAY.wrap("You custom name has been removed."));

    private static final MessageLocale COMMAND_NICK_SET_TARGET = LangEntry.builder("Command.Nick.Set.Target").chatMessage(
        GRAY.wrap("Set " + SOFT_YELLOW.wrap(PLAYER_NAME) + "'s custom name to " + SOFT_YELLOW.wrap(GENERIC_NAME) + "."));

    private static final MessageLocale COMMAND_NICK_SET_NOTIFY = LangEntry.builder("Command.Nick.Set.Notify").chatMessage(
        GRAY.wrap("You got a new custom name: " + SOFT_YELLOW.wrap(GENERIC_NAME) + "."));

    private static final MessageLocale COMMAND_NICK_CHANGE_DONE = LangEntry.builder("Command.Nick.Change.Done").chatMessage(
        GRAY.wrap("You changed your custom name to " + SOFT_YELLOW.wrap(GENERIC_NAME) + "."));

    private static final MessageLocale COMMAND_NICK_CHANGE_ERROR_BAD_WORDS = LangEntry.builder("Command.Nick.Error.BadWords").chatMessage(
        SOFT_RED.wrap("This name is not allowed."));

    private static final MessageLocale COMMAND_NICK_CHANGE_ERROR_REGEX = LangEntry.builder("Command.Nick.Error.Regex").chatMessage(
        SOFT_RED.wrap("Name contains forbidden characters."));

    private static final MessageLocale COMMAND_NICK_CHANGE_ERROR_TOO_LONG = LangEntry.builder("Command.Nick.Error.TooLong").chatMessage(
        GRAY.wrap("Name can't be no longer than " + SOFT_RED.wrap(GENERIC_AMOUNT) + " characters."));

    private static final MessageLocale COMMAND_NICK_CHANGE_ERROR_TOO_SHORT = LangEntry.builder("Command.Nick.Error.TooShort").chatMessage(
        SOFT_RED.wrap("Name can't be shorted than " + SOFT_RED.wrap(GENERIC_AMOUNT) + " characters."));

    private final EssentialModule   module;
    private final EssentialSettings settings;
    private final UserManager userManager;

    private final Pattern nickPattern;

    public NickCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull EssentialSettings settings, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.settings = settings;
        this.userManager = userManager;
        this.nickPattern = Pattern.compile(this.settings.nickRegex.get());
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_CHANGE, true, new String[]{"changenick"}, builder -> builder
            .playerOnly()
            .description(COMMAND_NICK_CHANGE_DESC)
            .permission(NICK_CHANGE)
            .withArguments(Arguments.greedyString(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME))
            .executes(this::changeNick)
        );

        this.registerLiteral(COMMAND_CLEAR, true, new String[]{"clearnick"}, builder -> builder
            .description(COMMAND_NICK_CLEAR_DESC)
            .permission(NICK_CLEAR)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER).optional().permission(NICK_CLEAR_OTHERS))
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::clearNick)
        );

        this.registerLiteral(COMMAND_SET, true, new String[]{"setnick"}, builder -> builder
            .description(COMMAND_NICK_SET_DESC)
            .permission(NICK_SET)
            .withArguments(
                Arguments.playerName(CommandArguments.PLAYER),
                Arguments.greedyString(CommandArguments.NAME).localized(CoreLang.COMMAND_ARGUMENT_NAME_NAME)
            )
            .withFlags(CommandArguments.FLAG_SILENT)
            .executes(this::setNickForPlayer)
        );

        this.registerRoot("nickname", true, new String[]{"nickname"},
            Map.of(
                COMMAND_CHANGE, "change",
                COMMAND_CLEAR, "clear",
                COMMAND_SET, "set"
            ),
            builder -> builder.description(COMMAND_NICK_ROOT_DESC).permission(NICK_ROOT)
        );
    }

    // TODO Rework

    @NotNull
    private static String filterColors(@NotNull String name) {
        return NightMessage.stripTags(name, TagPool.ALL_COLORS_AND_STYLES);
    }

    private boolean changeNick(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        String nick = arguments.getString(CommandArguments.NAME);

        String raw = NightMessage.stripTags(nick);
        if (!player.hasPermission(NICK_BYPASS_LENGTH)) {
            if (raw.length() < this.settings.nickMinLength.get()) {
                this.module.sendPrefixed(COMMAND_NICK_CHANGE_ERROR_TOO_SHORT, context.getSender(), replacer -> replacer.with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(this.settings.nickMinLength.get())));
                return false;
            }
            if (raw.length() > this.settings.nickMaxLength.get()) {
                this.module.sendPrefixed(COMMAND_NICK_CHANGE_ERROR_TOO_LONG, context.getSender(), replacer -> replacer.with(SLPlaceholders.GENERIC_AMOUNT, () -> String.valueOf(this.settings.nickMaxLength.get())));
                return false;
            }
        }

        if (!player.hasPermission(NICK_BYPASS_WORDS)) {
            if (this.settings.nickBannedWords.get().stream().anyMatch(word -> raw.toLowerCase().contains(word.toLowerCase()))) {
                this.module.sendPrefixed(COMMAND_NICK_CHANGE_ERROR_BAD_WORDS, context.getSender());
                return false;
            }
            if (plugin.getServer().getPlayerExact(raw) != null) {
                this.module.sendPrefixed(COMMAND_NICK_CHANGE_ERROR_BAD_WORDS, context.getSender());
                return false;
            }
        }

        if (!player.hasPermission(NICK_BYPASS_REGEX)) {
            if (!TimedMatcher.create(this.nickPattern, raw).matches()) {
                this.module.sendPrefixed(COMMAND_NICK_CHANGE_ERROR_REGEX, context.getSender());
                return false;
            }
        }

        if (player.hasPermission(NICK_COLORS)) {
            nick = filterColors(nick);
        }
        else nick = raw;

        SunUser user = plugin.getUserManager().getOrFetch(player);
        this.module.setCustomName(user, nick);
        String finalNick = nick;
        this.module.sendPrefixed(COMMAND_NICK_CHANGE_DONE, context.getSender(), replacer -> replacer.with(SLPlaceholders.GENERIC_NAME, () -> finalNick));

        return true;
    }

    private boolean clearNick(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerOrSenderWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (user, target) -> {
            this.module.setCustomName(user, null);

            EntityUtil.setCustomName(target, (NightComponent) null);

            if (context.getSender() != target) {
                this.module.sendPrefixed(COMMAND_NICK_CLEAR_TARGET, context.getSender(), replacer -> replacer.with(CommonPlaceholders.PLAYER.resolver(target)));
            }
            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(COMMAND_NICK_CLEAR_NOTIFY, target);
            }
        });
    }

    private boolean setNickForPlayer(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        return this.loadPlayerWithDataAndRunInMainThread(context, arguments, this.module, this.userManager, (user, target) -> {
            String nick = arguments.getString(CommandArguments.NAME);
            String raw = NightMessage.stripAll(nick);
            if (context.hasPermission(NICK_COLORS)) {
                nick = filterColors(nick);
            }
            else nick = raw;

            this.module.setCustomName(user, nick);

            if (context.getSender() != user.player().orElse(null)) {
                String finalNick1 = nick;
                this.module.sendPrefixed(COMMAND_NICK_SET_TARGET, context.getSender(), replacer -> replacer
                    .with(SLPlaceholders.GENERIC_NAME, () -> finalNick1)
                    .with(SLPlaceholders.PLAYER_NAME, user::getName)
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                String finalNick = nick;
                this.module.sendPrefixed(COMMAND_NICK_SET_NOTIFY, target, replacer -> replacer.with(SLPlaceholders.GENERIC_NAME, () -> finalNick));
            }
        });
    }
}
