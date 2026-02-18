package su.nightexpress.sunlight.module.homes.dialog.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.dialog.Dialog;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;
import su.nightexpress.sunlight.user.UserManager;
import su.nightexpress.sunlight.utils.FutureUtils;

public class HomeInvitePlayerDialog extends Dialog<Home> {

    private final SunLightPlugin plugin;
    private final HomesModule    module;
    private final UserManager    userManager;

    private static final TextLocale TITLE = LangEntry.builder("Homes.Dialog.InvitePlayer.Title").text(title("Home", "Invite Player"));

    private static final DialogElementLocale BODY = LangEntry.builder("Homes.Dialog.InvitePlayer.Body").dialogElement(400,
        "Enter a name of the player you want to invite to your home."
    );

    private static final TextLocale INPUT_NAME = LangEntry.builder("Homes.Dialog.InvitePlayer.Input.PlayerName").text("Player Name");

    private static final String JSON_NAME = "name";

    public HomeInvitePlayerDialog(@NotNull SunLightPlugin plugin, @NotNull HomesModule module, @NotNull UserManager userManager) {
        this.plugin = plugin;
        this.module = module;
        this.userManager = userManager;
    }

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Home home) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_NAME, INPUT_NAME).maxLength(24).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String name = nbtHolder.getText(JSON_NAME, home.getName());
                if (name.isBlank()) return;

                this.userManager.loadTargetProfile(name).thenAcceptAsync(profile -> {
                    if (profile == null) {
                        this.module.sendPrefixed(CoreLang.ERROR_INVALID_PLAYER, player);
                        return;
                    }

                    if (this.module.inviteToHome(player, home, profile)) {
                        viewer.callback();
                    }
                }, this.plugin::runTask).whenComplete(FutureUtils::printStacktrace);
            })
            .build();
    }
}
