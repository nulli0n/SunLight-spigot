package su.nightexpress.sunlight.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nightexpress.sunlight.SunLight;

import java.util.Collections;
import java.util.List;

public class CommandShortcut extends GeneralCommand<SunLight> {

    private final GeneralCommand<SunLight> fallback;
    private final String[]                 extraArgs;

    public CommandShortcut(@NotNull SunLight plugin, @NotNull String[] aliases,
                           @NotNull GeneralCommand<SunLight> fallback, @NotNull String[] extraArgs) {
        super(plugin, aliases, fallback.getPermission());
        this.fallback = fallback;
        this.extraArgs = extraArgs;
    }

    @Override
    @NotNull
    public String getUsage() {
        return this.fallback.getUsage();
    }

    @Override
    @NotNull
    public String getDescription() {
        return this.fallback.getDescription();
    }

    @Override
    public boolean isPlayerOnly() {
        return this.fallback.isPlayerOnly();
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        int fullSize = this.extraArgs.length + args.length;
        int index = 0;
        String[] fined = new String[fullSize];
        for (String str : this.extraArgs) fined[index++] = str;
        for (String str : args) fined[index++] = str;

        List<String> tab = this.fallback.onTabComplete(player, this.fallback.getFallback(), this.getAliases()[0], fined);
        return tab == null ? Collections.emptyList() : tab;
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        String fined = String.join(" ", this.extraArgs) + " " + String.join(" ", result.getArgs());
        if (sender instanceof Player player) {
            if (!this.plugin.getUserManager().checkCommandCooldown(player, "/" + this.fallback.getFallback().getLabel() + " " + fined)) return;
        }
        this.fallback.onCommand(sender, this.fallback.getFallback(), result.getLabel(), fined.split(" "));
    }
}
