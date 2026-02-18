package su.nightexpress.sunlight.module.playerwarps.dialog.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.nightcore.util.Placeholders;
import su.nightexpress.sunlight.dialog.Dialog;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.core.PlayerWarpsSettings;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class PlayerWarpNameDialog extends Dialog<PlayerWarp> {

    private static final TextLocale TITLE = LangEntry.builder("PlayerWarps.Dialog.WarpName.Title")
        .text(title("Warp", "Name"));

    private static final DialogElementLocale BODY = LangEntry.builder("PlayerWarps.Dialog.WarpName.Body").dialogElement(400,
        "Please enter a name for your warp.",
        "",
        "You can click " + OPEN_URL.with(Placeholders.URL_WIKI_TEXT).wrap(YELLOW.and(BOLD).wrap("HERE")) + " to see all available formatting tags and color codes."
    );

    private static final TextLocale INPUT_NAME = LangEntry.builder("PlayerWarps.Dialog.WarpName.Input.Name")
        .text(SPRITE_ITEM.apply(Material.NAME_TAG) + " Name");

    private static final String JSON_NAME = "name";

    private final PlayerWarpsSettings settings;

    public PlayerWarpNameDialog(@NonNull PlayerWarpsSettings settings) {
        this.settings = settings;
    }

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull PlayerWarp warp) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(DialogInputs.text(JSON_NAME, INPUT_NAME).maxLength(this.settings.getWarpNameCharacterLimit()).initial(warp.getName()).build())
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String name = nbtHolder.getText(JSON_NAME, warp.getName());

                warp.setName(name);
                warp.markDirty();

                viewer.callback();
            })
            .build();
    }
}
