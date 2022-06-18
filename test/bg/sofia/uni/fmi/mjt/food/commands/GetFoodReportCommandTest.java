package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodReport;
import bg.sofia.uni.fmi.mjt.food.http.dto.LabelNutrients;
import bg.sofia.uni.fmi.mjt.food.http.dto.Nutrient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static bg.sofia.uni.fmi.mjt.food.commands.Command.SERVICE_ERROR_MSG;
import static bg.sofia.uni.fmi.mjt.food.commands.GetFoodReportCommand.INVALID_FDC_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetFoodReportCommandTest {
    private static final int FDC_ID = 123;
    private static final String FDC_ID_STRING = "123";
    private static final String LETTERS_FDC_ID = "abc";

    @Mock
    private Receiver receiver;

    private static FoodReport foodReport;

    private Command getFoodReport;

    @BeforeAll
    public static void setUpClass() {
        LabelNutrients nutrients = new LabelNutrients(
            new Nutrient(1.5),
            new Nutrient(1.6),
            new Nutrient(1.7),
            new Nutrient(1.8),
            new Nutrient(1.9));
        foodReport = new FoodReport("description", "ingredients", nutrients);
    }

    @BeforeEach
    public void setUp() {
        getFoodReport = new GetFoodReportCommand(1, 1, receiver);
    }

    @Test
    public void testExecute() throws FoodHttpClientException {
        String assertMessage = "execute in GetFoodReportCommand does not return the correct response";

        when(receiver.getFoodReport(FDC_ID)).thenReturn(foodReport);
        String expected = foodReport.toString();
        String actual = getFoodReport.execute(List.of(FDC_ID_STRING));
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testExecuteGetFoodReportInvalidArgsCount() {
        String assertMessage =
            "execute in GetFoodReportCommand does not validate the arguments count correctly";

        String expected = GetFoodCommand.INVALID_ARGS_COUNT_MSG;
        String actual = getFoodReport.execute(Collections.emptyList());
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testExecuteGetFoodReportCommandServiceException() throws FoodHttpClientException {
        String assertMessage =
            "execute in GetFoodReportCommand does not handle the FoodHttpClientException properly";

        String expected = String.format(SERVICE_ERROR_MSG, "null");
        when(receiver.getFoodReport(FDC_ID)).thenThrow(FoodHttpClientException.class);
        String actual = getFoodReport.execute(List.of(FDC_ID_STRING));
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testGetFoodReportInvalidFdcId() {
        String assertMessage =
            "execute in GetFoodReportCommand is expected to throw NumberFormatException when the fdcId is invalid";

        String actual = getFoodReport.execute(List.of(LETTERS_FDC_ID));
        assertEquals(INVALID_FDC_ID, actual, assertMessage);
    }
}
