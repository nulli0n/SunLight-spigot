package su.nightexpress.sunlight.module.bans.menu;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.module.bans.punishment.PunishData;
import su.nightexpress.sunlight.module.bans.punishment.PunishedIP;
import su.nightexpress.sunlight.module.bans.punishment.PunishedPlayer;

import java.util.Comparator;

public enum SortMode {

    NAME((data1, data2) -> {
        if (data1 instanceof PunishedIP ip1 && data2 instanceof PunishedIP ip2) {
            return ip1.getAddress().compareTo(ip2.getAddress());
        }
        if (data1 instanceof PunishedPlayer player1 && data2 instanceof PunishedPlayer player2) {
            return player1.getPlayerName().compareTo(player2.getPlayerName());
        }
        return 0;
    }),
    DATE(Comparator.comparingLong(PunishData::getCreateDate).reversed()),
    PUNISHER(Comparator.comparing(PunishData::getAdmin)),
    REASON(Comparator.comparing(PunishData::getReason));

    private final Comparator<PunishData> comparator;

    SortMode(@NotNull Comparator<PunishData> comparator) {
        this.comparator = comparator;
    }

    @NotNull
    public Comparator<PunishData> getComparator() {
        return comparator;
    }
}
