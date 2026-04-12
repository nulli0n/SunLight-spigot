package su.nightexpress.sunlight.module.kits.dialog.impl;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.bridge.dialog.wrap.input.text.WrappedMultilineOptions;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.DialogActions;
import su.nightexpress.nightcore.ui.dialog.build.DialogBases;
import su.nightexpress.nightcore.ui.dialog.build.DialogBodies;
import su.nightexpress.nightcore.ui.dialog.build.DialogButtons;
import su.nightexpress.nightcore.ui.dialog.build.DialogInputs;
import su.nightexpress.nightcore.ui.dialog.build.DialogTypes;
import su.nightexpress.nightcore.ui.dialog.wrap.Dialog;
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitDescriptionDialog extends Dialog<Kit> {

    private static final TextLocale TITLE = LangEntry.builder("Kits.Dialog.KitDescription.Title").text(
        "Kit Description");

    private static final DialogElementLocale BODY = LangEntry.builder("Kits.Dialog.KitDescription.Body").dialogElement(
        400,
        "Enter kit description."
    );

    private static final TextLocale INPUT_ID = LangEntry.builder("Kits.Dialog.KitDescription.Input.Name").text(
        "Description");

    private static final String JSON_DESCRIPTION = "description";

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull Kit kit) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_DESCRIPTION, INPUT_ID)
                    .maxLength(1024)
                    .width(300)
                    .initial(String.join("\n", kit.definition().getDescription()))
                    .multiline(new WrappedMultilineOptions(5, 300))
                    .build()
                )
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String description = nbtHolder.getText(JSON_DESCRIPTION).orElse(null);
                if (description == null) return;

                kit.definition().setDescription(Arrays.asList(description.split("\n")));
                kit.markDirty();

                viewer.callback();
            })
            .build();
    }
}
