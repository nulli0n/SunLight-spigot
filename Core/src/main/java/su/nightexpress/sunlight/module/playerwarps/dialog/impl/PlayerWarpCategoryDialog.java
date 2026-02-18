package su.nightexpress.sunlight.module.playerwarps.dialog.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import su.nightexpress.nightcore.bridge.common.NightNbtHolder;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
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
import su.nightexpress.sunlight.module.playerwarps.category.NormalCategory;
import su.nightexpress.sunlight.module.playerwarps.PlayerWarp;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GREEN;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.UNDERLINED;
import static su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders.CATEGORY_DESCRIPTION;
import static su.nightexpress.sunlight.module.playerwarps.PlayerWarpsPlaceholders.CATEGORY_NAME;

public class PlayerWarpCategoryDialog extends Dialog<PlayerWarp> {

    private static final TextLocale TITLE = LangEntry.builder("PlayerWarps.Dialog.WarpCategory.Title").text(title("Warp", "Category"));

    private static final DialogElementLocale BODY = LangEntry.builder("PlayerWarps.Dialog.WarpCategory.Body.Selection").dialogElement(400,
        "Please Select a category for your warp."
    );

    private static final ButtonLocale BUTTON_NORMAL = LangEntry.builder("PlayerWarps.Dialog.WarpCategory.Button.CategoryNormal")
        .button(CATEGORY_NAME, CATEGORY_DESCRIPTION);

    private static final ButtonLocale BUTTON_SELECTED = LangEntry.builder("PlayerWarps.Dialog.WarpCategory.Button.CategorySelected")
        .button(GREEN.wrap("✔ " + UNDERLINED.wrap(CATEGORY_NAME)), CATEGORY_DESCRIPTION);

    private static final String ACTION_CATEGORY = "category";
    private static final String JSON_ID         = "id";

    private final PlayerWarpsModule module;

    public PlayerWarpCategoryDialog(@NonNull PlayerWarpsModule module) {
        this.module = module;
    }

    @Override
    @NotNull
    public WrappedDialog create(@NotNull Player player, @NotNull PlayerWarp warp) {
        List<WrappedActionButton> buttons = new ArrayList<>();

        this.module.getSettings().getCategories().forEach(category -> {
            PlaceholderContext placeholderContext = PlaceholderContext.builder().with(category.placeholders()).build();
            ButtonLocale locale = warp.isCategory(category) ? BUTTON_SELECTED : BUTTON_NORMAL;

            buttons.add(DialogButtons.action(locale.replace(placeholderContext::apply))
                .action(DialogActions.customClick(ACTION_CATEGORY, NightNbtHolder.builder().put(JSON_ID, category.id()).build()))
                .build()
            );
        });

        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .build()
            )
            .type(DialogTypes.multiAction(buttons).exitAction(DialogButtons.back()).build())
            .handleResponse(ACTION_CATEGORY, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                String id = nbtHolder.getText(JSON_ID).orElse(null);
                if (id == null) return;

                NormalCategory category = this.module.getSettings().getCategory(id);
                if (category == null) return;

                warp.setCategory(category);
                warp.markDirty();

                viewer.callback();
            })
            .build();
    }
}
