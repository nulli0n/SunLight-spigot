package su.nightexpress.sunlight.module.customtext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.util.FileUtil;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.module.Module;
import su.nightexpress.sunlight.module.ModuleInfo;
import su.nightexpress.sunlight.module.customtext.command.CustomTextCommands;
import su.nightexpress.sunlight.module.customtext.config.CTextLang;
import su.nightexpress.sunlight.module.customtext.config.CTextPerms;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class CustomTextModule extends Module {

    public static final String DIR_TEXT = "/texts/";

    private final Map<String, CustomText> textMap;

    public CustomTextModule(@NotNull SunLightPlugin plugin, @NotNull String id) {
        super(plugin, id);
        this.textMap = new HashMap<>();
    }

    @Override
    protected void gatherInfo(@NotNull ModuleInfo moduleInfo) {
        moduleInfo.setLangClass(CTextLang.class);
        moduleInfo.setPermissionsClass(CTextPerms.class);
    }

    @Override
    protected void onModuleLoad() {
        this.loadCommands();
        this.loadDefaults();
        this.loadTexts();
    }

    @Override
    protected void onModuleUnload() {
        this.textMap.values().forEach(text -> CustomTextCommands.unregister(this.plugin, text));
        this.textMap.clear();
    }

    private void loadCommands() {
        CustomTextCommands.load(this.plugin, this);
    }

    private void loadDefaults() {
        File dir = new File(this.getAbsolutePath(), DIR_TEXT);
        if (dir.exists()) return;

        dir.mkdirs();

        // TODO Text-coversion methods + options to auto convert Latins to special latins

        this.createText("rules", text -> {
            text.setDescription("Server rules.");
            text.setText(Lists.newList(
                " ",
                RED.enclose(BOLD.enclose("ꜱᴜʀᴠɪᴠᴀʟ ʀᴜʟᴇꜱ")),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ᴏꜰꜰᴇɴꜱɪᴠᴇ ʟᴀɴɢᴜᴀɢᴇ") + " " + LIGHT_GREEN.enclose("[ᴡᴀʀɴ]") + " " + YELLOW.enclose("[ᴍᴜᴛᴇ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ꜱᴘᴀᴍᴍɪɴɢ/ꜰʟᴏᴏᴅɪɴɢ") + " " + LIGHT_GREEN.enclose("[ᴡᴀʀɴ]") + " " + YELLOW.enclose("[ᴍᴜᴛᴇ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ᴛᴏxɪᴄɪᴛʏ") + " " + LIGHT_GREEN.enclose("[ᴡᴀʀɴ]") + " " + YELLOW.enclose("[ᴍᴜᴛᴇ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ʜᴀʀᴀꜱꜱᴍᴇɴᴛ") + " " + LIGHT_GREEN.enclose("[ᴡᴀʀɴ]") + " " + YELLOW.enclose("[ᴍᴜᴛᴇ]") + " " + RED.enclose("[ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ᴀᴅᴠᴇʀᴛɪꜱɪɴɢ") + " " + LIGHT_GREEN.enclose("[ᴍᴜᴛᴇ]") + " " + YELLOW.enclose("[ʙᴀɴ]") + " " + RED.enclose("[ɪᴘ ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ꜱᴛᴇᴀʟɪɴɢ/ɢʀɪᴇꜰɪɴɢ") + " " + LIGHT_GREEN.enclose("[ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ꜱᴄᴀᴍᴍɪɴɢ") + " " + LIGHT_GREEN.enclose("[ʙᴀɴ]") + " " + YELLOW.enclose("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + RED.enclose("[ɪᴘ ʙᴀɴ]"),
                    "",
                    PURPLE.enclose(BOLD.enclose("ɪʟʟᴇɢᴀʟ ᴍᴏᴅɪꜰɪᴄᴀᴛɪᴏɴꜱ")),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("xʀᴀʏ/ᴄʜᴇꜱᴛ ᴇꜱᴘ") + " " + LIGHT_GREEN.enclose("[ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ʜᴀᴄᴋᴇᴅ ᴄʟɪᴇɴᴛ") + " " + LIGHT_GREEN.enclose("[ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ɪʟʟᴇɢᴀʟ ᴍᴏᴅɪꜰɪᴄᴀᴛɪᴏɴꜱ") + " " + LIGHT_GREEN.enclose("[ʙᴀɴ]") + " " + YELLOW.enclose("[ᴘᴇʀᴍ ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ᴄᴀᴠᴇ/ꜱᴇᴇᴅ ꜰɪɴᴅᴇʀꜱ") + " " + LIGHT_GREEN.enclose("[ᴘᴇʀᴍ ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ᴀᴜᴛᴏ ᴄʟɪᴄᴋᴇʀ") + " " + LIGHT_GREEN.enclose("[ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ᴅᴜᴘɪɴɢ") + " " + LIGHT_GREEN.enclose("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + YELLOW.enclose("[ɪᴘ ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ʙᴜɢ ᴀʙᴜꜱᴇ") + " " + LIGHT_GREEN.enclose("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + YELLOW.enclose("[ɪᴘ ʙᴀɴ]"),
                    DARK_GRAY.enclose("▸") + " " + LIGHT_GRAY.enclose("ᴡᴏʀʟᴅ ᴅᴏᴡɴʟᴏᴀᴅɪɴɢ") + " " + LIGHT_GREEN.enclose("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + YELLOW.enclose("[ɪᴘ ʙᴀɴ]"),
                " "
            ));
        });
    }

    private void createText(@NotNull String name, @NotNull Consumer<CustomText> consumer) {
        File file = new File(this.getAbsolutePath() + DIR_TEXT, name + ".yml");
        FileUtil.create(file);

        CustomText text = new CustomText(this.plugin, file);
        consumer.accept(text);
        text.save();
    }

    private void loadTexts() {
        for (File file : FileUtil.getConfigFiles(this.getAbsolutePath() + DIR_TEXT, true)) {
            CustomText text = new CustomText(this.plugin, file);
            if (text.load()) {
                this.textMap.put(text.getId(), text);
            }
            else this.error("Text not loaded: '" + file.getName() + "'!");
        }

        this.info("Loaded " + this.textMap.size() + " custom texts!");

        this.getCustomTexts().forEach(text -> {
            CustomTextCommands.register(this.plugin, text);
        });
    }

    @Nullable
    public CustomText getCustomText(@NotNull String id) {
        return this.textMap.get(id.toLowerCase());
    }

    @NotNull
    public Collection<CustomText> getCustomTexts() {
        return this.textMap.values();
    }
}
