package su.nightexpress.sunlight.command.mob;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandFlag;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.LocationUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MobSpawnCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "spawn";

    private static final CommandFlag<String> FLAG_NAME = CommandFlag.stringFlag("name");

    private static final Set<EntityType> ALLOWED_VALUES = Stream.of(EntityType.values())
        .filter(EntityType::isAlive).filter(EntityType::isSpawnable).collect(Collectors.toSet());

    public MobSpawnCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_MOB_SPAWN);
        this.setDescription(plugin.getMessage(Lang.COMMAND_MOB_SPAWN_DESC));
        this.setUsage(plugin.getMessage(Lang.COMMAND_MOB_SPAWN_USAGE));
        this.setPlayerOnly(true);
        this.addFlag(FLAG_NAME);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return ALLOWED_VALUES.stream().map(Enum::name).map(String::toLowerCase).toList();
        }
        if (arg == 2) {
            return Arrays.asList("1", "2", "3", "5", "10");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        EntityType entityType = StringUtil.getEnum(result.getArg(1), EntityType.class).orElse(null);
        if (entityType == null || !ALLOWED_VALUES.contains(entityType)) {
            this.plugin.getMessage(Lang.COMMAND_MOB_SPAWN_ERROR_TYPE).send(sender);
            return;
        }

        int amount = result.getInt(2, 1);
        String name = result.getFlag(FLAG_NAME);

        Player player = (Player) sender;
        Location location = LocationUtil.getCenter(player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation());
        for (int count = 0; count < amount; count++) {
            Entity entity = player.getWorld().spawnEntity(location, entityType);
            if (name != null) {
                entity.setCustomName(Colorizer.apply(name));
                entity.setCustomNameVisible(true);
            }
        }

        plugin.getMessage(Lang.COMMAND_MOB_SPAWN_DONE)
            .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amount))
            .replace(Placeholders.GENERIC_TYPE, LangManager.getEntityType(entityType))
            .send(sender);
    }
}
