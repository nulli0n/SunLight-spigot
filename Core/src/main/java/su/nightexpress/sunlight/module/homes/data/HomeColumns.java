package su.nightexpress.sunlight.module.homes.data;


import su.nightexpress.nightcore.db.column.Column;

import java.util.UUID;

public class HomeColumns {

    public static final Column<String>  ID              = Column.stringType("homeId", 128).build();
    public static final Column<UUID>    OWNER_ID        = Column.uuidType("ownerId").build();
    public static final Column<String>  OWNER_NAME      = Column.stringType("ownerName", 24).build();
    public static final Column<String>  NAME            = Column.stringType("name", 128).build();
    public static final Column<String>  ICON_ID         = Column.stringType("icon", 32).build();
    public static final Column<String>  POSITION        = Column.stringType("position", 32).defaultValue("0,0,0").build();
    public static final Column<String>  WORLD           = Column.stringType("world", 32).defaultValue("null").build();
    public static final Column<String>  TYPE            = Column.stringType("type", 16).build();
    public static final Column<String>  INVITED_PLAYERS = Column.mediumText("invitedPlayers").build();
    public static final Column<Boolean> FAVORITE        = Column.booleanType("isDefault").build();

    public static final Column<String> LOCATION = Column.mediumText("location").build();
}
