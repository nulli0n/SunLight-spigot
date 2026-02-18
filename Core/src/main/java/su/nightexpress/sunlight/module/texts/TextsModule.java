package su.nightexpress.sunlight.module.texts;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.commands.command.NightCommand;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.sunlight.config.PermissionTree;
import su.nightexpress.sunlight.hook.placeholder.PlaceholderRegistry;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleContext;
import su.nightexpress.sunlight.module.texts.command.TextCommandProvider;
import su.nightexpress.sunlight.module.texts.text.Text;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class TextsModule extends Module {

    private final Map<String, Text> textByIdMap;
    private final Set<NightCommand> textCommands;

    public TextsModule(@NotNull ModuleContext context) {
        super(context);
        this.textByIdMap = new HashMap<>();
        this.textCommands = new HashSet<>();
    }

    @Override
    protected void loadModule(@NotNull FileConfig config) {
        this.plugin.injectLang(TextsLang.class);

        this.loadTexts();
    }

    @Override
    protected void unloadModule() {
        this.textCommands.forEach(NightCommand::unregister);
        this.textCommands.clear();

        this.textByIdMap.clear();
    }

    @Override
    protected void registerCommands() {
        this.commandRegistry.addProvider("texts-text", new TextCommandProvider(this.plugin, this));

        this.textByIdMap.values().forEach(text -> {
            NightCommand command = NightCommand.literal(this.plugin, text.getId(), builder -> builder
                .description(text.getDescription())
                .permission(text.getPermission())
                .executes((context, arguments) -> {
                    this.showText(context.getSender(), text);
                    return true;
                })
            );
            if (command.register()) {
                this.textCommands.add(command);
            }
        });
    }

    @Override
    protected void registerPermissions(@NotNull PermissionTree root) {
        root.merge(TextsPerms.MODULE);
    }

    @Override
    public void registerPlaceholders(@NotNull PlaceholderRegistry registry) {

    }

    private void loadTexts() {
        Path textDir = Path.of(this.getSystemPath() + TextsFiles.DIR_TEXTS);

        if (!Files.exists(textDir)) {
            TextsDefaults.defaultTexts().forEach((id, function) -> {
                Path textFile = Path.of(textDir.toString(), FileConfig.withExtension(id));
                FileConfig config = FileConfig.load(textFile);
                Text text = function.apply(textFile, id);
                config.edit(text::write);
            });
        }

        FileUtil.findYamlFiles(textDir.toString()).forEach(file -> {
            Text text = Text.fromFile(file);
            this.textByIdMap.put(text.getId(), text);
        });

        this.info("Loaded " + this.textByIdMap.size() + " custom texts.");
    }

    @Nullable
    public Text getTextById(@NotNull String id) {
        return this.textByIdMap.get(id.toLowerCase());
    }

    @NotNull
    public Set<Text> getCustomTexts() {
        return Set.copyOf(this.textByIdMap.values());
    }

    public void showText(@NotNull CommandSender sender, @NotNull Text text) {
        List<String> texts = sender instanceof Player player ? text.getText(player) : text.getText();
        texts.forEach(line -> Players.sendMessage(sender, line));
    }
}
