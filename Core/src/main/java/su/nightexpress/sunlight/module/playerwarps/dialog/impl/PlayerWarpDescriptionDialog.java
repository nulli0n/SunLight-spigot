package su.nightexpress.sunlight.module.playerwarps.dialog.impl;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BOLD;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.OPEN_URL;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.SPRITE_ITEM;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.YELLOW;

import java.util.Arrays;

import org.bukkit.Material;
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
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsSettings;

public class PlayerWarpDescriptionDialog extends Dialog<PlayerWarp> {

    private static final TextLocale TITLE = LangEntry.builder("PlayerWarps.Dialog.WarpDescription.Title")
        .text(title("Warp", "Description"));

    private static final DialogElementLocale BODY = LangEntry.builder("PlayerWarps.Dialog.WarpDescription.Body")
        .dialogElement(400,
            "Please enter a description for the warp.",
            "",
            "You can click " + OPEN_URL.with(Placeholders.URL_WIKI_TEXT).wrap(YELLOW.and(BOLD).wrap("HERE")) +
                " to see all available formatting tags and color codes."
        );

    private static final TextLocale INPUT_DESCRIPTION = LangEntry.builder(
        "PlayerWarps.Dialog.WarpDescription.Input.Name")
        .text(SPRITE_ITEM.apply(Material.WRITABLE_BOOK) + " Description");

    private static final String JSON_DESCRIPTION = "description";

    private final PlayerWarpsSettings settings;

    public PlayerWarpDescriptionDialog(@NonNull PlayerWarpsSettings settings) {
        this.settings = settings;
    }

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull PlayerWarp warp) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_DESCRIPTION, INPUT_DESCRIPTION)
                    .maxLength(this.settings.getWarpDescriptionCharacterLimit())
                    .width(200)
                    .initial(String.join("\n", warp.getDescription()))
                    .multiline(new WrappedMultilineOptions(5, 50))
                    .build()
                )
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String description = nbtHolder.getText(JSON_DESCRIPTION).orElse(null);
                if (description == null) return;

                warp.setDescription(Arrays.asList(description.split("\n")));
                warp.markDirty();

                viewer.callback();
            })
            .build();
    }
}
