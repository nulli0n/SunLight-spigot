package su.nightexpress.sunlight.module.rtp.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.rtp.RTPModule;
import su.nightexpress.sunlight.module.rtp.config.RTPLang;
import su.nightexpress.sunlight.module.rtp.config.RTPPerms;

public class RTPCommandProvider extends AbstractCommandProvider {

    private final RTPModule module;

    public RTPCommandProvider(@NotNull SunLightPlugin plugin, @NotNull RTPModule module) {
        super(plugin);
        this.module = module;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("rtp", true, new String[]{"rtp", "wild"}, builder -> builder
            .playerOnly()
            .description(RTPLang.COMMAND_RTP_DESC)
            .permission(RTPPerms.COMMAND_RTP)
            .executes(this::execute)
        );
    }

    private boolean execute(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player player = context.getPlayerOrThrow();
        this.module.teleportToRandomPlace(player);
        return true;
    }
}
