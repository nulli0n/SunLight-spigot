package su.nightexpress.sunlight.module.spawns.dialog.impl;

import java.util.Arrays;
import java.util.function.Function;

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
import su.nightexpress.sunlight.module.spawns.Spawn;
import su.nightexpress.sunlight.module.spawns.model.SpawnRule;

public class SpawnRulesDialog extends Dialog<Spawn> {

    private static final TextLocale TITLE = LangEntry.builder("Spawns.Dialog.SpawnLoginRules.Title").text(title("Spawn",
        "Login Rules"));

    private static final DialogElementLocale BODY = LangEntry.builder("Spawns.Dialog.SpawnLoginRules.Body")
        .dialogElement(400,
            " "
        );

    private static final TextLocale INPUT_ENABLED = LangEntry.builder("Spawns.Dialog.SpawnLoginRules.Input.Enabled")
        .text("Enabled");
    private static final TextLocale INPUT_RANKS   = LangEntry.builder("Spawns.Dialog.SpawnLoginRules.Input.Ranks").text(
        "Affected Ranks");

    private static final String JSON_ENABLED = "enabled";
    private static final String JSON_RANKS   = "ranks";

    private final Function<Spawn, SpawnRule> ruleFunction;

    public SpawnRulesDialog(@NonNull Function<Spawn, SpawnRule> ruleFunction) {
        this.ruleFunction = ruleFunction;
    }

    @Override
    @NonNull
    public WrappedDialog create(@NonNull Player player, @NonNull Spawn spawn) {
        return Dialogs.builder()
            .base(DialogBases.builder(TITLE)
                .body(DialogBodies.plainMessage(BODY))
                .inputs(
                    DialogInputs.bool(JSON_ENABLED, INPUT_ENABLED).initial(this.ruleFunction.apply(spawn).isEnabled())
                        .build(),
                    DialogInputs.text(JSON_RANKS, INPUT_RANKS)
                        .multiline(new WrappedMultilineOptions(10, 100))
                        .initial(String.join("\n", this.ruleFunction.apply(spawn).getRanks()))
                        .maxLength(200)
                        .build()
                )
                .build()
            )
            .type(DialogTypes.multiAction(DialogButtons.ok()).exitAction(DialogButtons.back()).build())
            .handleResponse(DialogActions.OK, (viewer, identifier, nbtHolder) -> {
                if (nbtHolder == null) return;

                SpawnRule rule = this.ruleFunction.apply(spawn);
                boolean enabled = nbtHolder.getBoolean(JSON_ENABLED, rule.isEnabled());
                String ranksRaw = nbtHolder.getText(JSON_RANKS, String.join("\n", rule.getRanks()));

                rule.setEnabled(enabled);
                rule.setRanks(Arrays.asList(ranksRaw.split("\n")));
                spawn.markDirty();

                viewer.callback();
            })
            .build();
    }
}
