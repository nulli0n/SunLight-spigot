package su.nightexpress.sunlight.nms.V1_20_R1.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.level.block.state.BlockState;

public class AnvilContainer extends AnvilMenu {

    public AnvilContainer(int containerId, Inventory inventory) {
        super(containerId, inventory);
        this.checkReachable = false;
    }

    @Override
    protected boolean isValidBlock(BlockState iblockdata) {
        return true;
    }
}
