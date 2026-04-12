package su.nightexpress.sunlight.module.playerwarps.dialog.impl;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SPRITE_ITEM;

import org.bukkit.Material;
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
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;

public class PlayerWarpPriceDialog extends Dialog<PlayerWarp> {

    private static final TextLocale TITLE = LangEntry.builder("PlayerWarps.Dialog.WarpPrice.Title").text(title("Warp",
        "Price"));

    private static final DialogElementLocale BODY = LangEntry.builder("PlayerWarps.Dialog.WarpPrice.Body")
        .dialogElement(400,
            "Please enter a price for your warp."
        );

    private static final TextLocale INPUT_PRICE = LangEntry.builder("PlayerWarps.Dialog.WarpPrice.Input.Price")
        .text(SPRITE_ITEM.apply(Material.EMERALD) + " Price");

    private static final String JSON_PRICE = "price";

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull PlayerWarp warp) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_PRICE, INPUT_PRICE).maxLength(12).initial(String.valueOf(warp
                    .getPrice())).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                double cost = nbtHolder.getDouble(JSON_PRICE).orElse(warp.getPrice());

                warp.setPrice(cost);
                warp.markDirty();

                viewer.callback();
            })
            .build();
    }
}
