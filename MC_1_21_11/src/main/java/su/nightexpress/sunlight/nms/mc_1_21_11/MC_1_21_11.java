package su.nightexpress.sunlight.nms.mc_1_21_11;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jspecify.annotations.NonNull;

import com.mojang.authlib.GameProfile;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.players.NameAndId;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.sunlight.api.PortableContainer;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.nms.mc_1_21_11.container.PlayerEnderChest;
import su.nightexpress.sunlight.nms.mc_1_21_11.container.PlayerInventory;

public class MC_1_21_11 implements SunNMS {

    private static final Method SET_GAME_MODE         = Reflex.safeMethod(ServerPlayerGameMode.class,
        "setGameModeForPlayer", "a", GameType.class, GameType.class);
    private static final Method OPEN_CUSTOM_INVENTORY = Reflex.safeMethod(CraftHumanEntity.class, "openCustomInventory",
        Inventory.class, ServerPlayer.class, net.minecraft.world.inventory.MenuType.class);

    @Override
    public void dropFallingContent(@NonNull FallingBlock fallingBlock) {
        CraftFallingBlock craftBlock = (CraftFallingBlock) fallingBlock;
        FallingBlockEntity nmsBlock = craftBlock.getHandle();

        nmsBlock.spawnAtLocation((ServerLevel) nmsBlock.level(), nmsBlock.getBlockState().getBlock());
    }

    @NonNull
    public Object fineChatPacket(@NonNull Object packet) {
        ClientboundPlayerChatPacket chatPacket = (ClientboundPlayerChatPacket) packet;
        Component component = chatPacket.unsignedContent() == null ? Component.literal(chatPacket.body()
            .content()) : chatPacket.unsignedContent();

        Holder<ChatType> typeHolder = chatPacket.chatType().chatType();

        ChatType.Bound decorator = new ChatType.Bound(typeHolder, chatPacket.chatType().name(), chatPacket.chatType()
            .targetName());
        component = decorator.decorate(component);

        return new ClientboundSystemChatPacket(component, false);
    }

    @Override

    public org.bukkit.entity.@NonNull Player loadPlayerData(@NonNull UUID id, @NonNull String name) {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        DedicatedServer server = craftServer.getServer();
        DedicatedPlayerList playerList = craftServer.getHandle();
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        if (level == null) throw new IllegalStateException("Server level is null");

        GameProfile profile = new GameProfile(id, name);
        ServerPlayer serverPlayer = new ServerPlayer(server, level, profile, ClientInformation.createDefault()); // GameMode reset

        ProblemReporter reporter = new ProblemReporter.Collector();
        RegistryAccess access = serverPlayer.registryAccess();


        NameAndId nameAndId = new NameAndId(id, name);

        var input = playerList.playerIo.load(nameAndId).orElse(new CompoundTag());
        var value = TagValueInput.create(reporter, access, input);

        serverPlayer.load(value);

        return serverPlayer.getBukkitEntity();
    }

    @Override
    public void setGameMode(@NonNull Player player, @NonNull GameMode mode) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        GameType gameType = GameType.byName(mode.name().toLowerCase());
        GameType previous = serverPlayer.gameMode.getPreviousGameModeForPlayer();

        Reflex.invokeMethod(SET_GAME_MODE, serverPlayer.gameMode, gameType, previous);
        craftPlayer.saveData();
    }

    @Override
    public void teleport(@NonNull Player player, @NonNull Location location) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.setPosRaw(location.getX(), location.getY(), location.getZ());
        if (player.getWorld() != location.getWorld() && location.getWorld() != null) {
            CraftWorld craftWorld = (CraftWorld) location.getWorld();
            serverPlayer.setServerLevel(craftWorld.getHandle());
        }
        craftPlayer.saveData();
    }

    @Override
    @NonNull
    public Inventory getPlayerEnderChest(@NonNull Player player) {
        return new PlayerEnderChest((CraftPlayer) player).getInventory();
    }

    @Override
    @NonNull
    public Inventory getPlayerInventory(@NonNull Player player) {
        return new PlayerInventory((CraftPlayer) player).getInventory();
    }

    @Override
    public void openPlayerInventory(@NonNull Player player, @NonNull Player owner) {
        Inventory inventory = this.getPlayerInventory(owner);  // Patched CraftInventory used here to prevent inventory type & size mismatch.
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();

        // There is a "wrong" menu type obtained in the CraftHumanEntity#openInventory -> CraftContainer.getNotchInventoryType(inventory)
        // This is caused by CraftInventory wrapper with the PlayerInventory container inside, which getNotchInventoryType takes it into an account and returns wrong MenuType.
        // We have to hardcode the 'windowType' variable here as 9X5 menu type to prevent Network Protocol Error due to slots size mismatch.
        Reflex.invokeMethod(OPEN_CUSTOM_INVENTORY, null, inventory, serverPlayer,
            net.minecraft.world.inventory.MenuType.GENERIC_9x5);
    }

    @Override
    public void openContainer(@NonNull Player player, @NonNull PortableContainer menuType) {
        AbstractContainerMenu menu = this.createContainer(menuType, player);

        player.openInventory(menu.getBukkitView());
    }

    @NonNull
    private AbstractContainerMenu createContainer(@NonNull PortableContainer type, @NonNull Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer nmsPlayer = craftPlayer.getHandle();
        int contId = nmsPlayer.nextContainerCounter();
        ContainerLevelAccess access = ContainerLevelAccess.create(nmsPlayer.level(), nmsPlayer.blockPosition());
        net.minecraft.world.entity.player.Inventory inventory = nmsPlayer.getInventory();

        AbstractContainerMenu menu = switch (type) {
            case ANVIL -> new AnvilMenu(contId, inventory, access);
            case WORKBENCH -> new CraftingMenu(contId, inventory, access);
            case ENCHANTING_TABLE -> new EnchantmentMenu(contId, inventory, access);
            case LOOM -> new LoomMenu(contId, inventory, access);
            case SMITHING_TABLE -> new SmithingMenu(contId, inventory, access);
            case GRINDSTONE -> new GrindstoneMenu(contId, inventory, access);
            case STONECUTTER -> new StonecutterMenu(contId, inventory, access);
            case CARTOGRAPHY_TABLE -> new CartographyTableMenu(contId, inventory, access);
        };
        menu.checkReachable = false;

        return menu;
    }
}
