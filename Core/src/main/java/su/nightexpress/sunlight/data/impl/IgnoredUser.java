package su.nightexpress.sunlight.data.impl;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.lang.LangManager;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.utils.UserInfo;

public class IgnoredUser implements Placeholder {

    private static final String PLACEHOLDER_HIDE_CHAT          = "%hide_chat%";
    private static final String PLACEHOLDER_DENY_CONVERSATIONS = "%deny_conversations%";
    private static final String PLACEHOLDER_DENY_TELEPORTS     = "%deny_teleports%";

    private final UserInfo       userInfo;
    private final PlaceholderMap placeholderMap;

    private boolean hideChatMessages;
    private boolean denyConversations;
    private boolean denyTeleports;

    public IgnoredUser(@NotNull UserInfo userInfo) {
        this(userInfo, false, true, true);
    }

    public IgnoredUser(@NotNull UserInfo userInfo,
                       boolean hideChatMessages,
                       boolean denyConversations,
                       boolean denyTeleports) {
        this.userInfo = userInfo;
        this.setHideChatMessages(hideChatMessages);
        this.setDenyConversations(denyConversations);
        this.setDenyTeleports(denyTeleports);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.GENERIC_NAME, () -> this.getUserInfo().getName())
            .add(PLACEHOLDER_HIDE_CHAT, () -> LangManager.getBoolean(this.isHideChatMessages()))
            .add(PLACEHOLDER_DENY_CONVERSATIONS, () -> LangManager.getBoolean(this.isDenyConversations()))
            .add(PLACEHOLDER_DENY_TELEPORTS, () -> LangManager.getBoolean(this.isDenyTeleports()))
        ;
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public UserInfo getUserInfo() {
        return userInfo;
    }

    public boolean isHideChatMessages() {
        return this.hideChatMessages;
    }

    public void setHideChatMessages(boolean hideChatMessages) {
        this.hideChatMessages = hideChatMessages;
    }

    public boolean isDenyConversations() {
        return this.denyConversations;
    }

    public void setDenyConversations(boolean denyConversations) {
        this.denyConversations = denyConversations;
    }

    public boolean isDenyTeleports() {
        return this.denyTeleports;
    }

    public void setDenyTeleports(boolean denyTeleports) {
        this.denyTeleports = denyTeleports;
    }
}
