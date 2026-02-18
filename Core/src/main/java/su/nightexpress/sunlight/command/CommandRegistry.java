package su.nightexpress.sunlight.command;

import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.NodeExecutor;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.commands.tree.LiteralNode;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.SimpleManager;
import su.nightexpress.nightcore.util.CommandUtil;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.TimeUtil;
import su.nightexpress.nightcore.util.time.TimeFormatType;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SLFiles;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.CommandProvider;
import su.nightexpress.sunlight.command.provider.definition.HubDefinition;
import su.nightexpress.sunlight.command.provider.definition.LiteralDefinition;
import su.nightexpress.sunlight.config.Lang;
import su.nightexpress.sunlight.config.Perms;
import su.nightexpress.sunlight.user.SunUser;

import java.util.*;

public class CommandRegistry extends SimpleManager<SunLightPlugin> {

    private final CommandSettings settings;

    private final Map<String, CommandProvider> providers;
    private final Set<NightCommand>            commands;

    public CommandRegistry(@NotNull SunLightPlugin plugin) {
        super(plugin);
        this.settings = new CommandSettings();
        this.providers = new LinkedHashMap<>();
        this.commands = new HashSet<>();
    }

    @Override
    protected void onLoad() {
        this.settings.load(this.plugin.getConfig());
        this.registerCommands();
    }

    @Override
    protected void onShutdown() {
        this.commands.forEach(NightCommand::unregister);
        this.commands.clear();
        this.providers.clear();
    }

    public void addProvider(@NotNull String id, @NotNull CommandProvider provider) {
        this.providers.put(LowerCase.INTERNAL.apply(id), provider);
    }

    private void registerCommands() {
        this.providers.forEach((providerId, provider) -> {
            FileConfig config = FileConfig.load(this.plugin.getDataFolder() + SLFiles.DIR_COMMANDS, FileConfig.withExtension(providerId));

            this.plugin.injectLang(provider); // Register and load command's locales.

            provider.registerDefaults();
            provider.load(config);

            provider.getLiteralBuilders().forEach((nodeId, consumer) -> {
                LiteralDefinition literalDefinition = provider.getLiteralDefinitions().get(nodeId);
                if (literalDefinition == null || !literalDefinition.enabled()) return;

                this.register(NightCommand.literal(this.plugin, literalDefinition.aliases(), builder -> {
                    consumer.accept(builder);

                    if (this.settings.isCooldownsEnabled()) {
                        this.wrapExecutorWithCooldown(providerId, nodeId, literalDefinition, builder);
                    }
                }));
            });


            provider.getRootBuilders().forEach((rootId, rootBuilder) -> {
                HubDefinition rootDefinition = provider.getRootDefinitions().get(rootId);
                if (rootDefinition == null || !rootDefinition.enabled()) return;

                List<LiteralNode> childrens = new ArrayList<>();

                provider.getLiteralBuilders().forEach((nodeId, consumer) -> {
                    String alias = rootDefinition.childrenAliases().get(nodeId);
                    if (alias == null || alias.isBlank()) return;

                    childrens.add(Commands.literal(alias, consumer));
                });

                if (childrens.isEmpty()) {
                    this.plugin.warn("Root command '" + rootDefinition.name() + "' was not registered due to no sub-commands available.");
                    return;
                }

                this.register(NightCommand.hub(this.plugin, rootDefinition.aliases(), builder -> {
                    rootBuilder.accept(builder);
                    builder.localized(rootDefinition.name());
                    builder.branch(childrens.toArray(new LiteralNode[0]));
                }));
            });

            config.saveChanges();
        });
    }

    private void register(@NotNull NightCommand command) {
        if (this.settings.isConflictUnregisterEnabled()) {
            this.unregisterConflicts(command);
        }

        if (!command.register()) {
            this.plugin.warn("Command '%s' was not registered with the passed in label, which indicates the SunLight's fallback prefix was used one or more time. This usually means that there is a vanilla command with the same label.");
        }

        this.commands.add(command);
    }

    @NotNull
    public Set<CommandProvider> getProviders() {
        return new HashSet<>(this.providers.values());
    }

    private void wrapExecutorWithCooldown(@NotNull String providerId, @NotNull String nodeId, @NotNull LiteralDefinition definition, @NotNull LiteralNodeBuilder builder) {
        NodeExecutor executor = builder.getExecutor(); // Original executor set by the provider implementation.

        int cooldown = definition.cooldown();

        builder.executes((context, arguments) -> {
            Player player = context.getPlayer();
            SunUser user = player == null ? null : this.plugin.getUserManager().getOrFetch(player);
            CommandKey key = new CommandKey(providerId, nodeId);

            if (cooldown != 0 && user != null && !player.hasPermission(Perms.BYPASS_COMMAND_COOLDOWN)) {
                Long expireDate = user.getCommandCooldown(key);
                if (expireDate != null && !TimeUtil.isPassed(expireDate)) {
                    (expireDate < 0 ? Lang.COMMAND_COOLDOWN_ONE_TIME : Lang.COMMAND_COOLDOWN_DEFAULT).message().sendWith(player, replacer -> replacer
                        .with(SLPlaceholders.GENERIC_TIME, () -> TimeFormats.formatDuration(expireDate, TimeFormatType.LITERAL))
                        .with(SLPlaceholders.GENERIC_COMMAND, () -> "/" + context.getInput())
                    );
                    return false;
                }
            }

            boolean result = executor.run(context, arguments);
            if (result && cooldown != 0 && user != null) {
                long expireDate = TimeUtil.createFutureTimestamp(cooldown);

                user.setCommandCooldown(key, expireDate);
                user.markDirty();
            }

            return result;
        });
    }

    private void unregisterConflicts(@NotNull NightCommand command) {
        Set<String> aliases = new HashSet<>(command.getAliases());
        aliases.add(command.getName());

        aliases.forEach(alias -> {
            CommandUtil.getCommand(alias).ifPresent(other -> {
                boolean result = CommandUtil.unregister(other);
                String owner = getCommandOwner(other);
                if (this.settings.getConflictUnregisterBlacklist().contains(LowerCase.INTERNAL.apply(owner))) return;

                if (result) {
                    this.plugin.info("Unregistered conflicting '%s' (%s) command in favor of SunLight's alternative.".formatted(other.getName(), owner));
                }
                else {
                    this.plugin.warn("Could not unregister conflicting command '%s' (%s) in favor of SunLight's alternative.".formatted(other.getName(), owner));
                }
            });
        });
    }

    @NotNull
    private static String getCommandOwner(@NotNull Command command) {
        if (command instanceof PluginIdentifiableCommand identifiableCommand) {
            return identifiableCommand.getPlugin().getName();
        }

        if (command instanceof BukkitCommand bukkitCommand) {
            String permission = bukkitCommand.getPermission();
            if (permission != null && permission.startsWith("minecraft")) {
                return "Vanilla";
            }

            return "Bukkit";
        }

        return "Unknown";
    }
}
