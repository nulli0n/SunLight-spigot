package su.nightexpress.sunlight.module.chat.processor.chat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.ChatProperties;
import su.nightexpress.sunlight.module.chat.cache.UserChatCache;
import su.nightexpress.sunlight.module.chat.context.MessageContext;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.core.ChatPerms;
import su.nightexpress.sunlight.module.chat.mention.ChatMention;
import su.nightexpress.sunlight.module.chat.mention.PlayerMention;
import su.nightexpress.sunlight.module.chat.processor.MessageProcessor;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.UserManager;

public class MentionProcessor implements MessageProcessor {

    private final Pattern                  pattern;
    private final UserManager              userManager;
    private final Map<String, ChatMention> mentions;

    public MentionProcessor(@NonNull Pattern pattern, @NonNull UserManager userManager) {
        this.pattern = pattern;
        this.userManager = userManager;
        this.mentions = new HashMap<>();
    }

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull MessageContext context) {
        Player player = context.getPlayer();
        UserChatCache cache = context.getCache();
        String format = context.getFormat();

        int mentionsLimit = player.hasPermission(ChatPerms.BYPASS_MENTION_AMOUNT) ? -1 : module.getSettings()
            .getMentionsLimit();
        int mentionsCount = 0;

        Matcher matcher = this.pattern.matcher(format);
        StringBuilder builder = new StringBuilder(format.length() + 50);

        while (matcher.find()) {
            String mentionName = matcher.group(1);
            Runnable appendRaw = () -> matcher.appendReplacement(builder, Matcher.quoteReplacement(matcher.group(0)));

            ChatMention mention = this.getMention(module, context, mentionName);

            if (mention == null) {
                appendRaw.run();
                continue;
            }

            // Do not tag yourself.
            if (mention instanceof PlayerMention playerMention && playerMention.isApplicable(player)) {
                appendRaw.run();
                continue;
            }

            if (!ChatPerms.MENTION.hasChildAccess(player, LowerCase.INTERNAL.apply(mentionName))) {
                appendRaw.run();
                continue;
            }

            if (mentionsLimit >= 0 && mentionsCount >= mentionsLimit) {
                appendRaw.run();
                break;
            }

            if (cache.hasMentionCooldown(mentionName)) {
                module.sendPrefixed(ChatLang.MENTION_ERROR_COOLDOWN, player, replacer -> replacer
                    .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatDuration(cache
                        .getMentionCooldownTimestamp(mentionName), TimeFormatType.LITERAL))
                    .with(SLPlaceholders.GENERIC_NAME, () -> mentionName)
                );
                appendRaw.run();
                continue;
            }

            this.mentions.put(LowerCase.INTERNAL.apply(mentionName), mention);
            matcher.appendReplacement(builder, mention.getFormat());
            mentionsCount++;
        }

        matcher.appendTail(builder);

        context.setFormat(builder.toString());
    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull MessageContext context) {
        Player player = context.getPlayer();
        Set<Player> targets = new HashSet<>();

        boolean hasBypass = player.hasPermission(ChatPerms.BYPASS_MENTION_COOLDOWN);
        int cooldown = module.getSettings().getMentionsCooldown();

        this.mentions.forEach((id, mention) -> {
            context.getViewers().forEach(sender -> {
                if (sender instanceof Player target && mention.isApplicable(target)) {
                    SunUser targetUser = this.userManager.getOrFetch(target);
                    if (targetUser.getPropertyOrDefault(ChatProperties.MENTIONS)) {
                        targets.add(target);
                    }
                }
            });

            if (!hasBypass && cooldown > 0) {
                context.getCache().setMentionCooldown(id, cooldown);
            }
        });

        module.sendPrefixed(ChatLang.MENTION_NOTIFY, targets, replacer -> replacer.with(CommonPlaceholders.PLAYER
            .resolver(context.getPlayer())));
    }

    @Nullable
    private ChatMention getMention(@NonNull ChatModule module, @NonNull MessageContext context, @NonNull String name) {
        ChatMention mention = module.getSettings().getCustomMentions().get(LowerCase.INTERNAL.apply(name));
        if (mention != null) return mention;

        Player player = Bukkit.getPlayerExact(name);
        if (player == null) return null;
        if (!context.getViewers().contains(player)) return null;

        String format = CommonPlaceholders.PLAYER.replacer(player).apply(module.getSettings().getMentionsFormat());

        return new PlayerMention(player.getName(), format);
    }
}
