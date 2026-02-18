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

public class KitPriorityDialog extends Dialog<Kit> {

    private static final TextLocale TITLE = LangEntry.builder("Kits.Dialog.KitPriority.Title").text("Kit Priority");

    private static final DialogElementLocale BODY = LangEntry.builder("Kits.Dialog.KitPriority.Body").dialogElement(400,
        "Enter kit priority."
    );

    private static final TextLocale INPUT_ID = LangEntry.builder("Kits.Dialog.KitPriority.Input.Name").text("Priority");

    private static final String JSON_PRIORITY = "priority";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Kit kit) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_PRIORITY, INPUT_ID).maxLength(8).initial(String.valueOf(kit.definition().getPriority())).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                int priority = nbtHolder.getInt(JSON_PRIORITY).orElse(kit.definition().getPriority());

                kit.definition().setPriority(priority);
                kit.markDirty();

                viewer.callback();
            })
            .build();
    }
}
