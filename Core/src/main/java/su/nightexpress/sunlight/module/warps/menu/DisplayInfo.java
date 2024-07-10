package su.nightexpress.sunlight.module.warps.menu;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.warps.type.SortType;
import su.nightexpress.sunlight.module.warps.type.WarpType;

public class DisplayInfo {

    public WarpType type;
    public SortType sortType;
    public boolean ownedOnly;

    public DisplayInfo(@NotNull WarpType type) {
        this.type = type;
        this.sortType = SortType.WARP_NAME;
        this.ownedOnly = false;
    }
}
