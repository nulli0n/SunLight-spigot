package su.nightexpress.sunlight.nms.mc_1_21_6.container;

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
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Reflex;
import su.nightexpress.sunlight.api.MenuType;
import su.nightexpress.sunlight.nms.SunNMS;
import su.nightexpress.sunlight.nms.mc_1_21_6.container.container.PlayerEnderChest;
import su.nightexpress.sunlight.nms.mc_1_21_6.container.container.PlayerInventory;

import java.lang.reflect.Method;
import java.util.UUID;

public class MC_1_21_6 implements SunNMS {

    // setGameModeForPlayer
    private static final Method SET_GAME_MODE = Reflex.getMethod(ServerPlayerGameMode.class, "setGameModeForPlayer", "a", GameType.class, GameType.class);

    public MC_1_21_6() {
//        RegistryAccess access = CraftRegistry.getMinecraftRegistry();
//        access.createSerializationContext(NbtOps.INSTANCE);
//
//        TagValueOutput out = TagValueOutput.createWithContext(new ProblemReporter.Collector(), access);
//        out.store("Item", net.minecraft.world.item.ItemStack.CODEC, Items.SPRUCE_DOOR.getDefaultInstance());
//        out.buildResult();
    }

    @Override
    public void dropFallingContent(@NotNull FallingBlock fallingBlock) {
        CraftFallingBlock craftBlock = (CraftFallingBlock) fallingBlock;
        FallingBlockEntity nmsBlock = craftBlock.getHandle();

        nmsBlock.spawnAtLocation((ServerLevel) nmsBlock.level(), nmsBlock.getBlockState().getBlock());
    }

    @NotNull
    public Object fineChatPacket(@NotNull Object packet) {
        ClientboundPlayerChatPacket chatPacket = (ClientboundPlayerChatPacket) packet;
        Component component = chatPacket.unsignedContent() == null ? Component.literal(chatPacket.body().content()) : chatPacket.unsignedContent();

        Holder<ChatType> typeHolder = chatPacket.chatType().chatType();

        ChatType.Bound decorator = new ChatType.Bound(typeHolder, chatPacket.chatType().name(), chatPacket.chatType().targetName());
        component = decorator.decorate(component);

        return new ClientboundSystemChatPacket(component, false);
    }

    @Override
    @NotNull
    public org.bukkit.entity.Player loadPlayerData(@NotNull UUID id, @NotNull String name) {
        CraftServer craftServer = (CraftServer) Bukkit.getServer();
        DedicatedServer server = craftServer.getServer();
        DedicatedPlayerList playerList = craftServer.getHandle();
        ServerLevel level = server.getLevel(Level.OVERWORLD);
        if (level == null) throw new IllegalStateException("Server level is null");

        GameProfile profile = new GameProfile(id, name);
        ServerPlayer serverPlayer = new ServerPlayer(server, level, profile, ClientInformation.createDefault()); // GameMode reset

        ProblemReporter reporter = new ProblemReporter.Collector();
        RegistryAccess access = serverPlayer.registryAccess();
        CompoundTag emptyTag = new CompoundTag();

        ValueInput input = playerList.playerIo.load(serverPlayer, reporter).orElse(TagValueInput.create(reporter, access, emptyTag));
        serverPlayer.loadGameTypes(input); // Save GameMode on load data

        return serverPlayer.getBukkitEntity();
    }

    @Override
    public void setGameMode(@NotNull Player player, @NotNull GameMode mode) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        //serverPlayer.gameMode.changeGameModeForPlayer(GameType.byName(mode.name().toLowerCase()));
        //craftPlayer.saveData();

        GameType gameType = GameType.byName(mode.name().toLowerCase());
        GameType previous = serverPlayer.gameMode.getPreviousGameModeForPlayer();

        Reflex.invokeMethod(SET_GAME_MODE, serverPlayer.gameMode, gameType, previous);
        craftPlayer.saveData();
    }

    @Override
    public void teleport(@NotNull Player player, @NotNull Location location) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.setPosRaw(location.getX(), location.getY(), location.getZ());
        if (player.getWorld() != location.getWorld() && location.getWorld() != null) {
            CraftWorld craftWorld = (CraftWorld) location.getWorld();
            //serverPlayer.level = craftWorld.getHandle();
            serverPlayer.setServerLevel(craftWorld.getHandle());
        }
        craftPlayer.saveData();
    }

    @Override
    @NotNull
    public Inventory getPlayerEnderChest(@NotNull Player player) {
        return new PlayerEnderChest((CraftPlayer) player).getInventory();
    }

    @Override
    @NotNull
    public Inventory getPlayerInventory(@NotNull Player player) {
        return new PlayerInventory((CraftPlayer) player).getInventory();
    }

    @Override
    public void openPlayerInventory(@NotNull Player player, @NotNull Player owner) {
        player.openInventory(this.getPlayerInventory(owner));
    }

    @Override
    public void openContainer(@NotNull Player player, @NotNull MenuType menuType) {
        AbstractContainerMenu menu = this.createContainer(menuType, player);

        player.openInventory(menu.getBukkitView());
    }

    @NotNull
    private AbstractContainerMenu createContainer(@NotNull MenuType type, @NotNull Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer nmsPlayer = craftPlayer.getHandle();
        int contId = nmsPlayer.nextContainerCounter();
        ContainerLevelAccess access = ContainerLevelAccess.create(nmsPlayer.level(), nmsPlayer.blockPosition());
        net.minecraft.world.entity.player.Inventory inventory = nmsPlayer.getInventory();

        AbstractContainerMenu menu = switch (type) {
            case ANVIL -> new AnvilMenu(contId, inventory, access);
            case CRAFTING -> new CraftingMenu(contId, inventory, access);
            case ENCHANTMENT -> new EnchantmentMenu(contId, inventory, access);
            case LOOM -> new LoomMenu(contId, inventory, access);
            case SMITHING -> new SmithingMenu(contId, inventory, access);
            case GRINDSTONE -> new GrindstoneMenu(contId, inventory, access);
            case STONECUTTER -> new StonecutterMenu(contId, inventory, access);
            case CARTOGRAPHY -> new CartographyTableMenu(contId, inventory, access);
        };
        menu.checkReachable = false;

        return menu;
    }
}
