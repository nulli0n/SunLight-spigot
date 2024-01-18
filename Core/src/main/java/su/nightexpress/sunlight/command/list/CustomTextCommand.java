package su.nightexpress.sunlight.command.list;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.EngineUtils;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.sunlight.Perms;
import su.nightexpress.sunlight.SunLight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Deprecated
// TODO Pages support? in Chat module
public class CustomTextCommand extends GeneralCommand<SunLight> {

    public static final String NAME = "ctext";

    private final List<String> text;

    public CustomTextCommand(@NotNull SunLight plugin, @NotNull File file) {
        super(plugin,
            new String[]{StringUtil.noSpace(file.getName().replace(".txt", ""))},
            Perms.CMD_CTEXT + "." + StringUtil.noSpace(file.getName().replace(".txt", "")).toLowerCase());
        this.text = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String sCurrentLine;
            while ((sCurrentLine = br.readLine()) != null) {
                this.text.add(Colorizer.apply(sCurrentLine));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    @NotNull
    public String getUsage() {
        return "";
    }

    @Override
    @NotNull
    public String getDescription() {
        return "";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        Player player = sender instanceof Player ? (Player) sender : null;
        boolean papi = EngineUtils.hasPlaceholderAPI();

        this.text.forEach(line -> {
            if (player != null && papi) {
                line = PlaceholderAPI.setPlaceholders(player, line);
            }
            PlayerUtil.sendRichMessage(sender, line.replace("%player%", sender.getName()));
        });
    }
}
