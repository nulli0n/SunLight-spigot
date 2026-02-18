package su.nightexpress.sunlight.module.kits.dialog.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.sunlight.dialog.Dialog;
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitNameDialog extends Dialog<Kit> {

    private static final TextLocale TITLE = LangEntry.builder("Kits.Dialog.KitName.Title").text("Kit Name");

    private static final DialogElementLocale BODY = LangEntry.builder("Kits.Dialog.KitName.Body").dialogElement(400,
        "Enter kit name."
    );

    private static final TextLocale INPUT_ID = LangEntry.builder("Kits.Dialog.KitName.Input.Name").text("Name");

    private static final String JSON_NAME = "name";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Kit kit) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_NAME, INPUT_ID).maxLength(256).initial(kit.definition().getName()).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String name = nbtHolder.getText(JSON_NAME).orElse(null);
                if (name == null) return;

                kit.definition().setName(name);
                kit.markDirty();

                viewer.callback();
            })
            .build();
    }
}
