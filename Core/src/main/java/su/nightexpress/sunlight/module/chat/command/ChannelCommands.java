package su.nightexpress.sunlight.module.chat.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.RootCommand;
import su.nightexpress.nightcore.command.experimental.ServerCommand;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.chat.ChatChannel;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatLang;
import su.nightexpress.sunlight.module.chat.config.ChatPerms;

public class ChannelCommands {

    private static final String ARG_CHANNEL = "channel";

    public static final String NODE_JOIN  = "channel_join";
    public static final String NODE_LEAVE = "channel_leave";
    public static final String NODE_SET   = "channel_set";

    public static void load(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        CommandRegistry.registerDirectExecutor(NODE_JOIN, (template, config) -> builderJoin(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_LEAVE, (template, config) -> builderLeave(plugin, module, template, config));
        CommandRegistry.registerDirectExecutor(NODE_SET, (template, config) -> builderSet(plugin, module, template, config));

        CommandRegistry.addTemplate("channel", CommandTemplate.group(new String[]{"channel"},
            "Chat channel commands.",
            ChatPerms.PREFIX_COMMAND + "channel",
            CommandTemplate.direct(new String[]{"join"}, NODE_JOIN),
            CommandTemplate.direct(new String[]{"leave"}, NODE_LEAVE),
            CommandTemplate.direct(new String[]{"set"}, NODE_SET)
        ));

        CommandRegistry.addTemplate("joinchannel", CommandTemplate.direct(new String[]{"joinchannel"}, NODE_JOIN));
        CommandRegistry.addTemplate("leavechannel", CommandTemplate.direct(new String[]{"leavechannel"}, NODE_LEAVE));
        CommandRegistry.addTemplate("setchannel", CommandTemplate.direct(new String[]{"setchannel"}, NODE_SET));

        // Direct register for channel commands.
        for (ChatChannel channel : module.getChannelManager().getChannels()) {
            ServerCommand command = RootCommand.direct(plugin, channel.getId(), builder -> builder
                .playerOnly()
                .description(ChatLang.COMMAND_SHORT_CHANNEL_DESC)
                .permission(ChatPerms.COMMAND_CHANNEL_SET)
                //.withArgument(ArgumentTypes.string(CommandArguments.TEXT).complex().localized(ChatLang.COMMAND_ARGUMENT_NAME_TEXT))
                .executes((context, arguments) -> directChannel(plugin, module, context, arguments, channel))
            );
            plugin.getCommandManager().registerCommand(command);
        }
    }

    public static void unload(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        // Unregister dynamic channel commands.
        for (ChatChannel channel : module.getChannelManager().getChannels()) {
            plugin.getCommandManager().unregisterServerCommand(channel.getId());
        }
    }



    @NotNull
    private static ArgumentBuilder<ChatChannel> channelArgument(@NotNull ChatModule module) {
        return CommandArgument.builder(ARG_CHANNEL, (string, context) -> module.getChannelManager().getChannel(string))
            .customFailure(ChatLang.ERROR_COMMAND_INVALID_CHANNEL_ARGUMENT)
            .localized(ChatLang.COMMAND_ARGUMENT_NAME_CHANNEL)
            .withSamples(context -> module.getChannelManager().getAvailableChannels(context.getPlayerOrThrow()).stream().map(ChatChannel::getId).toList());
    }



    public static boolean directChannel(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments,
                                        @NotNull ChatChannel channel) {
        Player player = context.getExecutor();
        if (player == null) return false;

        //if (!arguments.hasArgument(CommandArguments.TEXT)) {
        module.getChannelManager().setActiveChannel(player, channel);
        return true;
        /*}

        String message = channel.getMessagePrefix() + arguments.getStringArgument(CommandArguments.TEXT);

        plugin.runTaskAsync(task -> {
            AsyncPlayerChatEvent chatEvent = new AsyncPlayerChatEvent(true, player, message, new HashSet<>(plugin.getServer().getOnlinePlayers()));
            plugin.getPluginManager().callEvent(chatEvent);
        });

        return true;*/
    }



    @NotNull
    public static DirectNodeBuilder builderJoin(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(ChatLang.COMMAND_CHANNEL_JOIN_DESC)
            .permission(ChatPerms.COMMAND_CHANNEL_JOIN)
            .withArgument(channelArgument(module).required())
            .executes((context, arguments) -> joinChannel(plugin, module, context, arguments))
            ;
    }

    public static boolean joinChannel(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ChatChannel channel = arguments.getArgument(ARG_CHANNEL, ChatChannel.class);

        module.getChannelManager().joinChannel(player, channel);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderLeave(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(ChatLang.COMMAND_CHANNEL_LEAVE_DESC)
            .permission(ChatPerms.COMMAND_CHANNEL_LEAVE)
            .withArgument(channelArgument(module).required())
            .executes((context, arguments) -> leaveChannel(plugin, module, context, arguments))
            ;
    }

    public static boolean leaveChannel(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ChatChannel channel = arguments.getArgument(ARG_CHANNEL, ChatChannel.class);

        module.getChannelManager().leaveChannel(player, channel);
        return true;
    }



    @NotNull
    public static DirectNodeBuilder builderSet(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(ChatLang.COMMAND_CHANNEL_SET_DESC)
            .permission(ChatPerms.COMMAND_CHANNEL_SET)
            .withArgument(channelArgument(module).required())
            .executes((context, arguments) -> setChannel(plugin, module, context, arguments))
            ;
    }

    public static boolean setChannel(@NotNull SunLightPlugin plugin, @NotNull ChatModule module, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        ChatChannel channel = arguments.getArgument(ARG_CHANNEL, ChatChannel.class);

        module.getChannelManager().setActiveChannel(player, channel);
        return true;
    }
}
