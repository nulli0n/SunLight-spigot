package su.nightexpress.sunlight.module.texts;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.module.texts.text.Text;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class TextsDefaults {

    @NotNull
    public static Map<String, BiFunction<Path, String, Text>> defaultTexts() {
        Map<String, BiFunction<Path, String, Text>> map = new HashMap<>();

        String description = "Server rules.";
        List<String> text = Lists.newList(
            " ",
            RED.wrap(BOLD.wrap("ꜱᴜʀᴠɪᴠᴀʟ ʀᴜʟᴇꜱ")),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ᴏꜰꜰᴇɴꜱɪᴠᴇ ʟᴀɴɢᴜᴀɢᴇ") + " " + GREEN.wrap("[ᴡᴀʀɴ]") + " " + YELLOW.wrap("[ᴍᴜᴛᴇ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ꜱᴘᴀᴍᴍɪɴɢ/ꜰʟᴏᴏᴅɪɴɢ") + " " + GREEN.wrap("[ᴡᴀʀɴ]") + " " + YELLOW.wrap("[ᴍᴜᴛᴇ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ᴛᴏxɪᴄɪᴛʏ") + " " + GREEN.wrap("[ᴡᴀʀɴ]") + " " + YELLOW.wrap("[ᴍᴜᴛᴇ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ʜᴀʀᴀꜱꜱᴍᴇɴᴛ") + " " + GREEN.wrap("[ᴡᴀʀɴ]") + " " + YELLOW.wrap("[ᴍᴜᴛᴇ]") + " " + RED.wrap("[ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ᴀᴅᴠᴇʀᴛɪꜱɪɴɢ") + " " + GREEN.wrap("[ᴍᴜᴛᴇ]") + " " + YELLOW.wrap("[ʙᴀɴ]") + " " + RED.wrap("[ɪᴘ ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ꜱᴛᴇᴀʟɪɴɢ/ɢʀɪᴇꜰɪɴɢ") + " " + GREEN.wrap("[ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ꜱᴄᴀᴍᴍɪɴɢ") + " " + GREEN.wrap("[ʙᴀɴ]") + " " + YELLOW.wrap("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + RED.wrap("[ɪᴘ ʙᴀɴ]"),
            "",
            PURPLE.wrap(BOLD.wrap("ɪʟʟᴇɢᴀʟ ᴍᴏᴅɪꜰɪᴄᴀᴛɪᴏɴꜱ")),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("xʀᴀʏ/ᴄʜᴇꜱᴛ ᴇꜱᴘ") + " " + GREEN.wrap("[ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ʜᴀᴄᴋᴇᴅ ᴄʟɪᴇɴᴛ") + " " + GREEN.wrap("[ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ɪʟʟᴇɢᴀʟ ᴍᴏᴅɪꜰɪᴄᴀᴛɪᴏɴꜱ") + " " + GREEN.wrap("[ʙᴀɴ]") + " " + YELLOW.wrap("[ᴘᴇʀᴍ ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ᴄᴀᴠᴇ/ꜱᴇᴇᴅ ꜰɪɴᴅᴇʀꜱ") + " " + GREEN.wrap("[ᴘᴇʀᴍ ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ᴀᴜᴛᴏ ᴄʟɪᴄᴋᴇʀ") + " " + GREEN.wrap("[ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ᴅᴜᴘɪɴɢ") + " " + GREEN.wrap("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + YELLOW.wrap("[ɪᴘ ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ʙᴜɢ ᴀʙᴜꜱᴇ") + " " + GREEN.wrap("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + YELLOW.wrap("[ɪᴘ ʙᴀɴ]"),
            DARK_GRAY.wrap("▸") + " " + GRAY.wrap("ᴡᴏʀʟᴅ ᴅᴏᴡɴʟᴏᴀᴅɪɴɢ") + " " + GREEN.wrap("[ᴘᴇʀᴍ ʙᴀɴ]") + " " + YELLOW.wrap("[ɪᴘ ʙᴀɴ]"),
            " "
        );

        map.put("rules", (path, id) -> new Text(path, id, description, text));

        return map;
    }
}
