package su.nightexpress.sunlight.module.playerwarps.dialog.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.sunlight.dialog.Dialog;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class PlayerWarpPriceDialog extends Dialog<PlayerWarp> {

    private static final TextLocale TITLE = LangEntry.builder("PlayerWarps.Dialog.WarpPrice.Title").text(title("Warp", "Price"));

    private static final DialogElementLocale BODY = LangEntry.builder("PlayerWarps.Dialog.WarpPrice.Body").dialogElement(400,
        "Please enter a price for your warp."
    );

    private static final TextLocale INPUT_PRICE = LangEntry.builder("PlayerWarps.Dialog.WarpPrice.Input.Price")
        .text(SPRITE_ITEM.apply(Material.EMERALD) + " Price");

    private static final String JSON_PRICE = "price";

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull PlayerWarp warp) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_PRICE, INPUT_PRICE).maxLength(12).initial(String.valueOf(warp.getPrice())).build())
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
