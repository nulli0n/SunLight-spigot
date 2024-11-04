package su.nightexpress.sunlight.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.util.Lists;
import su.nightexpress.sunlight.Placeholders;
import su.nightexpress.sunlight.command.cooldown.ArgumentPattern;
import su.nightexpress.sunlight.command.cooldown.CommandCooldown;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandConfig {

    public static final ConfigValue<Boolean> DEBUG_REGISTER_LOG = ConfigValue.create("Debug.RegisterLog",
        false,
        "Controls whether command register info will be printed in server console."
    );

    public static final ConfigValue<Boolean> UNREGISTER_CONFLICTS = ConfigValue.create("Unregister_Conflicts",
        false,
        "Sets whether or not commands with similar names/aliases should be unregistered from the server",
        "if they're overlaps with the SunLight commands.",
        "Warning: Setting this on 'true' may result both positive and negative effects."
    );

    @NotNull
    public static List<CommandCooldown> getDefaultCooldowns() {
        List<CommandCooldown> list = new ArrayList<>();

        CommandCooldown healCooldown = new CommandCooldown(
            "heal",
            Lists.newSet("heal", "health"),
            Lists.newSet(
                new ArgumentPattern(Lists.newList(Lists.newSet("heal"))),
                new ArgumentPattern(Lists.newList(Lists.newSet("health", "hp"), Lists.newSet("restore")))
            ),
            Map.of("vip", 60, "gold", 30)
        );

        CommandCooldown homeCooldown = new CommandCooldown(
            "home",
            Lists.newSet("home", "homes"),
            Lists.newSet(
                new ArgumentPattern(Lists.newList(Lists.newSet("home"))),
                new ArgumentPattern(Lists.newList(Lists.newSet("homes"), Lists.newSet("teleport")))
            ),
            Map.of(Placeholders.DEFAULT, 30, "vip", 0)
        );

        list.add(healCooldown);
        list.add(homeCooldown);

        return list;
    }
}
