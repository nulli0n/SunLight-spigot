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

public class KitCostDialog extends Dialog<Kit> {

    private static final TextLocale TITLE = LangEntry.builder("Kits.Dialog.KitCost.Title").text("Kit Cost");

    private static final DialogElementLocale BODY = LangEntry.builder("Kits.Dialog.KitCost.Body").dialogElement(400,
        "Enter kit cost."
    );

    private static final TextLocale INPUT_ID = LangEntry.builder("Kits.Dialog.KitCost.Input.Name").text("Cost");

    private static final String JSON_COST = "cost";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Kit kit) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_COST, INPUT_ID).maxLength(12).initial(String.valueOf(kit.definition().getCost())).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                double cost = nbtHolder.getDouble(JSON_COST).orElse(kit.definition().getCost());

                kit.definition().setCost(cost);
                kit.markDirty();

                viewer.callback();
            })
            .build();
    }
}
