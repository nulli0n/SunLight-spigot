package su.nightexpress.sunlight.module.chat;

import su.nightexpress.nightcore.util.placeholder.TypedPlaceholder;
import su.nightexpress.sunlight.module.chat.channel.ChatChannel;
import su.nightexpress.sunlight.module.chat.core.ChatLang;

public class ChatPlaceholders {

    public static final String CHANNEL_ID             = "%channel_id%";
    public static final String CHANNEL_NAME           = "%channel_name%";
    public static final String CHANNEL_DISTANCE_TYPE  = "%channel_distance_type%";
    public static final String CHANNEL_DISTANCE_RANGE = "%channel_distance_range%";

    public static final String ITEM_NAME  = "%item_name%";
    public static final String ITEM_VALUE = "%item_value%";

    public static final TypedPlaceholder<ChatChannel> CHANNEL = TypedPlaceholder.builder(ChatChannel.class)
        .with(CHANNEL_ID, ChatChannel::getId)
        .with(CHANNEL_NAME, channel -> channel.getDisplay().name())
        .with(CHANNEL_DISTANCE_TYPE, channel -> ChatLang.CHANNEL_DISTANCE_TYPE.getLocalized(channel.getDistance().type()))
        .with(CHANNEL_DISTANCE_RANGE, channel -> String.valueOf(channel.getDistance().range()))
        .build();
}
