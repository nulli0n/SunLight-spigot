package su.nightexpress.sunlight.module.inventories.command;

import org.bukkit.Sound;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.EnumLocale;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.placeholder.CommonPlaceholders;
import su.nightexpress.nightcore.util.sound.VanillaSound;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.PortableContainer;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.inventories.InventoriesModule;
import su.nightexpress.sunlight.module.inventories.InventoriesPerms;
import su.nightexpress.sunlight.nms.SunNMS;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;
import static su.nightexpress.sunlight.SLPlaceholders.GENERIC_TYPE;
import static su.nightexpress.sunlight.SLPlaceholders.PLAYER_DISPLAY_NAME;

public class ContainerCommandProvider extends AbstractCommandProvider {

    private static final Permission PERMISSION_ROOT        = InventoriesPerms.COMMAND.permission("container.root");
    private static final Permission PERMISSION_OTHERS      = InventoriesPerms.COMMAND.permission("container.others");
    private static final Permission PERMISSION_ANVIL       = InventoriesPerms.COMMAND.permission("container.anvil");
    private static final Permission PERMISSION_LOOM        = InventoriesPerms.COMMAND.permission("container.loom");
    private static final Permission PERMISSION_WORKBENCH   = InventoriesPerms.COMMAND.permission("container.workbench");
    private static final Permission PERMISSION_SMITHING    = InventoriesPerms.COMMAND.permission("container.smithing_table");
    private static final Permission PERMISSION_GRINDSTONE  = InventoriesPerms.COMMAND.permission("container.grindstone");
    private static final Permission PERMISSION_CARTOGRAPHY = InventoriesPerms.COMMAND.permission("container.cartography_table");
    private static final Permission PERMISSION_ENCHANTING  = InventoriesPerms.COMMAND.permission("container.enchanting_table");
    private static final Permission PERMISSION_STONECUTTER = InventoriesPerms.COMMAND.permission("container.stonecutter");

    private static final TextLocale DESCRIPTION_ROOT = LangEntry.builder("Command.Container.Root.Desc").text("Portable Container commands.");
    private static final TextLocale DESCRIPTION_TYPE = LangEntry.builder("Command.Container.Type.Desc").text("Opens Portable " + GENERIC_TYPE + ".");

    private static final MessageLocale MESSAGE_NOTIFY = LangEntry.builder("Command.Container.Notify").chatMessage(
        GRAY.wrap("You have opened " + SOFT_AQUA.wrap("Portable " + GENERIC_TYPE + "."))
    );

    private static final MessageLocale MESSAGE_TARGETTED = LangEntry.builder("Command.Container.Target").chatMessage(
        GRAY.wrap("You have opened " + SOFT_YELLOW.wrap("Portable " + GENERIC_TYPE) + " for " + SOFT_YELLOW.wrap(PLAYER_DISPLAY_NAME + "."))
    );

    private static final EnumLocale<PortableContainer> CONTAINER_LOCALE = LangEntry.builder("MenuType").enumeration(PortableContainer.class);

    private final InventoriesModule module;
    private final SunNMS nms;

    public ContainerCommandProvider(@NotNull SunLightPlugin plugin, @NotNull InventoriesModule module, @NotNull SunNMS nms) {
        super(plugin);
        this.module = module;
        this.nms = nms;
    }

    @NotNull
    private Permission getPermission(@NotNull PortableContainer container) {
        return switch (container) {
            case ANVIL -> PERMISSION_ANVIL;
            case LOOM -> PERMISSION_LOOM;
            case WORKBENCH -> PERMISSION_WORKBENCH;
            case SMITHING_TABLE -> PERMISSION_SMITHING;
            case GRINDSTONE -> PERMISSION_GRINDSTONE;
            case CARTOGRAPHY_TABLE -> PERMISSION_CARTOGRAPHY;
            case ENCHANTING_TABLE -> PERMISSION_ENCHANTING;
            case STONECUTTER -> PERMISSION_STONECUTTER;
        };
    }

    @NotNull
    private Sound getSound(@NotNull PortableContainer container) {
        return switch (container) {
            case ANVIL -> Sound.BLOCK_ANVIL_PLACE;
            case LOOM -> Sound.UI_LOOM_SELECT_PATTERN;
            case WORKBENCH -> Sound.BLOCK_WOOD_PLACE;
            case SMITHING_TABLE -> Sound.BLOCK_SMITHING_TABLE_USE;
            case GRINDSTONE -> Sound.BLOCK_GRINDSTONE_USE;
            case CARTOGRAPHY_TABLE -> Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT;
            case ENCHANTING_TABLE -> Sound.BLOCK_ENCHANTMENT_TABLE_USE;
            case STONECUTTER -> Sound.UI_STONECUTTER_TAKE_RESULT;
        };
    }

    @Override
    public void registerDefaults() {
        Map<String, String> childrens = new HashMap<>();

        Stream.of(PortableContainer.values()).forEach(container -> {
            Permission permission = this.getPermission(container);
            String nodeId = LowerCase.INTERNAL.apply(container.name());
            String alias = nodeId.replace("_", "");

            this.registerLiteral(nodeId, true, new String[]{alias}, builder -> builder
                .description(DESCRIPTION_TYPE.text().replace(SLPlaceholders.GENERIC_TYPE, CONTAINER_LOCALE.getLocalized(container)))
                .permission(permission)
                .withArguments(Arguments.playerName(CommandArguments.PLAYER).permission(PERMISSION_OTHERS).optional())
                .withFlags(CommandArguments.FLAG_SILENT)
                .executes((context, arguments) -> this.open(context, arguments, container))
            );

            childrens.put(nodeId, alias);
        });

        this.registerRoot("Container", true, new String[]{"container"}, childrens, builder -> builder
            .description(DESCRIPTION_ROOT)
            .permission(PERMISSION_ROOT)
        );
    }

    private boolean open(@NotNull CommandContext context, @NotNull ParsedArguments arguments, @NotNull PortableContainer container) {
        return this.runForOnlinePlayerOrSender(context, arguments, this.module, target -> {
            this.nms.openContainer(target, container);
            VanillaSound.of(this.getSound(container)).play(target);

            if (context.getSender() != target) {
                this.module.sendPrefixed(MESSAGE_TARGETTED, context.getSender(), builder -> builder
                    .with(CommonPlaceholders.PLAYER.resolver(target))
                    .with(SLPlaceholders.GENERIC_TYPE, () -> CONTAINER_LOCALE.getLocalized(container))
                );
            }

            if (!context.hasFlag(CommandArguments.FLAG_SILENT)) {
                this.module.sendPrefixed(MESSAGE_NOTIFY, target, builder -> builder
                    .with(SLPlaceholders.GENERIC_TYPE, () -> CONTAINER_LOCALE.getLocalized(container))
                );
            }

            return true;
        });
    }
}
