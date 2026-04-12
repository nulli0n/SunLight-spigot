package su.nightexpress.sunlight.module.chat.processor.global;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.cache.CachedContent;
import su.nightexpress.sunlight.module.chat.context.ChatContext;
import su.nightexpress.sunlight.module.chat.core.ChatLang;
import su.nightexpress.sunlight.module.chat.processor.ChatProcessor;

public class AntiFloodProcessor implements ChatProcessor<ChatContext> {

    private boolean floodDetected;

    @Override
    public void preProcess(@NonNull ChatModule module, @NonNull ChatContext context) {
        Player player = context.getPlayer();

        CachedContent lastContent = context.getLastContent();
        if (lastContent == null || lastContent.isExpired()) return;

        double threshold = module.getSettings().getAntiFloodSimilarityScoreThreshold();
        if (!this.isSimilarEnough(context.getMessage(), lastContent.content(), threshold)) return;

        this.floodDetected = true;

        int score = lastContent.getCount();
        if (score >= module.getSettings().getAntiFloodSimilarityCountThreshold()) {
            module.sendPrefixed(ChatLang.ANTI_FLOOD_SIMILAR_CONTENT, player);
            context.cancel();
            return;
        }

        lastContent.addCount();
    }

    @Override
    public void postProcess(@NonNull ChatModule module, @NonNull ChatContext context) {
        if (this.floodDetected) return; // Do not override last content if player is flooding the same one.

        int lifeTime = module.getSettings().getUserContentCacheLifetime();
        if (lifeTime <= 0) return;

        context.setLastContent(context.getMessage(), lifeTime);
    }

    private boolean isSimilarEnough(@NonNull String left, @NonNull String right, double similarityThreshold) {
        double score = getSimiliartyScore(left, right);
        double threshold = similarityThreshold / 100D;

        return score >= threshold;
    }

    public static double getSimiliartyScore(@NonNull String left, @NonNull String right) {
        JaroWinklerSimilarity similarity = new JaroWinklerSimilarity();
        return similarity.apply(left, right);
    }
}
