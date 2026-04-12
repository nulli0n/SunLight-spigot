package su.nightexpress.sunlight.module.playerwarps.dialog.impl;

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
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsModule;
import su.nightexpress.sunlight.module.playerwarps.menu.WarpsListData;

public class PlayerWarpsSearchDialog extends Dialog<WarpsListData> {

    private static final TextLocale TITLE = LangEntry.builder("PlayerWarps.Dialog.WarpSearch.Title").text(title(
        "Player Warps", "Search"));

    private static final DialogElementLocale BODY = LangEntry.builder("PlayerWarps.Dialog.WarpSearch.Body")
        .dialogElement(400,
            "Enter text to search."
        );

    private static final TextLocale INPUT_TEXT = LangEntry.builder("PlayerWarps.Dialog.WarpSearch.Input.Text").text(
        "Search for");

    private static final String JSON_TEXT = "text";

    protected final PlayerWarpsModule module;

    public PlayerWarpsSearchDialog(@NonNull PlayerWarpsModule module) {
        this.module = module;
    }

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull WarpsListData data) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_TEXT, INPUT_TEXT).maxLength(40).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String text = nbtHolder.getText(JSON_TEXT).orElse(null);

                this.module.openWarpsList(viewer.getPlayer(), data.category(), data.sortType(), text);
            })
            .build();
    }
}
