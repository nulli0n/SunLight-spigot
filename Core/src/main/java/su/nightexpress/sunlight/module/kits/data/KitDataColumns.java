package su.nightexpress.sunlight.module.kits.data;

import su.nightexpress.nightcore.db.column.Column;

import java.util.UUID;

public class KitDataColumns {

    public static final Column<UUID>   PLAYER_ID     = Column.uuidType("playerId").build();
    public static final Column<String> KIT_ID        = Column.stringType("kitId", 64).build();
    public static final Column<Long>   COOLDOWN_DATE = Column.longType("cooldownDate").build();
}
