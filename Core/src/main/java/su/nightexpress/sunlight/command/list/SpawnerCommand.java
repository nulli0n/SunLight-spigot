package su.nightexpress.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.List;

// TODO Spawners module
@Deprecated
public class SpawnerCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "spawner";

    public SpawnerCommand(@NotNull SunLight plugin, @NotNull String[] aliases) {
        super(plugin, aliases, Perms.CMD_SPAWNER);
    }

    @Override
    @NotNull
    public String getUsage() {
        return plugin.getMessage(Lang.Command_Spawner_Usage).getLocalized();
    }

    @Override
    @NotNull
    public String getDescription() {
        return plugin.getMessage(Lang.Command_Spawner_Desc).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int i, @NotNull String[] args) {
        if (i == 1) {
            return SunUtils.ENTITY_TYPES.stream()
                .filter(type -> player.hasPermission(Perms.CMD_SPAWNER + "." + type)).toList();
        }
        return super.getTab(player, i, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 1) {
            this.printUsage(sender);
            return;
        }

        Player player = (Player) sender;
        Block block = player.getTargetBlock(null, 100);
        if (block.getType() != Material.SPAWNER) {
            plugin.getMessage(Lang.Command_Spawner_Error_Block).send(sender);
            return;
        }

        EntityType entityType = StringUtil.getEnum(result.getArg(0), EntityType.class).orElse(null);
        if (entityType == null || !entityType.isSpawnable()) {
            plugin.getMessage(Lang.Command_Spawner_Error_Type).send(sender);
            return;
        }

        if (!sender.hasPermission(Perms.CMD_SPAWNER + "." + entityType.name().toLowerCase())) {
            this.errorPermission(sender);
            return;
        }

        BlockState state = block.getState();
        CreatureSpawner spawner = (CreatureSpawner) state;
        try {
            spawner.setSpawnedType(entityType);
        } catch (IllegalArgumentException ex) {
            plugin.getMessage(Lang.Command_Spawner_Error_Type).send(sender);
            return;
        }
        state.update(true);

        plugin.getMessage(Lang.Command_Spawner_Done)
            .replace("%type%", plugin.getLangManager().getEnum(entityType))
            .send(sender);
    }
}
