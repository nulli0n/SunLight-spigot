package su.nightexpress.sunlight.command.enderchest;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.data.impl.SunUser;
import su.nightexpress.sunlight.utils.SunUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnderchestFillCommand extends AbstractCommand<SunLight> {

    public static final String NAME = "fill";

    public EnderchestFillCommand(@NotNull SunLight plugin) {
        super(plugin, new String[]{NAME}, Perms.COMMAND_ENDERCHEST_FILL);
        this.setUsage(plugin.getMessage(Lang.COMMAND_ENDERCHEST_FILL_USAGE));
        this.setDescription(plugin.getMessage(Lang.COMMAND_ENDERCHEST_FILL_DESC));
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg >= 2) {
            return SunUtils.ITEM_TYPES;
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 3) {
            this.printUsage(sender);
            return;
        }

        Player pTarget = plugin.getServer().getPlayer(result.getArg(1));
        SunUser userTarget = pTarget == null ? plugin.getUserManager().getUserData(result.getArg(1)) : null;
        if (pTarget == null && userTarget == null) {
            this.errorPlayer(sender);
            return;
        }

        List<Material> materials = Stream.of(result.getArgs()).skip(2).limit(27)
            .map(String::toUpperCase)
            .map(Material::getMaterial).filter(Objects::nonNull).toList();
        if (materials.isEmpty()) {
            plugin.getMessage(Lang.ERROR_MATERIAL_INVALID).send(sender);
            return;
        }

        int invSize = 27;
        int matSize = materials.size();
        int[] slotsPerItem = NumberUtil.splitIntoParts(invSize, matSize);
        int indexSlot = 0;
        List<ItemStack> content = new ArrayList<>();

        for (int indexItem = 0; indexItem < matSize; indexItem++) {
            int slotsAmount = slotsPerItem[indexItem];
            ItemStack item = new ItemStack(materials.get(indexItem));
            for (int slot = 0; slot < slotsAmount; slot++) {
                content.add(item);
            }
        }

        Collections.shuffle(content);

        if (pTarget == null) {
            pTarget = plugin.getSunNMS().loadPlayerData(userTarget.getId(), userTarget.getName());
        }
        plugin.getSunNMS().getPlayerEnderChest(pTarget).setContents(content.toArray(new ItemStack[0]));
        pTarget.saveData();

        String itemName = materials.stream().map(LangManager::getMaterial).collect(Collectors.joining(", "));

        plugin.getMessage(Lang.COMMAND_ENDERCHEST_FILL_DONE_EXECUTOR)
            .replace(Placeholders.Player.replacer(pTarget))
            .replace(Placeholders.GENERIC_ITEM, itemName)
            .send(sender);
    }
}
