package su.nightexpress.sunlight.module.kits.dialog.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.sunlight.SLPlaceholders;
import su.nightexpress.sunlight.dialog.Dialog;
import su.nightexpress.sunlight.module.kits.KitsModule;
import su.nightexpress.sunlight.module.kits.config.KitsLang;

public class KitCreationDialog extends Dialog<KitsModule> {

    private static final TextLocale TITLE = LangEntry.builder("Kits.Dialog.KitCreation.Title").text("Kit Creation");

    private static final DialogElementLocale BODY = LangEntry.builder("Kits.Dialog.KitCreation.Body").dialogElement(400,
        "Enter an identifier for new kit."
    );

    private static final TextLocale INPUT_ID = LangEntry.builder("Kits.Dialog.KitCreation.Input.ID").text("Kit Identifier");

    private static final String JSON_ID = "id";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull KitsModule module) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_ID, INPUT_ID).maxLength(64).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String id = nbtHolder.getText(JSON_ID).orElse(null);
                if (id == null) return;

                try {
                    module.createKit(id);
                }
                catch (IllegalArgumentException exception) {
                    module.sendPrefixed(KitsLang.KIT_CREATION_ERROR, player, builder -> builder.with(SLPlaceholders.GENERIC_MESSAGE, exception::getMessage));
                }

                viewer.callback();
            })
            .build();
    }
}
