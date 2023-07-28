package su.nightexpress.sunlight.nms.v1_19_R3.container;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.jetbrains.annotations.NotNull;

public class GrindstoneContainer extends GrindstoneMenu {

    public GrindstoneContainer(int contId, @NotNull ServerPlayer player) {
        super(contId, player.getInventory(), ContainerLevelAccess.create(player.level, player.blockPosition()));
        this.checkReachable = false;
    }
}
