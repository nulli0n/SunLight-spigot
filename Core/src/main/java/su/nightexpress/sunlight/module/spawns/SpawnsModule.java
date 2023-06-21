package su.nightexpress.sunlight.module.spawns;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.SunLight;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.spawns.command.SpawnsCommand;
import su.nightexpress.sunlight.module.spawns.config.SpawnsLang;
import su.nightexpress.sunlight.module.spawns.editor.EditorLocales;
import su.nightexpress.sunlight.module.spawns.editor.SpawnsEditor;
import su.nightexpress.sunlight.module.spawns.impl.Spawn;
import su.nightexpress.sunlight.module.spawns.listener.SpawnListener;
import su.nightexpress.sunlight.module.spawns.util.Placeholders;
import su.nightexpress.sunlight.module.spawns.util.SpawnsPerms;

import java.util.*;

public class SpawnsModule extends Module {

    public static final String DIR_SPAWNS = "/spawns/";

    private final Map<String, Spawn> spawns;

    private SpawnsEditor editor;

    public SpawnsModule(@NotNull SunLight plugin, @NotNull String id) {
        super(plugin, id);
        this.spawns = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.plugin.registerPermissions(SpawnsPerms.class);
        this.plugin.getLangManager().loadMissing(SpawnsLang.class);
        this.plugin.getLangManager().loadEditor(EditorLocales.class);
        this.plugin.getLang().saveChanges();
        this.plugin.runTask(task -> this.loadSpawns()); // Do a tick delay for all the worlds to load.
        this.plugin.getCommandRegulator().register(SpawnsCommand.NAME, (cfg1, aliases) -> new SpawnsCommand(this, aliases));

        this.addListener(new SpawnListener(this));
    }

    private void loadSpawns() {
        for (JYML cfg : JYML.loadAll(this.getAbsolutePath() + DIR_SPAWNS, false)) {
            Spawn spawn = new Spawn(this, cfg);
            if (spawn.load()) {
                this.spawns.put(spawn.getId(), spawn);
            }
            else this.warn("Spawn not loaded: '" + cfg.getFile().getName() + "'!");
        }
        this.info("Loaded " + this.spawns.size() + " spawns!");
    }

    @Override
    protected void onShutdown() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        this.getSpawns().forEach(Spawn::clear);
        this.getSpawnsMap().clear();
    }

    @NotNull
    public SpawnsEditor getEditor() {
        if (this.editor == null) {
            this.editor = new SpawnsEditor(this);
        }
        return this.editor;
    }

    @NotNull
    public Map<String, Spawn> getSpawnsMap() {
        return this.spawns;
    }

    @NotNull
    public Collection<Spawn> getSpawns() {
        return this.getSpawnsMap().values();
    }

    @NotNull
    public List<String> getSpawnIds() {
        return new ArrayList<>(this.getSpawnsMap().keySet());
    }

    @NotNull
    public Optional<Spawn> getSpawnById(@NotNull String id) {
        return Optional.ofNullable(this.getSpawnsMap().get(id.toLowerCase()));
    }

    @NotNull
    public Optional<Spawn> getSpawnByDefault() {
        return this.getSpawns().stream().filter(Spawn::isDefault).findFirst();
    }

    @NotNull
    public Optional<Spawn> getSpawnByLogin(@NotNull Player player) {
        boolean hasPlayed = !plugin.getUserManager().getUserData(player).isRecentlyCreated();

        return this.getSpawns().stream()
            .filter(spawn -> {
                if (spawn.isFirstLoginTeleportEnabled() && !hasPlayed) return true;
                if (spawn.isLoginTeleportEnabled(player) && spawn.hasPermission(player)) return true;
                return false;
            })
            .max(Comparator.comparingInt(Spawn::getPriority));
    }

    @NotNull
    public Optional<Spawn> getSpawnByDeath(@NotNull Player player) {
        return this.getSpawns().stream()
            .filter(spawn -> spawn.isDeathTeleportEnabled(player) && spawn.hasPermission(player))
            .max(Comparator.comparingInt(Spawn::getPriority));
    }

    public boolean createSpawn(@NotNull Player player, @NotNull String id) {
        Location location = player.getLocation();
        id = StringUtil.lowerCaseUnderscore(id);

        Spawn spawn = this.getSpawnById(id).orElse(null);
        if (spawn == null) {
            JYML cfg = new JYML(this.getAbsolutePath() + DIR_SPAWNS, id + ".yml");
            spawn = new Spawn(this, cfg);
            spawn.setName(ChatColor.YELLOW + StringUtil.capitalizeUnderscored(id));
            if (this.getSpawns().isEmpty()) {
                spawn.setDefault(true);
                spawn.getLoginTeleportGroups().add(Placeholders.DEFAULT);
                spawn.setLoginTeleportEnabled(true);
                spawn.setFirstLoginTeleportEnabled(true);
            }
        }
        spawn.setLocation(location);
        spawn.save();

        this.getSpawnsMap().put(spawn.getId(), spawn);
        this.plugin.getMessage(SpawnsLang.COMMAND_SPAWNS_CREATE_DONE).replace(spawn.replacePlaceholders()).send(player);
        return true;
    }

    public void deleteSpawn(@NotNull Spawn spawn) {
        if (spawn.getFile().delete()) {
            spawn.clear();
            this.spawns.remove(spawn.getId());
        }
    }
}
