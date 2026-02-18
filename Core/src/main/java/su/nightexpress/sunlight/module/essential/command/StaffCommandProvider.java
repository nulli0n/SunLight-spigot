package su.nightexpress.sunlight.module.essential.command;

import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.commands.context.CommandContext;
import su.nightexpress.nightcore.commands.context.ParsedArguments;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.MessageLocale;
import su.nightexpress.nightcore.locale.entry.TextLocale;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.placeholder.Replacer;
import su.nightexpress.sunlight.SunLightPlugin;
import su.nightexpress.sunlight.command.provider.type.AbstractCommandProvider;
import su.nightexpress.sunlight.module.essential.EssentialModule;
import su.nightexpress.sunlight.module.essential.EssentialPerms;
import su.nightexpress.sunlight.module.essential.EssentialSettings;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.BR;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.GRAY;
import static su.nightexpress.sunlight.SLPlaceholders.*;

public class StaffCommandProvider extends AbstractCommandProvider {

    private static final Permission STAFF = EssentialPerms.COMMAND.permission("staff");

    private static final TextLocale DESCRIPTION = LangEntry.builder("Command.Staff.Desc").text("Show online staff.");

    private static final MessageLocale MESSAGE_NO_STAFF_ONLINE = LangEntry.builder("Command.Staff.Empty").chatMessage(
        GRAY.wrap("There is no staff online.")
    );

    private final EssentialModule module;
    private final EssentialSettings settings;

    public StaffCommandProvider(@NotNull SunLightPlugin plugin, @NotNull EssentialModule module, @NotNull EssentialSettings settings) {
        super(plugin);
        this.module = module;
        this.settings = settings;
    }

    @Override
    public void registerDefaults() {
        this.registerLiteral("staff", true, new String[]{"staff"}, builder -> builder
            .description(DESCRIPTION)
            .permission(STAFF)
            .executes(this::showStaff)
        );
    }

    @NotNull
    private String formatEntry(@NotNull Player player) {
        return forPlayerWithPAPI(player).apply(this.settings.staffEntryFormat.get());
    }

    private boolean showStaff(@NotNull CommandContext context, @NotNull ParsedArguments arguments) {
        Player executor = context.getPlayer();
        Set<Player> staffs = new HashSet<>();

        Players.getOnline().forEach(other -> {
            if (executor != null && !executor.canSee(other)) return;

            Set<String> playerRanks = Players.getInheritanceGroups(other);
            if (playerRanks.stream().anyMatch(this.settings.staffRanks.get()::contains)) {
                staffs.add(other);
            }
        });

        if (staffs.isEmpty()) {
            this.module.sendPrefixed(MESSAGE_NO_STAFF_ONLINE, context.getSender());
            return false;
        }

        String entries = staffs.stream()
            .sorted(Comparator.comparing(Player::getName))
            .map(this::formatEntry)
            .collect(Collectors.joining(BR));

        String text = String.join("\n", Replacer.create()
            .replace(GENERIC_ENTRY, entries)
            .replace(GENERIC_AMOUNT, () -> String.valueOf(staffs.size()))
            .apply(this.settings.staffFormat.get()));

        Players.sendMessage(context.getSender(), text);

        return true;
    }
}
