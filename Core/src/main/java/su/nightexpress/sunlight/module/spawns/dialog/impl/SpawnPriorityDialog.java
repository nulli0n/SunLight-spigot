package su.nightexpress.sunlight.module.spawns.dialog.impl;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
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
import su.nightexpress.sunlight.module.spawns.Spawn;

public class SpawnPriorityDialog extends Dialog<Spawn> {

    private static final TextLocale TITLE = LangEntry.builder("Spawns.Dialog.SpawnPriority.Title").text(title("Spawn",
        "Priority"));

    private static final DialogElementLocale BODY = LangEntry.builder("Spawns.Dialog.SpawnPriority.Body").dialogElement(
        400,
        "Enter spawn priority."
    );

    private static final TextLocale INPUT_PRIORITY = LangEntry.builder("Spawns.Dialog.SpawnPriority.Input.Priority")
        .text("Priority");

    private static final String JSON_PRIORITY = "priority";

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull Spawn spawn) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_PRIORITY, INPUT_PRIORITY).initial(String.valueOf(spawn.getPriority()))
                    .maxLength(6).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                int priority = nbtHolder.getInt(JSON_PRIORITY, spawn.getPriority());
                spawn.setPriority(priority);
                spawn.markDirty();

                viewer.callback();
            })
            .build();
    }
}
