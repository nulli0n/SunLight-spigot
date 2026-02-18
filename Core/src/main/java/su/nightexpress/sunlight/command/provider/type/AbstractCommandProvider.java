package su.nightexpress.sunlight.command.provider.type;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import su.nightexpress.nightcore.commands.builder.HubNodeBuilder;
import su.nightexpress.nightcore.commands.builder.LiteralNodeBuilder;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.util.LowerCase;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.CommandProvider;
import su.nightexpress.sunlight.command.provider.definition.HubDefinition;
import su.nightexpress.sunlight.command.provider.definition.LiteralDefinition;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.user.SunUser;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractCommandProvider implements CommandProvider {

    protected final SunLightPlugin plugin;

    protected final Map<String, Consumer<LiteralNodeBuilder>> literalBuilders;
    protected final Map<String, Consumer<HubNodeBuilder>>     rootBuilder;

    protected final Map<String, LiteralDefinition> defaultLiterals;
    protected final Map<String, HubDefinition>     defaultRoot;

    protected final Map<String, LiteralDefinition> literals;
    protected final Map<String, HubDefinition>     root;

    public AbstractCommandProvider(@NonNull SunLightPlugin plugin) {
        this.plugin = plugin;
        this.literalBuilders = new HashMap<>();
        this.rootBuilder = new HashMap<>();
        this.defaultLiterals = new HashMap<>();
        this.defaultRoot = new HashMap<>();
        this.literals = new HashMap<>();
        this.root = new HashMap<>();
    }

    @Override
    public void load(@NonNull FileConfig config) {
        //this.loadSettings(config, "Settings");
        this.loadLiterals(config, "LiteralNodes");
        this.loadRoot(config, "RootNodes");
    }

    /*protected void loadSettings(@NonNull FileConfig config, @NonNull String path) {

    }*/

    private void loadLiterals(@NonNull FileConfig config, @NonNull String path) {
        if (this.defaultLiterals.isEmpty()) return;

        this.defaultLiterals.forEach((id, definition) -> {
            String defPath = path + "." + id;

            if (!config.contains(defPath)) {
                config.set(defPath + ".Enabled", definition.enabled());
                config.setStringArray(defPath + ".Aliases", definition.aliases());
                config.set(defPath + ".Cooldown", definition.cooldown());
            }
        });

        config.getSection(path).forEach(sId -> {
            if (!this.literalBuilders.containsKey(sId)) return;

            String defPath = path + "." + sId;

            boolean enabled = config.getBoolean(defPath + ".Enabled");
            String[] aliases = config.getStringArray(defPath + ".Aliases");
            int cooldown = config.getInt(defPath + ".Cooldown");

            this.literals.put(LowerCase.INTERNAL.apply(sId), new LiteralDefinition(enabled, aliases, cooldown));
        });
    }

    private void loadRoot(@NonNull FileConfig config, @NonNull String path) {
        if (this.defaultRoot.isEmpty()) return;

        this.defaultRoot.forEach((id, definition) -> {
            String defPath = path + "." + id;

            if (!config.contains(defPath)) {
                config.set(defPath + ".Enabled", definition.enabled());
                config.setStringArray(defPath + ".Aliases", definition.aliases());
                config.set(defPath + ".Name", definition.name());
                definition.childrenAliases().forEach((childName, childAlias) -> config.set(defPath + ".Childrens." + childName, childAlias));
            }
        });

        config.getSection(path).forEach(sId -> {
            if (!this.rootBuilder.containsKey(sId)) return;

            String defPath = path + "." + sId;

            boolean enabled = config.getBoolean(defPath + ".Enabled");
            String[] aliases = config.getStringArray(defPath + ".Aliases");
            String name = config.getString(defPath + ".Name", "null");

            Map<String, String> childrenAliases = new HashMap<>();
            config.getSection(defPath + ".Childrens").forEach(sId2 -> {
                String alias = config.getString(defPath + ".Childrens." + sId2);
                if (alias == null || alias.isBlank()) return;

                if (!this.literalBuilders.containsKey(sId2)) return;

                childrenAliases.put(LowerCase.INTERNAL.apply(sId2), alias);
            });

            this.root.put(LowerCase.INTERNAL.apply(sId), new HubDefinition(enabled, aliases, name, childrenAliases));
        });
    }

    protected void registerLiteral(@NonNull String id, boolean enabled, @NonNull String[] aliases, @NonNull Consumer<LiteralNodeBuilder> consumer) {
        this.defaultLiterals.put(LowerCase.INTERNAL.apply(id), new LiteralDefinition(enabled, aliases, 0));
        this.literalBuilders.put(LowerCase.INTERNAL.apply(id), consumer);
    }

    protected void registerRoot(@NonNull String name, boolean enabled, @NonNull String[] aliases, @NonNull Consumer<Map<String, String>> mapConsumer, @NonNull Consumer<HubNodeBuilder> consumer) {
        Map<String, String> childrenAliases = new HashMap<>();
        mapConsumer.accept(childrenAliases);

        this.registerRoot(name, enabled, aliases, childrenAliases, consumer);
    }

    protected void registerRoot(@NonNull String name, boolean enabled, @NonNull String[] aliases, @NonNull Map<String, String> childrenAliases, @NonNull Consumer<HubNodeBuilder> consumer) {
        this.defaultRoot.put(LowerCase.INTERNAL.apply(name), new HubDefinition(enabled, aliases, StringUtil.capitalizeUnderscored(name), childrenAliases));
        this.rootBuilder.put(LowerCase.INTERNAL.apply(name), consumer);
    }

    @Override
    @NonNull
    public Map<String, HubDefinition> getRootDefinitions() {
        return this.root;
    }

    @Override
    @NonNull
    public Map<String, Consumer<HubNodeBuilder>> getRootBuilders() {
        return this.rootBuilder;
    }

    @Override
    @NonNull
    public Map<String, LiteralDefinition> getLiteralDefinitions() {
        return this.literals;
    }

    @Override
    @NonNull
    public Map<String, Consumer<LiteralNodeBuilder>> getLiteralBuilders() {
        return this.literalBuilders;
    }

    @Nullable
    protected World getWorld(@NonNull CommandContext context, @NonNull ParsedArguments arguments, @NonNull String argName) {
        if (arguments.contains(argName)) {
            return arguments.getWorld(argName);
        }

        if (!context.isPlayer()) {
            context.printUsage();
            return null;
        }

        return context.getPlayerOrThrow().getWorld();
    }

    protected boolean runForOnlinePlayerOrSender(@NonNull CommandContext context,
                                                 @NonNull ParsedArguments arguments,
                                                 @NonNull Module module,
                                                 @NonNull Function<Player, Boolean> consumer) {
        if (!arguments.contains(CommandArguments.PLAYER) && !context.isPlayer()) {
            context.printUsage();
            return false;
        }

        return this.runForOnlinePlayer(context, arguments, module, consumer);
    }

    protected boolean runForOnlinePlayer(@NonNull CommandContext context,
                                         @NonNull ParsedArguments arguments,
                                         @NonNull Module module,
                                         @NonNull Function<Player, Boolean> consumer) {
        String playerName = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());
        Player target = Players.getPlayer(playerName);

        if (target == null || !this.canSee(context, target)) {
            module.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, context.getSender()); // Module prefix
            return false;
        }

        return consumer.apply(target);
    }

    protected boolean loadPlayerOrSenderWithDataAndRunInMainThread(@NonNull CommandContext context,
                                                                   @NonNull ParsedArguments arguments,
                                                                   @NonNull Module module,
                                                                   @NonNull UserManager userManager,
                                                                   @NonNull BiConsumer<@NonNull SunUser, @NonNull Player> consumer) {
        if (!arguments.contains(CommandArguments.PLAYER) && !context.isPlayer()) {
            context.printUsage();
            return false;
        }

        return this.loadPlayerWithDataAndRunInMainThread(context, arguments, module, userManager, consumer);
    }

    protected boolean loadPlayerWithDataAndRunInMainThread(@NonNull CommandContext context,
                                                           @NonNull ParsedArguments arguments,
                                                           @NonNull Module module,
                                                           @NonNull UserManager userManager,
                                                           @NonNull BiConsumer<@NonNull SunUser, @NonNull Player> consumer) {

        CommandSender sender = context.getSender();
        String playerName = arguments.getString(CommandArguments.PLAYER, sender.getName());

        userManager.loadByNameAsync(playerName).thenCompose(userOptional -> {
            SunUser user = userOptional.orElse(null);
            if (user == null) {
                module.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, sender);
                return CompletableFuture.completedFuture(null);
            }

            return userManager.loadTargetPlayer(user).thenComposeAsync(target -> {
                if (target == null || !this.canSee(context, target)) {
                    module.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, sender);
                    return CompletableFuture.completedFuture(null);
                }

                consumer.accept(user, target);

                return target.isOnline() ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(target::saveData);

            }, this.plugin::runTask);
        }).whenComplete(FutureUtils::printStacktrace);

        return true;
    }

    protected boolean loadPlayerOrSenderAndRunInMainThread(@NonNull CommandContext context,
                                                           @NonNull ParsedArguments arguments,
                                                           @NonNull Module module,
                                                           @NonNull UserManager userManager,
                                                           @NonNull Consumer<@NonNull Player> consumer) {
        if (!arguments.contains(CommandArguments.PLAYER) && !context.isPlayer()) {
            context.printUsage();
            return false;
        }

        return this.loadPlayerAndRunInMainThread(context, arguments, module, userManager, consumer);
    }

    protected boolean loadPlayerAndRunInMainThread(@NonNull CommandContext context,
                                                   @NonNull ParsedArguments arguments,
                                                   @NonNull Module module,
                                                   @NonNull UserManager userManager,
                                                   @NonNull Consumer<@NonNull Player> consumer) {
        String playerName = arguments.getString(CommandArguments.PLAYER, context.getSender().getName());

        return this.loadPlayerAndRunInMainThread(context, playerName, module, userManager, consumer);
    }

    protected boolean loadPlayerAndRunInMainThread(@NonNull CommandContext context,
                                                   @NonNull String playerName,
                                                   @NonNull Module module,
                                                   @NonNull UserManager userManager,
                                                   @NonNull Consumer<@NonNull Player> consumer) {
        userManager.loadTargetPlayer(playerName).thenComposeAsync(target -> {
            if (target == null || !this.canSee(context, target)) {
                module.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, context.getSender());
                return CompletableFuture.completedFuture(null);
            }

            consumer.accept(target);

            return target.isOnline() ? CompletableFuture.completedFuture(null) : CompletableFuture.runAsync(target::saveData);

        }, this.plugin::runTask).whenComplete(FutureUtils::printStacktrace);

        return true;
    }

    protected boolean canSee(@NonNull CommandContext context, @NonNull Player target) {
        return !(context.getSender() instanceof Player sender) || sender.canSee(target);
    }
}
