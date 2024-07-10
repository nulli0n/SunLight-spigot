package su.nightexpress.sunlight.module.customtext;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.customtext.config.CTextPerms;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomText extends AbstractFileData<SunLightPlugin> {

    private String description;
    private List<String> text;

    public CustomText(@NotNull SunLightPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.setText(new ArrayList<>());
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.setDescription(config.getString("Description", ""));
        this.setText(config.getStringList("Text"));
        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Description", this.description);
        config.set("Text", this.text);
    }

    public boolean hasPermission(@NotNull CommandSender sender) {
        return sender.hasPermission(CTextPerms.TEXTS) || sender.hasPermission(this.getPermission());
    }

    @NotNull
    public String getPermission() {
        return CTextPerms.PREFIX_TEXT + this.getId();
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    @NotNull
    public List<String> getText() {
        return text;
    }

    public void setText(@NotNull List<String> text) {
        this.text = text;
    }
}
