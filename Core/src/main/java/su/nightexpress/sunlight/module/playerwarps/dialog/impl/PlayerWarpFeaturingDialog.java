package su.nightexpress.sunlight.module.playerwarps.dialog.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.bridge.common.NightNbtHolder;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.bridge.dialog.wrap.base.WrappedDialogAfterAction;
import su.nightexpress.nightcore.bridge.dialog.wrap.button.WrappedActionButton;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.ButtonLocale;
import su.nightexpress.nightcore.locale.entry.DialogElementLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.sunlight.dialog.Dialog;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsModule;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;
import su.nightexpress.sunlight.module.playerwarps.featuring.FeaturedSlot;

import java.util.*;
import java.util.function.Predicate;

import static su.nightexpress.sunlight.module.warps.WarpsPlaceholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class PlayerWarpFeaturingDialog extends Dialog<PlayerWarpFeaturingDialog.Data> {

    private static final TextLocale TITLE = LangEntry.builder("PlayerWarps.Dialog.Featuring.Title").text(title("Warp", "Featuring"));

    private static final DialogElementLocale BODY_SELECTION = LangEntry.builder("PlayerWarps.Dialog.Featuring.Body.Selection").dialogElement(400,
        "Select a warp to feature for promotion.",
        "",
        "It will remain featured for " + YELLOW.wrap(PlayerWarpsPlaceholders.SLOT_DURATION) + ". Cost: " + GREEN.wrap(PlayerWarpsPlaceholders.SLOT_PRICE) + "."
    );

    private static final DialogElementLocale BODY_CONFIRMATION = LangEntry.builder("PlayerWarps.Dialog.Featuring.Body.Confirmation").dialogElement(400,
        "Please confirm featuring for the " + YELLOW.and(UNDERLINED).wrap(WARP_NAME) + " warp.",
        "",
        "It will remain featured for " + YELLOW.wrap(PlayerWarpsPlaceholders.SLOT_DURATION) + ". Cost: " + GREEN.wrap(PlayerWarpsPlaceholders.SLOT_PRICE) + "."
    );

    private static final ButtonLocale BUTTON_WARP = LangEntry.builder("PlayerWarps.Dialog.Featuring.Button.Warp").button(WARP_NAME, WARP_DESCRIPTION);

    private static final String ACTION_WARP = "warp";
    private static final String JSON_ID = "id";

    private final PlayerWarpsModule module;
    private final Map<UUID, String> idCache;

    public record Data(@NonNull FeaturedSlot slot, int slotIndex) {}

    public PlayerWarpFeaturingDialog(@NonNull PlayerWarpsModule module) {
        this.module = module;
        this.idCache = new HashMap<>();
    }

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull Data data) {
        List<WrappedActionButton> buttons = new ArrayList<>();

        PlaceholderContext.Builder bodyPlaceholders = PlaceholderContext.builder().with(data.slot.placeholders());

        String selectedId = this.idCache.get(player.getUniqueId());
        PlayerWarp selectedWarp = selectedId == null ? null : this.module.getRepository().getById(selectedId);
        boolean selected = selectedWarp != null;

        if (!selected) {
            this.module.getRepository().getByOwner(player.getUniqueId()).stream().filter(Predicate.not(PlayerWarp::isFeatured)).forEach(warp -> {
                PlaceholderContext placeholderContext = PlaceholderContext.builder().with(warp.placeholders()).build();
                ButtonLocale locale = (BUTTON_WARP).replace(placeholderContext::apply);

                buttons.add(DialogButtons.action(locale)
                    .action(DialogActions.customClick(ACTION_WARP, NightNbtHolder.builder().put(JSON_ID, warp.getId()).build()))
                    .build()
                );
            });
        }
        else {
            bodyPlaceholders.with(selectedWarp.placeholders());
            buttons.add(DialogButtons.ok());
        }

        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage((selected ? BODY_CONFIRMATION : BODY_SELECTION).replace(bodyPlaceholders.build()::apply)))
                .afterAction(WrappedDialogAfterAction.NONE)
                .build()
            )
            .type(DialogTypes.multiAction(buttons).exitAction(DialogButtons.back()).build())
            .handleResponse(ACTION_WARP, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String id = nbtHolder.getText(JSON_ID).orElse(null);
                if (id == null) return;

                this.idCache.put(viewer.getPlayer().getUniqueId(), id);

                this.show(viewer.getPlayer(), data, viewer.getCallback());
            })
            .handleResponse(DialogActions.BACK, (viewer, identifier, nbtHolder) -> {
                this.idCache.remove(viewer.getPlayer().getUniqueId());
                viewer.closeFully();
            })
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String id = this.idCache.remove(viewer.getPlayer().getUniqueId());
                if (id == null) return;

                viewer.close();
                this.module.purchaseFeaturedSlot(viewer.getPlayer(), data.slot, data.slotIndex, id);
            })
            .build();
    }
}
