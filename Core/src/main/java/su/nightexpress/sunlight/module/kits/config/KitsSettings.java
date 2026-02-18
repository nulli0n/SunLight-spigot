package su.nightexpress.sunlight.module.kits.config;

import su.nightexpress.nightcore.configuration.AbstractConfig;
import su.nightexpress.nightcore.configuration.ConfigProperty;
import su.nightexpress.nightcore.configuration.ConfigTypes;

public class KitsSettings extends AbstractConfig {

    private final ConfigProperty<Integer> dataSaveInterval = this.addProperty(ConfigTypes.INT, "Global.Data-Save-Interval",
        60,
        ""
    );

    private final ConfigProperty<Integer> kitSaveInterval = this.addProperty(ConfigTypes.INT, "Global.Kits-Save-Interval",
        60,
        ""
    );

    private final ConfigProperty<Boolean> bindItemsToPlayers = this.addProperty(ConfigTypes.BOOLEAN , "Bind_Items_To_Players",
        false,
        "When enabled, all items a player receives from a kit will be bound to that player.",
        "Players can not use/pick up items that are bound to other players."
    );

    private final ConfigProperty<Boolean> guiHideNoPermission = this.addProperty(ConfigTypes.BOOLEAN, "GUI.Hide_No_Permission",
        false,
        "Sets whether or not kits with permission requirement enabled should be hidden from",
        "players that don't have permission to those kits."
    );

    public int getDataSaveInterval() {
        return this.dataSaveInterval.get();
    }

    public int getKitSaveInterval() {
        return this.kitSaveInterval.get();
    }

    public boolean isBindToPlayers() {
        return this.bindItemsToPlayers.get();
    }

    public boolean isHideNoPermKits() {
        return this.guiHideNoPermission.get();
    }
}
