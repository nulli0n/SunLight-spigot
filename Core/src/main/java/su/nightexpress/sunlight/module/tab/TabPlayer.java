package su.nightexpress.sunlight.module.tab;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.module.tab.format.TabLayoutFormat;
import su.nightexpress.sunlight.module.tab.format.TabNameFormat;

public class TabPlayer {

    private final Player player;
    private final PlaceholderContext placeholderContext;

    private TabLayoutFormat layoutFormat;
    private TabNameFormat nameFormat;

    public TabPlayer(@NotNull Player player, @NotNull PlaceholderContext placeholderContext) {
        this.player = player;
        this.placeholderContext = placeholderContext;
    }

    public void updatePlayerList() {
        String listName = this.nameFormat == null ? this.player.getName() : this.placeholderContext.apply(this.nameFormat.getFormat());
        String header = this.layoutFormat == null ? null : this.placeholderContext.apply(String.join("\n", this.layoutFormat.getHeader()));
        String footer = this.layoutFormat == null ? null : this.placeholderContext.apply(String.join("\n", this.layoutFormat.getFooter()));

        Players.setPlayerListName(this.player, listName);
        Players.setPlayerListHeaderFooter(this.player, header, footer);
    }

    public boolean isOnline() {
        return this.player.isOnline();
    }

    @NotNull
    public String withPlaceholders(@NotNull String string) {
        return this.placeholderContext.apply(string);
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public PlaceholderContext getPlaceholderContext() {
        return this.placeholderContext;
    }

    @Nullable
    public TabLayoutFormat getLayoutFormat() {
        return layoutFormat;
    }

    public void setLayoutFormat(@Nullable TabLayoutFormat layoutFormat) {
        this.layoutFormat = layoutFormat;
    }

    @Nullable
    public TabNameFormat getNameFormat() {
        return this.nameFormat;
    }

    public void setNameFormat(@Nullable TabNameFormat nameFormat) {
        this.nameFormat = nameFormat;
    }
}
