package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodCannotBeCachedException;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.food.commands.Command.INVALID_ARGS_COUNT_MSG;
import static bg.sofia.uni.fmi.mjt.food.commands.Command.SERVICE_ERROR_MSG;
import static bg.sofia.uni.fmi.mjt.food.commands.GetFoodCommand.CASHING_ERROR_MSG;
import static bg.sofia.uni.fmi.mjt.food.commands.GetFoodCommand.NO_FOODS_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetFoodCommandTest {
    private static final String FOOD_NAME = "raffaello";

    @Mock
    private Receiver receiver;

    private static FoodCollection foodCollection;

    private Command getFood;

    @BeforeAll
    public static void setUpClass() {
        Food food1 = new Food(123, "description1", "11111");
        Food food2 = new Food(456, "description2", "22222");
        foodCollection = new FoodCollection(List.of(food1, food2));
    }

    @BeforeEach
    public void setUp() {
        getFood = new GetFoodCommand(1, Integer.MAX_VALUE, receiver);
    }

    @Test
    public void testExecuteGetFood() throws FoodHttpClientException {
        String assertMessage = "execute in GetFoodCommand does not return the correct response";

        when(receiver.getFood(List.of(FOOD_NAME))).thenReturn(foodCollection);
        String expected = foodCollection.toString();
        String actual = getFood.execute(List.of(FOOD_NAME));
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testExecuteGetFoodEmptyResponse() throws FoodHttpClientException {
        String assertMessage =
            "execute in GetFoodCommand does not work properly when the response is empty";

        when(receiver.getFood(List.of(FOOD_NAME))).thenReturn(null);
        String actual = getFood.execute(List.of(FOOD_NAME));
        assertEquals(NO_FOODS_FOUND, actual, assertMessage);
    }

    @Test
    public void testExecuteGetFoodInvalidArgsCount() {
        String assertMessage =
            "execute in GetFoodCommand does not validate the arguments count correctly";

        String actual = getFood.execute(Collections.emptyList());
        assertEquals(INVALID_ARGS_COUNT_MSG, actual, assertMessage);
    }

    @Test
    public void testExecuteGetFoodCommandServiceException() throws FoodHttpClientException {
        String assertMessage =
            "execute in GetFoodCommand does not handle the FoodHttpClientException properly";

        String expected = String.format(SERVICE_ERROR_MSG, "null");
        when(receiver.getFood(any())).thenThrow(FoodHttpClientException.class);
        String actual = getFood.execute(List.of(FOOD_NAME));
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testExecuteGetFoodCommandFoodCannotBeCached()
        throws FoodHttpClientException, FoodCannotBeCachedException {
        String assertMessage =
            "execute in GetFoodCommand does not handle the FoodCannotBeCachedException properly";

        when(receiver.getFood(any())).thenReturn(foodCollection);
        doThrow(FoodCannotBeCachedException.class).when(receiver).cacheFoods(foodCollection);
        String actual = getFood.execute(List.of(FOOD_NAME));
        assertEquals(CASHING_ERROR_MSG, actual, assertMessage);
    }
}
