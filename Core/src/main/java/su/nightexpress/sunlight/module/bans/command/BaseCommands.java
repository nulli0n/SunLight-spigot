package su.nightexpress.sunlight.module.bans.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansConfig;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.utils.UserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BaseCommands {

    private static final String NODE_ALTS = "alts";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull BansModule module) {
        if (BansConfig.isAltCheckerEnabled()) {
            CommandRegistry.registerDirectExecutor(NODE_ALTS, (template, config) -> DirectNode.builder(plugin, template.getAliases())
                .description(BansLang.COMMAND_ALTS_DESC)
                .permission(BansPerms.COMMAND_ALTS)
                //.withArgument(ArgumentTypes.playerName(CommandArguments.PLAYER))
                .executes((context, arguments) -> showAlts(plugin, module, context, arguments))
            );

            CommandRegistry.addSimpleTemplate(NODE_ALTS);
        }
    }

    public static boolean showAlts(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Map<String, Set<UserInfo>> map = new HashMap<>(module.getAltAccountsMap());
        map.values().removeIf(set -> set.isEmpty() || set.size() < 2);

        if (map.isEmpty()) {
            BansLang.ALTS_GLOBAL_NOTHING.getMessage().send(context.getSender());
            return false;
        }

        BansLang.ALTS_GLOBAL_LIST.getMessage()
            .replace(Placeholders.GENERIC_ENTRY, list -> {
                map.forEach((address, players) -> {
                    String playerNames = players.stream().map(UserInfo::getName).collect(Collectors.joining(", "));
                    list.add(BansLang.ALTS_GLOBAL_ENTRY.getString()
                        .replace(Placeholders.GENERIC_SOURCE, address)
                        .replace(Placeholders.GENERIC_NAME, playerNames)
                    );
                });
            })
            .send(context.getSender());

        return true;
    }
}
