package su.nightexpress.sunlight.module.chat.rule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.LowerCase;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private final Map<Character, TrieNode> children;

    private boolean isEnd;

    public TrieNode() {
        this.children = new HashMap<>();
    }

    public void add(@NotNull String word) {
        String lowered = LowerCase.USER_LOCALE.apply(word);
        TrieNode node = this;

        for (char c : lowered.toCharArray()) {
            node = node.children.computeIfAbsent(c,  k -> new TrieNode());
        }
        node.isEnd = true;
    }

    @Nullable
    public TrieNode children(char c) {
        return this.children.get(c);
    }

    public boolean isEnd() {
        return this.isEnd;
    }
}
