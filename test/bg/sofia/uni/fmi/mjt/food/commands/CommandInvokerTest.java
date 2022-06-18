package bg.sofia.uni.fmi.mjt.food.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.food.commands.CommandInvoker.INVALID_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommandInvokerTest {
    private static final String FOOD_NAME = "raffaello";
    private static final String RESPONSE = "success";
    private static final String INVOKE_MESSAGE = "get-food raffaello";
    private static final String UNKNOWN_COMMAND = "get food";

    @Mock
    private Receiver receiver;

    @Mock
    private GetFoodCommand command;

    private CommandInvoker invoker;

    @BeforeEach
    public void setUp() {
        invoker = new CommandInvoker(Map.of(GetFoodCommand.GET_FOOD_CMD, command), receiver);
    }

    @Test
    public void testInvokeNullMessage() {
        String assertMessage =
            "invoke does not work properly when the message is null";

        String actualResponse = invoker.invoke(null);
        assertEquals(INVALID_MESSAGE, actualResponse, assertMessage);
    }

    @Test
    public void testInvoke() {
        String assertMessage = "invoke does not work properly when the command is valid";

        List<String> arguments = List.of(FOOD_NAME);
        when(command.execute(arguments)).thenReturn(RESPONSE);
        String actualResponse = invoker.invoke(INVOKE_MESSAGE);
        assertEquals(RESPONSE, actualResponse, assertMessage);
    }

    @Test
    public void testInvokeUnknownCommand() {
        String assertMessage = "invoke does not work properly when the command is unknown";

        String actualResponse = invoker.invoke(UNKNOWN_COMMAND);
        assertEquals(CommandInvoker.UNKNOWN_COMMAND, actualResponse, assertMessage);
    }
}
