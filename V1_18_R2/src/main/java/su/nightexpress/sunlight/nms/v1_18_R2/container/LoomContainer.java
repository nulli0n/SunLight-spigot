package su.nightexpress.sunlight.nms.v1_18_R2.container;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LoomMenu;
import org.jetbrains.annotations.NotNull;

public class LoomContainer extends LoomMenu {

    public LoomContainer(int contId, @NotNull ServerPlayer player) {
        super(contId, player.getInventory(), ContainerLevelAccess.create(player.level, player.blockPosition()));
        this.checkReachable = false;
    }
}
