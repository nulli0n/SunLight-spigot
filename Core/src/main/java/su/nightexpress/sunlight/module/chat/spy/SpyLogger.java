package su.nightexpress.sunlight.module.chat.spy;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.text.night.NightMessage;
import su.nightexpress.nightcore.util.time.TimeFormats;
import su.nightexpress.sunlight.SunLightPlugin;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SpyLogger {

    private final SunLightPlugin          plugin;
    private final BlockingQueue<LogEntry> queue;
    private final BufferedWriter          writer;

    private boolean running;

    public SpyLogger(@NotNull SunLightPlugin plugin, @NotNull Path filePath) throws IOException {
        this.plugin = plugin;
        this.queue = new LinkedBlockingQueue<>();

        this.writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        this.running = true;
    }

    private record LogEntry(@NotNull String log, long timestamp) {}

    public void shutdown() {
        this.running = false;
        this.queue.clear();

        if (this.writer != null) {
            try {
                this.writer.close();
            }
            catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public void addEntry(@NotNull String log) {
        String stripped = NightMessage.stripTags(log);

        this.plugin.info(stripped);
        this.queue.add(new LogEntry(stripped, System.currentTimeMillis()));
    }

    public void write() {
        try {
            while (this.running && !this.queue.isEmpty()) {
                LogEntry result = this.queue.poll(500, TimeUnit.MILLISECONDS);
                if (result != null) {
                    String date = TimeFormats.formatDateTime(result.timestamp());
                    this.writer.append("[").append(date).append("] ").append(result.log());
                    this.writer.newLine();
                    this.writer.flush();
                }
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
