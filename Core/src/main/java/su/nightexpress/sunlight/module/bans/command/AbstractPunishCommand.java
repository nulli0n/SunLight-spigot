package su.nightexpress.sunlight.module.bans.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.command.CommandResult;
import su.nexmedia.engine.api.server.JPermission;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.Colorizer;
import su.nexmedia.engine.utils.Placeholders;
import su.nightexpress.sunlight.module.GeneralModuleCommand;
import su.nightexpress.sunlight.module.bans.BansModule;
import su.nightexpress.sunlight.module.bans.config.BansConfig;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentReason;
import su.nightexpress.sunlight.module.bans.punishment.PunishmentType;
import su.nightexpress.sunlight.module.bans.util.BanTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPunishCommand extends GeneralModuleCommand<BansModule> {

    protected final PunishmentType type;

    public AbstractPunishCommand(@NotNull BansModule module, @NotNull String[] aliases, @Nullable JPermission permission,
                                 @NotNull PunishmentType type) {
        super(module, aliases, permission);
        this.type = type;
    }

    @NotNull
    protected String fineUserName(@NotNull CommandSender sender, @NotNull String userName) {
        return userName;
    }

    protected boolean checkUserName(@NotNull CommandSender sender, @NotNull String userName) {
        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            List<String> list = new ArrayList<>();
            list.add("-1");

            BansConfig.GENERAL_TIME_ALIASES.get().getOrDefault(BanTime.MINUTES, Collections.emptySet()).stream().findFirst().ifPresent(alias -> {
                list.add("15" + alias);
            });

            BansConfig.GENERAL_TIME_ALIASES.get().getOrDefault(BanTime.HOURS, Collections.emptySet()).stream().findFirst().ifPresent(alias -> {
                list.add("1" + alias);
            });

            return list;
        }
        if (arg == 3) {
            return new ArrayList<>(BansConfig.GENERAL_REASONS.get().keySet());
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 2) {
            this.printUsage(sender);
            return;
        }

        String userName = this.fineUserName(sender, result.getArg(0));
        if (!this.checkUserName(sender, userName)) return;

        // Parse ban time from command argument. Returns -1 if time is not parsed or negative or zero provided.
        long banTime = BanTime.parse(result.getArg(1));
        // Find position where the punishment reason starts.
        int reasonIndex = 2;

        // Build reason text from all other arguments.
        String reasonMsg;
        PunishmentReason reason = result.length() > reasonIndex ? this.module.getReason(result.getArg(reasonIndex)) : this.module.getReason(Placeholders.DEFAULT);

        if (reason != null) {
            reasonMsg = reason.getMessage();
        }
        else {
            reasonMsg = Colorizer.apply(Stream.of(result.getArgs()).skip(reasonIndex).collect(Collectors.joining(" ")));
        }

        this.module.punish(userName, sender, reasonMsg, banTime, this.type);
    }
}
