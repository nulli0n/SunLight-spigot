package su.nightexpress.sunlight.module.warps.dialog.impl;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.ORANGE;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.RED;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SPRITE_ITEM;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.YELLOW;

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
import su.nightexpress.nightcore.util.ArrayUtil;
import su.nightexpress.sunlight.module.warps.Warp;

public class WarpSlotsDialog extends Dialog<Warp> {

    private static final TextLocale TITLE = LangEntry.builder("Warps.Dialog.WarpSlots.Title").text(title("Warp",
        "Slots"));

    private static final DialogElementLocale BODY = LangEntry.builder("Warps.Dialog.WarpSlots.Body").dialogElement(400,
        "Specify the " + YELLOW.wrap("slots") + " and " + YELLOW.wrap("page number") +
            " where this warp will be displayed in the GUI.",
        "",
        "Slot numbers start " + ORANGE.wrap("at 0") + " and should be separated by commas " + GRAY.wrap(
            "(e.g., 1, 2, 10, 11)") + ".",
        "",
        "The " + YELLOW.wrap("total number") + " of GUI pages is determined by the " + YELLOW.wrap("highest") +
            " page number assigned to any warp.",
        "",
        "You can set the page number to " + RED.wrap("0") + " to hide this warp from the GUI."
    );

    private static final TextLocale INPUT_PAGE  = LangEntry.builder("Warps.Dialog.WarpSlots.Input.Page").text(
        SPRITE_ITEM.apply(Material.PAPER) + " Page");
    private static final TextLocale INPUT_SLOTS = LangEntry.builder("Warps.Dialog.WarpSlots.Input.Name").text(
        SPRITE_ITEM.apply(Material.ITEM_FRAME) + " Slots");

    private static final String JSON_PAGE  = "page";
    private static final String JSON_SLOTS = "slots";

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull Warp warp) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(
                    DialogInputs.text(JSON_PAGE, INPUT_PAGE).maxLength(2).initial(String.valueOf(warp.getMenuPage()))
                        .build(),
                    DialogInputs.text(JSON_SLOTS, INPUT_SLOTS).maxLength(30).initial(ArrayUtil.arrayToString(warp
                        .getMenuSlots())).build()
                )
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                int page = nbtHolder.getInt(JSON_PAGE, warp.getMenuPage());
                int[] slots = nbtHolder.getText(JSON_SLOTS).map(ArrayUtil::parseIntArray).orElse(warp.getMenuSlots());

                warp.setMenuPage(page);
                warp.setMenuSlots(slots);
                warp.markDirty();

                viewer.callback();
            })
            .build();
    }
}
