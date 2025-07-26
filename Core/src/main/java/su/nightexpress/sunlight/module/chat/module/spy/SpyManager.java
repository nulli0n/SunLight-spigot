package su.nightexpress.sunlight.module.chat.module.spy;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.text.NightMessage;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.api.event.PlayerPrivateMessageEvent;
import su.nightexpress.sunlight.data.user.SunUser;
import su.nightexpress.sunlight.module.chat.ChatModule;
import su.nightexpress.sunlight.module.chat.config.ChatConfig;
import su.nightexpress.sunlight.module.chat.event.AsyncSunlightPlayerChatEvent;
import su.nightexpress.sunlight.module.chat.util.Placeholders;
import su.nightexpress.sunlight.utils.SunUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SpyManager extends AbstractManager<SunLightPlugin> {

    public static final String LOGS_DIR = "/spy_logs/";

    private final ChatModule module;

    public SpyManager(@NotNull SunLightPlugin plugin, @NotNull ChatModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    protected void onLoad() {
        SpyCommands.load(this.plugin, this);

        this.addListener(new SpyListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {

    }

    @NotNull
    public Set<Player> getSpies(@NotNull SpyType spyType) {
        return this.plugin.getServer().getOnlinePlayers().stream()
            .filter(player -> this.plugin.getUserManager().getOrFetch(player).getSettings().get(spyType.getSettingChat()))
            .collect(Collectors.toSet());
    }

    @Nullable
    private String getSpyFormat(@NotNull SpyType spyType, @NotNull CommandSender player, @NotNull String message) {
        String format = ChatConfig.SPY_FORMAT.get().get(spyType);
        if (format == null) return null;

        return Placeholders.forSender(player).apply(format).replace(Placeholders.GENERIC_MESSAGE, message);
    }



    public void handleSpyMode(@NotNull AsyncSunlightPlayerChatEvent event) {
        String format = this.getSpyFormat(SpyType.CHAT, event.getPlayer(), event.getMessage());
        if (format == null) return;

        format = event.getChannel().replacePlaceholders().apply(format);

        this.sendSpyInfo(event.getPlayer(), format, SpyType.CHAT, spy -> !event.getChannel().isInRadius(event.getPlayer(), spy));
        this.writeSpyLog(event.getPlayer(), format, SpyType.CHAT);
    }

    public void handleSpyMode(@NotNull PlayerCommandPreprocessEvent event) {
        String format = this.getSpyFormat(SpyType.COMMAND, event.getPlayer(), event.getMessage());
        if (format == null) return;

        this.sendSpyInfo(event.getPlayer(), format, SpyType.COMMAND, null);
        this.writeSpyLog(event.getPlayer(), format, SpyType.COMMAND);
    }

    public void handleSpyMode(@NotNull PlayerPrivateMessageEvent event) {
        String format = this.getSpyFormat(SpyType.SOCIAL, event.getSender(), event.getMessage());
        if (format == null) return;

        format = format.replace(Placeholders.GENERIC_TARGET, event.getTarget().getName());

        this.sendSpyInfo(event.getSender(), format, SpyType.SOCIAL, null);
        this.writeSpyLog(event.getSender(), format, SpyType.SOCIAL);
    }



    private void sendSpyInfo(@NotNull CommandSender sender, @NotNull String format, @NotNull SpyType spyType, @Nullable Predicate<Player> spyFilter) {
        this.getSpies(spyType).forEach(spy -> {
            if (spy == sender) return;
            if (spyFilter != null && !spyFilter.test(spy)) return;

            Players.sendModernMessage(spy, format);
        });
    }

    private void writeSpyLog(@NotNull CommandSender sender, @NotNull String format, @NotNull SpyType spyType) {
        if (!(sender instanceof Player player)) return;

        SunUser user = this.plugin.getUserManager().getOrFetch(player);
        if (!user.getSettings().get(spyType.getSettingLog())) {
            return;
        }

        this.plugin.info(format);

        String date = SunUtils.formatDate(System.currentTimeMillis());
        String outFile = "[" + date + "] " + NightMessage.stripAll(format);
        String filePath = this.module.getAbsolutePath() + LOGS_DIR + player.getName() + "_" + spyType.name().toLowerCase() + ".log";
        FileUtil.create(new File(filePath));

        this.plugin.runTaskAsync(task -> {
            try (BufferedWriter output = new BufferedWriter(new FileWriter(filePath, true))) {
                output.append(outFile);
                output.newLine();
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }
}
