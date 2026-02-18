package su.nightexpress.sunlight.module.bans.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.Arguments;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.CommandArguments;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansLang;
import su.nightexpress.sunlight.module.bans.config.BansPerms;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.FutureUtils;

import java.util.concurrent.CompletableFuture;

public class AltsCommandProvider extends AbstractCommandProvider {

    private final BansModule module;
    private final UserManager userManager;

    public AltsCommandProvider(@NotNull SunLightPlugin plugin, @NotNull BansModule module, @NotNull UserManager userManager) {
        super(plugin);
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("alts", true, new String[]{"alts"}, builder -> builder
            .description(BansLang.COMMAND_ALTS_DESC)
            .permission(BansPerms.COMMAND_ALTS)
            .withArguments(Arguments.playerName(CommandArguments.PLAYER))
            .executes(this::showAlts)
        );
    }

    private boolean showAlts(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        String playerName = arguments.getString(CommandArguments.PLAYER);

        this.userManager.loadTargetProfile(playerName).thenCompose(profile -> {
            if (profile == null) {
                context.errorBadPlayer();
                return CompletableFuture.completedFuture(null);
            }

            return this.userManager.loadInetAddress(profile.id()).thenCompose(address -> {
                if (address == null) {
                    context.errorBadPlayer();
                    return CompletableFuture.completedFuture(null);
                }

                return this.module.lookupAltProfiles(address).thenAcceptAsync(alts -> {
                    this.module.notifyAltProfiles(context.getSender(), profile, address, alts);
                }, this.plugin::runTask);
            });
        }).whenComplete(FutureUtils::printStacktrace);

        return true;
    }
}
