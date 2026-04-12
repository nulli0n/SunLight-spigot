package su.nightexpress.sunlight.module.chat.processor.command;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.cache.UserChatCache;
import su.nightexpress.sunlight.module.chat.context.CommandContext;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

public class CommandCooldownProcessor implements ChatProcessor<CommandContext> {

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull CommandContext context) {
        Player player = context.getPlayer();
        UserChatCache cache = context.getCache();

        long nextCommandTimestamp = cache.getNextCommandTimestamp();
        if (TimeUtil.isPassed(nextCommandTimestamp)) return;

        module.sendPrefixed(ChatLang.ANTI_FLOOD_COMMAND_COOLDOWN, player);
        context.cancel();
    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull CommandContext context) {
        context.getCache().setNextCommandTimestamp(module.getSettings().getAntiFloodCommandCooldown());
    }
}
