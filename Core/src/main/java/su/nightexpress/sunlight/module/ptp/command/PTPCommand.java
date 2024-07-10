package su.nightexpress.sunlight.module.ptp.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandRegistry;
import su.nightexpress.sunlight.command.template.CommandTemplate;
import su.nightexpress.sunlight.module.ptp.PTPModule;
import su.nightexpress.sunlight.module.ptp.config.PTPPerms;

public class PTPCommand {

    public static void load(@NotNull SunLightPlugin plugin, @NotNull PTPModule module) {
        CommandRegistry.addTemplate("ptp", CommandTemplate.group(new String[]{"ptp"},
            "PTP Commands.",
            PTPPerms.PREFIX_COMMAND + "ptp",
            CommandTemplate.direct(new String[]{"request"}, RequestCommands.NODE_REQUEST),
            CommandTemplate.direct(new String[]{"invite"}, RequestCommands.NODE_INVITE),
            CommandTemplate.direct(new String[]{"accept"}, AcceptCommands.NODE_ACCEPT),
            CommandTemplate.direct(new String[]{"decline"}, AcceptCommands.NODE_DECLINE),
            CommandTemplate.direct(new String[]{"toggle"}, ToggleCommand.NODE)
        ));
    }
}
