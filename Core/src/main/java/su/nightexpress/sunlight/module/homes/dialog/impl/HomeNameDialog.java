package su.nightexpress.sunlight.module.homes.dialog.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.sunlight.dialog.Dialog;
import su.nightexpress.sunlight.module.homes.impl.Home;

public class HomeNameDialog extends Dialog<Home> {

    private static final TextLocale TITLE = LangEntry.builder("Homes.Dialog.HomeName.Title").text("Home Name");

    private static final DialogElementLocale BODY = LangEntry.builder("Homes.Dialog.HomeName.Body").dialogElement(400,
        "Enter a new name for your home."
    );

    private static final TextLocale INPUT_NAME = LangEntry.builder("Homes.Dialog.HomeName.Input.Name").text("Name");

    private static final String JSON_NAME = "name";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Home home) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_NAME, INPUT_NAME).initial(home.getName()).maxLength(128).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String name = nbtHolder.getText(JSON_NAME, home.getName());
                home.setName(name);
                home.markDirty();

                viewer.callback();
            })
            .build();
    }
}
