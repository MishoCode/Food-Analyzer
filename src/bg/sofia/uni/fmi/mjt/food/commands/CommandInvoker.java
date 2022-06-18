package bg.sofia.uni.fmi.mjt.food.commands;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An object that stores all available commands and executes
 * specific command according to the given message from the server
 */
public class CommandInvoker {
    private static final int GET_FOOD_MIN_ARGS_COUNT = 1;
    private static final int GET_FOOD_MAX_ARGS_COUNT = Integer.MAX_VALUE;

    private static final int GET_FOOD_REPORT_ARGS_COUNT = 1;

    private static final int GET_FOOD_BY_BARCODE_MIN_ARGS_COUNT = 1;
    private static final int GET_FOOD_BY_BARCODE_MAX_ARGS_COUNT = 2;

    private static final String WHITESPACE = "\\s+";
    protected static final String UNKNOWN_COMMAND = "unknown command";
    protected static final String INVALID_MESSAGE =
        "Invalid message to the server: the command cannot be null or empty string";

    private Map<String, Command> commands = new HashMap<>();
    private final Receiver receiver;

    public CommandInvoker(Receiver receiver) {
        this.receiver = receiver;
        initCommands();
    }

    public CommandInvoker(Map<String, Command> commands, Receiver receiver) {
        this(receiver);
        this.commands = commands;
    }

    public String invoke(String message) {
        if (message == null) {
            return INVALID_MESSAGE;
        }

        String[] tokens = message.split(WHITESPACE);
        String commandName = tokens[0];
        if (!commands.containsKey(commandName)) {
            return UNKNOWN_COMMAND;
        }

        List<String> arguments = Arrays.stream(tokens).toList().subList(1, tokens.length);
        Command command = commands.get(commandName);
        return command.execute(arguments);
    }

    private void initCommands() {
        commands.put(
            GetFoodCommand.GET_FOOD_CMD,
            new GetFoodCommand(
                GET_FOOD_MIN_ARGS_COUNT,
                GET_FOOD_MAX_ARGS_COUNT,
                receiver));
        commands.put(
            GetFoodReportCommand.GET_FOOD_REPORT_CMD,
            new GetFoodReportCommand(
                GET_FOOD_REPORT_ARGS_COUNT,
                GET_FOOD_REPORT_ARGS_COUNT,
                receiver));
        commands.put(
            GetFoodByBarcodeCommand.GET_FOOD_BY_BARCODE_CMD,
            new GetFoodByBarcodeCommand(
                GET_FOOD_BY_BARCODE_MIN_ARGS_COUNT,
                GET_FOOD_BY_BARCODE_MAX_ARGS_COUNT,
                receiver));
    }
}
