package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.food.commands.GetFoodByBarcodeCommand.FOOD_NOT_FOUND_MSG;
import static bg.sofia.uni.fmi.mjt.food.commands.GetFoodByBarcodeCommand.INVALID_ARG_OPTION_MSG;
import static bg.sofia.uni.fmi.mjt.food.commands.GetFoodByBarcodeCommand.READING_BARCODE_ERROR_MSG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetFoodByBarcodeCommandTest {
    private static final String CODE_ARG = "--code=123";
    private static final String CODE_ARG_WITHOUT_SEPARATOR = "--code123";
    private static final String INVALID_CODE_ARG = "--cde=123";
    private static final String IMG_ARG = "--img=test.png";
    private static final String INVALID_IMG_ARG = "--img=invalid\\path";

    private static Food food;

    @Mock
    private Receiver receiver;

    private Command getFoodByBarcode;

    @BeforeAll
    public static void setUpClass() {
        food = new Food(123, "description1", "11111");
    }

    @BeforeEach
    public void setUp() {
        getFoodByBarcode = new GetFoodByBarcodeCommand(1, 2, receiver);
    }

    @Test
    public void testExecuteInvalidArgsCount() {
        String assertMessage =
            "execute in GetFoodByBarcodeCommand does not validate the arguments count correctly";

        String expected = GetFoodCommand.INVALID_ARGS_COUNT_MSG;
        String actual = getFoodByBarcode.execute(Collections.emptyList());
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testExecuteTwoArgs() {
        String assertMessage =
            "execute in GetFoodByBarcodeCommand does not work correctly with two arguments";

        when(receiver.getFoodByGtinUpc(any())).thenReturn(food);
        String codeOptionFirst = getFoodByBarcode.execute(List.of(CODE_ARG, IMG_ARG));
        String imgOptionFirst = getFoodByBarcode.execute(List.of(IMG_ARG, CODE_ARG));

        assertEquals(food.toString(), codeOptionFirst, assertMessage);
        assertEquals(food.toString(), imgOptionFirst, assertMessage);
    }

    @Test
    public void testExecuteInvalidArgOption() {
        String assertMessage =
            "execute in GetFoodByBarcodeCommand does not work correctly with invalid argument options";

        String actual = getFoodByBarcode.execute(List.of(INVALID_CODE_ARG));
        assertEquals(INVALID_ARG_OPTION_MSG, actual, assertMessage);
    }

    @Test
    public void testExecuteFoodNotInCache() {
        String assertMessage = """
            execute in GetFoodByBarcodeCommand does not work correctly \
            when the barcode is not found in the cache
            """;

        String actual = getFoodByBarcode.execute(List.of(CODE_ARG));
        assertEquals(FOOD_NOT_FOUND_MSG, actual, assertMessage);
    }

    @Test
    public void testExecuteSearchInCache() {
        String assertMessage = """
            execute in GetFoodByBarcodeCommand does not return \
            the correct response when searching in cache is required
            """;

        when(receiver.getFoodByGtinUpc(any())).thenReturn(food);
        String actual = getFoodByBarcode.execute(List.of(CODE_ARG));
        assertEquals(food.toString(), actual, assertMessage);
    }

    @Test
    public void testExecuteArgumentWithoutOptionSeparator() {
        String assertMessage = """
            execute in GetFoodByBarcodeCommand does not work properly \
            when the command does not have option separator
            """;

        String actual = getFoodByBarcode.execute(List.of(CODE_ARG_WITHOUT_SEPARATOR));
        assertEquals(INVALID_ARG_OPTION_MSG, actual, assertMessage);
    }

    @Test
    public void testExecuteReadsBarcodeFromImage() {
        String assertMessage = """
            execute in GetFoodByBarcodeCommand does not return \
            the correct response when reading from image is required
            """;

        String actual = getFoodByBarcode.execute(List.of(IMG_ARG));
        assertEquals(FOOD_NOT_FOUND_MSG, actual, assertMessage);
    }

    @Test
    public void testExecuteReadsBarcodeFromImageThrowsException() {
        String assertMessage = """
            execute in GetFoodByBarcodeCommand \
            does not work properly when an error occurs while reading the barcode image
            """;

        String actual = getFoodByBarcode.execute(List.of(INVALID_IMG_ARG));
        assertEquals(READING_BARCODE_ERROR_MSG, actual, assertMessage);
    }
}
