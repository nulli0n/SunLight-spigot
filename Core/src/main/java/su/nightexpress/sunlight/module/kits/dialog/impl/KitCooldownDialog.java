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

public class KitCooldownDialog extends Dialog<Kit> {

    private static final TextLocale TITLE = LangEntry.builder("Kits.Dialog.KitCooldown.Title").text("Kit Cooldown");

    private static final DialogElementLocale BODY = LangEntry.builder("Kits.Dialog.KitCooldown.Body").dialogElement(400,
        "Enter cooldown value."
    );

    private static final TextLocale INPUT_COOLDOWN = LangEntry.builder("Kits.Dialog.KitCooldown.Input.Name").text("Cooldown");

    private static final String JSON_COOLDOWN = "cooldown";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Kit kit) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_COOLDOWN, INPUT_COOLDOWN).maxLength(12).initial(String.valueOf(kit.definition().getCooldown())).build())
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
