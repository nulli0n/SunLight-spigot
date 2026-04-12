package su.nightexpress.sunlight.module.kits.dialog.impl;

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
import su.nightexpress.sunlight.module.kits.model.Kit;

public class KitCooldownDialog extends Dialog<Kit> {

    private static final TextLocale TITLE = LangEntry.builder("Kits.Dialog.KitCooldown.Title").text("Kit Cooldown");

    private static final DialogElementLocale BODY = LangEntry.builder("Kits.Dialog.KitCooldown.Body").dialogElement(400,
        "Enter cooldown value."
    );

    private static final TextLocale INPUT_COOLDOWN = LangEntry.builder("Kits.Dialog.KitCooldown.Input.Name").text(
        "Cooldown");

    private static final String JSON_COOLDOWN = "cooldown";

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull Kit kit) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_COOLDOWN, INPUT_COOLDOWN).maxLength(12).initial(String.valueOf(kit
                    .definition().getCooldown())).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                int cooldown = nbtHolder.getInt(JSON_COOLDOWN).orElse(kit.definition().getCooldown());

                kit.definition().setCooldown(cooldown);
                kit.markDirty();

                viewer.callback();
            })
            .build();
    }
}
