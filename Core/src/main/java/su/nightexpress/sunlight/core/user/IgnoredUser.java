package su.nightexpress.sunlight.core.user;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.UserInfo;

public class IgnoredUser implements Placeholder {

    public static final String PLACEHOLDER_HIDE_CHAT          = "%hide_chat%";
    public static final String PLACEHOLDER_DENY_CONVERSATIONS = "%deny_conversations%";
    public static final String PLACEHOLDER_DENY_TELEPORTS     = "%deny_teleports%";

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
            .add(PLACEHOLDER_HIDE_CHAT, () -> Lang.getYesOrNo(this.isHideChatMessages()))
            .add(PLACEHOLDER_DENY_CONVERSATIONS, () -> Lang.getYesOrNo(this.isDenyConversations()))
            .add(PLACEHOLDER_DENY_TELEPORTS, () -> Lang.getYesOrNo(this.isDenyTeleports()))
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
