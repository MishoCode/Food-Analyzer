package bg.sofia.uni.fmi.mjt.food.commands;

import java.util.List;

/**
 * An abstract class that is used to execute commands with provided arguments.
 * The execution of each command is applied to receiver and the
 * result is returned as a string
 */
public abstract class Command {
    protected static final String SERVICE_ERROR_MSG = "An error occurred in the food service: %s";
    protected static final String INVALID_ARGS_COUNT_MSG = "Invalid count of arguments";

    private final String commandName;
    protected final int minArgsCount;
    protected int maxArgsCount;
    protected Receiver receiver;

    public Command(int minArgsCount, int maxArgsCount,
                   String commandName, Receiver receiver) {
        this.minArgsCount = minArgsCount;
        this.maxArgsCount = maxArgsCount;
        this.commandName = commandName;
        this.receiver = receiver;
    }


    public abstract String execute(List<String> arguments);
}
