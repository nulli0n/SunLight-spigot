package su.nightexpress.sunlight.module.warps.dialog.impl;

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
import su.nightexpress.sunlight.module.warps.Warp;
import su.nightexpress.sunlight.module.warps.WarpsModule;

public class WarpCommandDialog extends Dialog<Warp> {

    private static final TextLocale TITLE = LangEntry.builder("Warps.Dialog.WarpCommand.Title").text(title("Warp",
        "Command"));

    private static final DialogElementLocale BODY = LangEntry.builder("Warps.Dialog.WarpCommand.Body").dialogElement(
        400,
        "Enter a " + YELLOW.wrap("command") + " for quick access to this warp.",
        "",
        "This command allows players to quickly teleport to this warp without providing additional arguments.",
        "",
        "To apply these changes, we " + RED.wrap("strongly recommend") + " restarting the server.",
        "However, you may check the " + ORANGE.wrap("'Rebuild Commands'") +
            " box to apply changes 'live', though some minor " + ORANGE.wrap("side effects") + " may occur."
    );

    private static final TextLocale INPUT_ENABLED = LangEntry.builder("Warps.Dialog.WarpCommand.Input.Enabled").text(
        "Enabled");
    private static final TextLocale INPUT_COMMAND = LangEntry.builder("Warps.Dialog.WarpCommand.Input.Name").text(
        SPRITE_ITEM.apply(Material.COMMAND_BLOCK_MINECART) + " Command");
    private static final TextLocale INPUT_REBUILD = LangEntry.builder("Warps.Dialog.WarpCommand.Input.Rebuild").text(
        "Rebuild Commands");

    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_COMMAND = "command";
    private static final String JSON_REBUILD = "rebuild";

    private final WarpsModule module;

    public WarpCommandDialog(@NonNull WarpsModule module) {
        this.module = module;
    }

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull Warp warp) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(
                    DialogInputs.bool(JSON_ENABLED, INPUT_ENABLED).initial(warp.isCommandEnabled()).build(),
                    DialogInputs.text(JSON_COMMAND, INPUT_COMMAND).maxLength(30).initial(warp.getName()).build(),
                    DialogInputs.bool(JSON_REBUILD, INPUT_REBUILD).initial(false).build()
                )
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                boolean enabled = nbtHolder.getBoolean(JSON_ENABLED, warp.isCommandEnabled());
                String name = nbtHolder.getText(JSON_COMMAND, warp.getCommandLabel());

                warp.setCommandEnabled(enabled);
                warp.setCommandLabel(name);
                warp.markDirty();

                if (nbtHolder.getBoolean(JSON_REBUILD, false)) {
                    this.module.updateWarpCommand(warp);
                }

                viewer.callback();
            })
            .build();
    }
}
