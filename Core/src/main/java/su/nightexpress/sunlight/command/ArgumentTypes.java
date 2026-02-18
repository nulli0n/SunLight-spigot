package su.nightexpress.sunlight.command;

import su.nightexpress.nightcore.commands.argument.ArgumentType;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.sunlight.config.Lang;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ArgumentTypes {

    public static final ArgumentType<InetAddress> INET_ADDRESS = (builder, string) -> {
        try {
            return InetAddress.getByName(string);
        }
        catch (UnknownHostException exception) {
            throw CommandSyntaxException.custom(Lang.COMMAND_SYNTAX_INVALID_INET_ADDRESS);
        }
    };
}
