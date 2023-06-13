package su.nightexpress.sunlight.nms.V1_20_R1.container;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.EnchantmentMenu;

public class EnchantingContainer extends EnchantmentMenu {

    public EnchantingContainer(int containerId, ServerPlayer player) {
        super(containerId, player.getInventory(), ContainerLevelAccess.create(player.level(), player.blockPosition()));
        this.checkReachable = false;
    }
}
