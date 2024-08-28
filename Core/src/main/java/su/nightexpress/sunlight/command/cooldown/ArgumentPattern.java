package su.nightexpress.sunlight.command.cooldown;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Lists;

import java.util.*;

public class ArgumentPattern {

    public static final String OR = "\\|";
    public static final String UNKNOWN_ARGUMENT = "?";

    private final List<Set<String>> arguments;

    public ArgumentPattern(@NotNull List<Set<String>> arguments) {
        this.arguments = arguments;
    }

    @NotNull
    public static ArgumentPattern from(@NotNull String str) {
        String[] split = str.split(" ");
        List<Set<String>> arguments = new ArrayList<>();

        for (String arg : split) {
            Set<String> values = new HashSet<>(Arrays.asList(arg.split(OR)));
            arguments.add(Lists.modify(values, String::toLowerCase));
        }

        return new ArgumentPattern(arguments);
    }

    @NotNull
    public String asString() {
        StringBuilder builder = new StringBuilder();

        this.arguments.forEach(values -> {
            if (!builder.isEmpty()) builder.append(" ");
            builder.append(String.join("|", values));
        });

        return builder.toString();
    }

    public boolean isApplicable(String[] commandLine) {
        if (commandLine.length < this.arguments.size()) return false;

        for (int index = 0; index < commandLine.length; index++) {
            if (!this.isArgument(commandLine[index], index)) {
                return false;
            }
        }

        return true;
    }

    public boolean isArgument(@NotNull String argument, int index) {
        if (index >= this.arguments.size()) return false;

        Set<String> values = this.arguments.get(index);

        return values.contains(UNKNOWN_ARGUMENT) || values.contains(argument.toLowerCase());
    }
}
