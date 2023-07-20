package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.command.CommandRegister;
import su.nexmedia.engine.utils.FileUtil;
import su.nexmedia.engine.utils.Pair;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.command.enderchest.EnderchestCommand;
import su.nightexpress.sunlight.command.ignore.IgnoreCommand;
import su.nightexpress.sunlight.command.inventory.InventoryCommand;
import su.nightexpress.sunlight.command.item.ItemCommand;
import su.nightexpress.sunlight.command.list.*;
import su.nightexpress.sunlight.command.mob.MobCommand;
import su.nightexpress.sunlight.command.nick.NickCommand;
import su.nightexpress.sunlight.command.teleport.TeleportCommand;
import su.nightexpress.sunlight.command.time.TimeCommand;
import su.nightexpress.sunlight.utils.Cleanable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CommandRegulator extends AbstractManager<SunLight> {

    private static final String FILE = "commands.yml";

    private final JYML config;

    public CommandRegulator(@NotNull SunLight plugin) {
        super(plugin);
        this.config = JYML.loadOrExtract(plugin, FILE);
    }

    @Override
    public void onLoad() {
        this.config.reload();
        this.config.initializeOptions(CommandConfig.class);

        this.register(AnvilCommand.NAME, (cfg, aliases) -> new AnvilCommand(plugin, aliases));
        this.register(AirCommand.NAME, (cfg, aliases) -> new AirCommand(plugin, aliases));
        this.register(BackCommand.NAME, (cfg, aliases) -> new BackCommand(plugin, cfg, aliases));
        this.register(CondenseCommand.NAME, (cfg, aliases) -> new CondenseCommand(plugin, aliases));
        this.register(DeathBackCommand.NAME, (cfg, aliases) -> new DeathBackCommand(plugin, cfg, aliases), "dback");
        this.register(DisposalCommand.NAME, (cfg, aliases) -> new DisposalCommand(plugin, cfg, aliases));
        this.register(DimensionCommand.NAME, ((cfg, aliases) -> new DimensionCommand(plugin, aliases)), "dim");
        this.register(EnchantCommand.NAME, (cfg, aliases) -> new EnchantCommand(plugin, aliases));
        this.register(EnchantingCommand.NAME, (cfg, aliases) -> new EnchantingCommand(plugin, aliases));
        this.register(EnderchestCommand.NAME, (cfg, aliases) -> new EnderchestCommand(plugin, aliases), "echest");
        this.register(ExpCommand.NAME, (cfg, aliases) -> new ExpCommand(plugin, aliases), "xp");
        this.register(ExtinguishCommand.NAME, (cfg, aliases) -> new ExtinguishCommand(plugin, aliases), "ext");
        this.register(EquipCommand.NAME, (cfg, aliases) -> new EquipCommand(plugin, aliases));
        this.register(FeedCommand.NAME, (cfg, aliases) -> new FeedCommand(plugin, aliases));
        this.register(FireCommand.NAME, (cfg, aliases) -> new FireCommand(plugin, aliases));
        this.register(FlyCommand.NAME, (cfg, aliases) -> new FlyCommand(plugin, aliases));
        this.register(FoodCommand.NAME, (cfg, aliases) -> new FoodCommand(plugin, aliases));
        this.register(FoodGodCommand.NAME, (cfg, aliases) -> new FoodGodCommand(plugin, aliases));
        this.register(GamemodeCommand.NAME, (cfg, aliases) -> new GamemodeCommand(plugin, aliases), "gm");
        this.register(GodCommand.NAME, (cfg, aliases) -> new GodCommand(plugin, cfg, aliases));
        this.register(HealCommand.NAME, (cfg, aliases) -> new HealCommand(plugin, aliases));
        this.register(IgnoreCommand.NAME, (cfg, aliases) -> new IgnoreCommand(plugin, aliases));
        this.register(InventoryCommand.NAME, (cfg, aliases) -> new InventoryCommand(plugin, aliases), "inv");
        this.register(ItemCommand.NAME, (cfg, aliases) -> new ItemCommand(plugin, cfg, aliases), "i");
        this.register(MobCommand.NAME, (cfg, aliases) -> new MobCommand(plugin, aliases));
        this.register(NearCommand.NAME, (cfg, aliases) -> new NearCommand(plugin, cfg, aliases));
        this.register(NickCommand.NAME, (cfg, aliases) -> new NickCommand(plugin, cfg, aliases));
        this.register(NoMobTargetCommand.NAME, (cfg, aliases) -> new NoMobTargetCommand(plugin, aliases));
        this.register(NoPhantomCommand.NAME, (cfg, aliases) -> new NoPhantomCommand(plugin, aliases));
        this.register(PlayerInfoCommand.NAME, (cfg, aliases) -> new PlayerInfoCommand(plugin, cfg, aliases), "pinfo");
        this.register(PlayerListCommand.NAME, (cfg, aliases) -> new PlayerListCommand(plugin, cfg, aliases), "plist");
        this.register(SkullCommand.NAME, (cfg, aliases) -> new SkullCommand(plugin, aliases));
        this.register(SmiteCommand.NAME, (cfg, aliases) -> new SmiteCommand(plugin, aliases));
        this.register(SpawnerCommand.NAME, (cfg, aliases) -> new SpawnerCommand(plugin, aliases));
        this.register(SpeedCommand.NAME, (cfg, aliases) -> new SpeedCommand(plugin, aliases));
        this.register(SudoCommand.NAME, (cfg, aliases) -> new SudoCommand(plugin, aliases));
        this.register(SuicideCommand.NAME, (cfg, aliases) -> new SuicideCommand(plugin, aliases));
        this.register(TimeCommand.NAME, (cfg, aliases) -> new TimeCommand(plugin, cfg, aliases));
        this.register(TeleportCommand.NAME, (cfg, aliases) -> new TeleportCommand(plugin, cfg, aliases), "tp");
        this.register(VanishCommand.NAME, (cfg, aliases) -> new VanishCommand(plugin, aliases));
        this.register(WeatherCommand.NAME, (cfg, aliases) -> new WeatherCommand(plugin, aliases));
        this.register(WorkbenchCommand.NAME, (cfg, aliases) -> new WorkbenchCommand(plugin, aliases));

        for (File file : FileUtil.getFiles(plugin.getDataFolder() + "/custom_text/", true)) {
            CustomTextCommand cmdText = new CustomTextCommand(plugin, file);
            this.plugin.getCommandManager().registerCommand(cmdText);
        }
        this.config.saveChanges();
    }

    @Override
    public void onShutdown() {
        new HashSet<>(this.plugin.getCommandManager().getCommands()).forEach(command -> {
            if (command instanceof Cleanable cleanable) {
                cleanable.clear();
            }
            this.plugin.getCommandManager().unregisterCommand(command);
        });
    }

    public void register(@NotNull String name, @NotNull CommandSupplier commandSupplier, @NotNull String... extraAliases) {
        // Get all command default aliases.
        List<String> allAliases = new ArrayList<>(Arrays.asList(extraAliases));
        allAliases.add(name);

        // Add to the config if not present.
        this.config.addMissing("Aliases." + name, String.join(",", allAliases));

        // Get custom user aliases.
        //String allAliases = extraAliases.length == 0 ? name : name + "," + String.join(",", extraAliases);
        String[] aliases = this.config.getString("Aliases." + name, name).split(",");
        if (aliases.length == 0) {
            this.plugin.error("Could not register '" + name + "' command! (no aliases set)");
            return;
        }

        // Check if command is disabled.
        if (CommandConfig.DISABLED.get().stream().anyMatch(allAliases::contains)) {
            return;
        }

        // Initialize command.
        GeneralCommand<SunLight> command = commandSupplier.supply(this.config, aliases);
        if (CommandConfig.UNREGISTER_CONFLICTS.get()) {
            CommandRegister.unregister(command.getAliases()[0]);
        }
        plugin.getCommandManager().registerCommand(command);

        // Register custom user shortcuts
        List<Pair<String[], String[]>> list = CommandConfig.SHORTCUTS.get().get(name.toLowerCase());
        if (list == null || list.isEmpty()) return;

        list.forEach(pair -> {
            String[] shortcuts = pair.getFirst();
            String[] args = pair.getSecond();

            CommandShortcut shortcut = new CommandShortcut(plugin, shortcuts, command, args);
            plugin.getCommandManager().registerCommand(shortcut);

        });
        plugin.info("Registered " + list.size() + " shortcut(s) for the '" + name + "' command.");
    }
}
