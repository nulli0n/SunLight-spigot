package su.nightexpress.sunlight.module.homes.dialog.impl;

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
import su.nightexpress.nightcore.ui.dialog.build.DialogTypes;
import su.nightexpress.nightcore.ui.dialog.wrap.Dialog;
import su.nightexpress.nightcore.util.placeholder.PlaceholderContext;
import su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers;
import su.nightexpress.sunlight.module.homes.HomePlaceholders;
import su.nightexpress.sunlight.module.homes.HomesModule;
import su.nightexpress.sunlight.module.homes.impl.Home;

public class HomeDeleteDialog extends Dialog<Home> {

    private static final TextLocale TITLE = LangEntry.builder("Homes.Dialog.Deletion.Title").text(title("Home",
        "Deletion"));

    private static final DialogElementLocale BODY = LangEntry.builder("Homes.Dialog.Deletion.Body").dialogElement(400,
        "Are you sure you want to delete the " + HomePlaceholders.HOME_NAME + " home?",
        "",
        TagWrappers.SOFT_RED.wrap("This action cannot be undone!")
    );

    private final HomesModule module;

    public HomeDeleteDialog(@NonNull HomesModule module) {
        this.module = module;
    }

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull Home home) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY.replace(PlaceholderContext.builder().with(home.placeholders())
                    .build())))
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.confirm()).exitAction(DialogButtons.cancel()).build())
            .handleResponse(DialogActions.CONFIRM, (viewer, identifier, nbtHolder) -> {
                this.module.removeHome(player, home);
                viewer.callback();
            })
            .build();
    }
}
