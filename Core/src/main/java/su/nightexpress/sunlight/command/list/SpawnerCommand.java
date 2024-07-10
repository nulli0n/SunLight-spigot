package su.nightexpress.sunlight.command.list;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.command.experimental.CommandContext;
import su.nightexpress.nightcore.command.experimental.argument.ParsedArguments;
import su.nightexpress.nightcore.command.experimental.builder.DirectNodeBuilder;
import su.nightexpress.nightcore.command.experimental.node.DirectNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.CommandPerms;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.config.Lang;

public class SpawnerCommand {

    public static final String NODE = "spawner";

    public static void load(@NotNull SunLightPlugin plugin) {
        CommandRegistry.registerDirectExecutor(NODE, (template, config) -> builder(plugin, template, config));

        CommandRegistry.addSimpleTemplate(NODE);
    }

    @NotNull
    public static DirectNodeBuilder builder(@NotNull SunLightPlugin plugin, @NotNull CommandTemplate template, @NotNull FileConfig config) {
        return DirectNode.builder(plugin, template.getAliases())
            .playerOnly()
            .description(Lang.COMMAND_SPAWNER_DESC)
            .permission(CommandPerms.SPAWNER)
            .withArgument(CommandArguments.entityType(CommandArguments.TYPE, EntityType::isSpawnable).required())
            .executes((context, arguments) -> execute(plugin, context, arguments))
            ;
    }

    public static boolean execute(@NotNull SunLightPlugin plugin, @NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();

        Block block = player.getTargetBlock(null, 100);
        if (block.getType() != Material.SPAWNER) {
            context.send(Lang.COMMAND_SPAWNER_ERROR_BLOCK.getMessage());
            return false;
        }

        EntityType entityType = arguments.getArgument(CommandArguments.TYPE, EntityType.class);
        if (!player.hasPermission(CommandPerms.SPAWNER_TYPE.apply(entityType))) {
            context.errorPermission();
            return false;
        }

        BlockState state = block.getState();
        CreatureSpawner spawner = (CreatureSpawner) state;
        try {
            spawner.setSpawnedType(entityType);
            spawner.update(true);
        }
        catch (IllegalArgumentException ex) {
            context.send(Lang.COMMAND_SPAWNER_ERROR_TYPE.getMessage());
            return false;
        }

        context.send(Lang.COMMAND_SPAWNER_DONE.getMessage().replace(Placeholders.GENERIC_TYPE, LangAssets.get(entityType)));
        return true;
    }
}
