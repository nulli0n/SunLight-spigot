package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.argument.ArgumentType;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;
import su.nightexpress.sunlight.module.chat.channel.ChatChannel;

import java.util.Map;
import java.util.Optional;

public class ChannelCommandsProvider extends AbstractCommandProvider {

    private static final String ARG_CHANNEL = "channel";

    private static final String COMMAND_JOIN  = "join";
    private static final String COMMAND_LEAVE = "leave";

    private final ChatModule module;
    private final ArgumentType<ChatChannel> channelArgumentType;

    public ChannelCommandsProvider(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
        this.channelArgumentType = (context, string) -> Optional.ofNullable(module.getChannelRepository().getById(string))
            .orElseThrow(() -> CommandSyntaxException.custom(ChatLang.COMMAND_SYNTAX_INVALID_CHANNEL));
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral(COMMAND_JOIN, false, new String[]{"joinchannel"}, builder -> builder
            .playerOnly()
            .description(ChatLang.COMMAND_CHANNEL_JOIN_DESC)
            .permission(ChatPerms.COMMAND_CHANNEL_JOIN)
            .withArguments(this.channelArgument())
            .executes(this::joinChannel)
        );

        this.registerLiteral(COMMAND_LEAVE, false, new String[]{"leavechannel"}, builder -> builder
            .playerOnly()
            .description(ChatLang.COMMAND_CHANNEL_LEAVE_DESC)
            .permission(ChatPerms.COMMAND_CHANNEL_LEAVE)
            .withArguments(this.channelArgument())
            .executes(this::leaveChannel)
        );

        this.registerRoot("channel", true, new String[]{"channel"},
            Map.of(
                COMMAND_JOIN, "join",
                COMMAND_LEAVE, "leave"
            ),
            builder -> builder.description(ChatLang.COMMAND_CHANNEL_ROOT_DESC).permission(ChatPerms.COMMAND_CHANNEL_ROOT)
        );
    }

    @NotNull
    private ArgumentNodeBuilder<ChatChannel> channelArgument() {
        return Commands.argument(ARG_CHANNEL, this.channelArgumentType)
            .localized(ChatLang.COMMAND_ARGUMENT_NAME_CHANNEL)
            .suggestions((reader, context) -> this.module.getChannelsAllowedToListen(context.getPlayerOrThrow()).stream().map(ChatChannel::getId).toList());
    }

    private boolean joinChannel(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ChatChannel channel = arguments.get(ARG_CHANNEL, ChatChannel.class);

        return this.module.joinChannel(player, channel);
    }

    private boolean leaveChannel(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ChatChannel channel = arguments.get(ARG_CHANNEL, ChatChannel.class);

        return this.module.leaveChannel(player, channel);
    }
}
