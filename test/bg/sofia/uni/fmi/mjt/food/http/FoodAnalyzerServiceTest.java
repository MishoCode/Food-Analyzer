package bg.sofia.uni.fmi.mjt.food.http;

import bg.sofia.uni.fmi.mjt.food.exceptions.BadRequestParameterException;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.exceptions.NoFoodFoundException;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodReport;
import bg.sofia.uni.fmi.mjt.food.http.dto.LabelNutrients;
import bg.sofia.uni.fmi.mjt.food.http.dto.Nutrient;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FoodAnalyzerServiceTest {
    private static final int UNKNOWN_STATUS_CODE = -1;
    private static final String FOOD_NAME = "pizza";

    private static FoodCollection foodCollection;
    private static String foodCollectionJson;
    private static FoodReport foodReport;
    private static String foodReportJson;

    @Mock
    private HttpClient foodHttpClientMock;

    @Mock
    private HttpResponse<String> foodHttpResponseMock;

    private Service service;

    @BeforeAll
    public static void setUpClass() {
        initFoodCollection();
        initFoodReport();
    }

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        Mockito.lenient()
            .when(foodHttpClientMock.send(Mockito.any(HttpRequest.class),
                ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenReturn(foodHttpResponseMock);

        service = new FoodAnalyzerService(foodHttpClientMock);
    }

    @Test
    public void testGetFoodWhenFoodNamesIsNull() {
        String assertMessage =
            "Illegal argument exception is expected to be thrown when foodName is null";
        assertThrows(IllegalArgumentException.class,
            () -> service.getFood(null), assertMessage);
    }

    @Test
    public void testGetFoodNoFoodNames() {
        String assertMessage =
            "Illegal argument exception is expected to be thrown when foodName is an empty string";

        assertThrows(IllegalArgumentException.class,
            () -> service.getFood(Collections.emptyList()), assertMessage);
    }

    @Test
    public void testGetFoodValidRequest() throws FoodHttpClientException {
        String assertMessage = "Incorrect food information for a properly requested food";

        when(foodHttpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(foodHttpResponseMock.body()).thenReturn(foodCollectionJson);

        FoodCollection result = service.getFood(List.of(FOOD_NAME));
        assertEquals(foodCollection, result, assertMessage);
    }

    @Test
    public void testGetFoodBadRequestParameter() {
        String assertMessage =
            "BadRequestParameterException should be thrown when the service responds with code 400";

        when(foodHttpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_BAD_REQUEST);
        assertThrows(BadRequestParameterException.class,
            () -> service.getFood(List.of(FOOD_NAME)), assertMessage);
    }

    @Test
    public void testGetFoodUnknownStatusCode() {
        String assertMessage =
            "FoodHttpClientException should be thrown when the service responds with an unknown code";

        when(foodHttpResponseMock.statusCode()).thenReturn(UNKNOWN_STATUS_CODE);
        assertThrows(FoodHttpClientException.class,
            () -> service.getFood(List.of(FOOD_NAME)), assertMessage);
    }

    @Test
    public void testGetFoodWrapsExceptions() throws IOException, InterruptedException {
        String assertMessage =
            "FoodHttpClientException should properly wrap the causing IOException";

        IOException expectedException = new IOException();
        when(foodHttpClientMock.send(Mockito.any(HttpRequest.class),
            ArgumentMatchers.<HttpResponse.BodyHandler<String>>any()))
            .thenThrow(expectedException);

        try {
            service.getFood(List.of(FOOD_NAME));
        } catch (Exception actualException) {
            assertEquals(expectedException, actualException.getCause(), assertMessage);
        }
    }

    @Test
    public void testGetFoodReportValidRequest() throws FoodHttpClientException {
        String assertMessage = "Incorrect food report for a properly requested food fdcId";

        when(foodHttpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(foodHttpResponseMock.body()).thenReturn(foodReportJson);

        FoodReport result = service.getFoodReport(123);
        assertEquals(foodReport, result, assertMessage);
    }

    @Test
    public void testGetFoodReportFoodNotFound() {
        String assertMessage =
            "NoFoodFoundException should be thrown when the service responds with code 404";

        when(foodHttpResponseMock.statusCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);
        assertThrows(NoFoodFoundException.class,
            () -> service.getFoodReport(123), assertMessage);
    }

    private static void initFoodCollection() {
        Food food1 = new Food(123, "description1", "11111");
        Food food2 = new Food(456, "description2", "22222");
        foodCollection = new FoodCollection(List.of(food1, food2));
        foodCollectionJson = new Gson().toJson(foodCollection);
    }

    private static void initFoodReport() {
        LabelNutrients nutrients = new LabelNutrients(
            new Nutrient(1.5),
            new Nutrient(1.6),
            new Nutrient(1.7),
            new Nutrient(1.8),
            new Nutrient(1.9));
        foodReport = new FoodReport("description", "ingredients", nutrients);
        foodReportJson = new Gson().toJson(foodReport);
    }
}
