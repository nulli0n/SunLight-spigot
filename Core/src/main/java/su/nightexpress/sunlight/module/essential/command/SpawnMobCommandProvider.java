package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.nightcore.util.LocationUtil;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialPerms;

import java.util.stream.IntStream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SOFT_YELLOW;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_AMOUNT;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TYPE;

@Deprecated
public class SpawnMobCommandProvider extends AbstractCommandProvider {

    public static final Permission PERMISSION = EssentialPerms.COMMAND.permission("spawnmob");

    public static final TextLocale DESCRIPTION = LangEntry.builder("Command.Mob.Spawn.Desc").text("Spawn a mob.");

    public static final MessageLocale MESSAGE_SPAWNED_FEEDBACK = LangEntry.builder("Command.Mob.Spawn.Done").chatMessage(
        GRAY.wrap("You have spawned " + SOFT_YELLOW.wrap(GENERIC_AMOUNT + "x " + GENERIC_TYPE) + ".")
    );

    private int spawnLimit;

    public SpawnMobCommandProvider(@NotNull SunLightPlugin plugin) {
        super(plugin);
    }

    /*@Override
    protected void loadSettings(@NotNull FileConfig config, @NotNull String path) {
        this.spawnLimit = Math.max(1, config.get(ConfigTypes.INT, path + ".SpawnLimit", 10,
            "Sets max. allowed mobs amount value per command execution."
        ));
    }*/

    @Override
    public void registerDefaults() {
        this.registerLiteral("spawnmob", true, new String[]{"spawnmob"}, builder -> builder
            .playerOnly()
            .description(DESCRIPTION)
            .permission(PERMISSION)
            .withArguments(
                CommandArguments.entityType(EntityType::isSpawnable),
                Arguments.integer(CommandArguments.AMOUNT, 1)
                    .optional()
                    .localized(CoreLang.COMMAND_ARGUMENT_NAME_AMOUNT.text())
                    .suggestions((reader, tabContext) -> IntStream.range(1, this.spawnLimit).boxed().map(String::valueOf).toList())
            )
            .executes(this::spawnMob)
        );
    }

    private boolean spawnMob(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        EntityType entityType = arguments.get(CommandArguments.TYPE, EntityType.class);

        int amount = Math.min(this.spawnLimit, arguments.getInt(CommandArguments.AMOUNT, 1));

        Location location = LocationUtil.setCenter2D(player.getTargetBlock(null, 100).getRelative(BlockFace.UP).getLocation());
        for (int count = 0; count < amount; count++) {
            player.getWorld().spawnEntity(location, entityType);
        }

        context.send(MESSAGE_SPAWNED_FEEDBACK, replacer -> replacer
            .replace(SLPlaceholders.GENERIC_AMOUNT, String.valueOf(amount))
            .replace(SLPlaceholders.GENERIC_TYPE, LangUtil.getSerializedName(entityType))
        );
        return true;
    }
}
