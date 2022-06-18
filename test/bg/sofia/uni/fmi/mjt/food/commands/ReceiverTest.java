package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.cache.Cache;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodCannotBeCachedException;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.http.Service;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodReport;
import bg.sofia.uni.fmi.mjt.food.http.dto.LabelNutrients;
import bg.sofia.uni.fmi.mjt.food.http.dto.Nutrient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReceiverTest {
    private static final String FOOD_NAME = "pizza";

    private static Food food1;
    private static Food food2;
    private static FoodCollection foodCollection;
    private static LabelNutrients labelNutrients;
    private static FoodReport foodReport;

    private Receiver receiver;

    @Mock
    private Service serviceMock;

    @Mock
    private Cache cacheMock;

    @BeforeAll
    public static void setUpClass() {
        food1 = new Food(123, "description1", "11111");
        food2 = new Food(456, "description2", "22222");
        foodCollection = new FoodCollection(List.of(food1, food2));
        labelNutrients = new LabelNutrients(
            new Nutrient(1.5),
            new Nutrient(1.6),
            new Nutrient(1.7),
            new Nutrient(1.8),
            new Nutrient(1.9));
        foodReport = new FoodReport("description", "ingredients", labelNutrients);
    }

    @BeforeEach
    public void setUp() {
        receiver = new Receiver(serviceMock, cacheMock);
    }

    @Test
    public void testGetFoodReturnsNull() throws FoodHttpClientException {
        String assertMessage = "getFood is expected to return null";

        when(cacheMock.getFoodsByName(List.of(FOOD_NAME))).thenReturn(null);
        FoodCollection foodCollection = receiver.getFood(List.of(FOOD_NAME));
        assertNull(foodCollection, assertMessage);
    }

    @Test
    public void testGetFoodReturnsFoodCollection() throws FoodHttpClientException {
        String assertMessage =
            "getFood does not work properly when it is expected to return a FoodCollection instance";

        when(cacheMock.getFoodsByName(List.of(FOOD_NAME))).thenReturn(foodCollection);
        FoodCollection actual = receiver.getFood(List.of(FOOD_NAME));

        assertEquals(foodCollection, actual, assertMessage);
        verify(cacheMock, times(1)).getFoodsByName(List.of(FOOD_NAME));
    }

    @Test
    public void testGetFoodByGtinUpc() {
        String assertMessage = "getFoodByGtinUpc does not return the correct food";

        when(cacheMock.getFoodByGtinUpc("11111")).thenReturn(food1);
        Food actual = receiver.getFoodByGtinUpc("11111");

        assertEquals(food1, actual, assertMessage);
        verify(cacheMock, times(1)).getFoodByGtinUpc("11111");
    }

    @Test
    public void testGetFoodReport() throws FoodHttpClientException {
        String assertMessage = "getFoodReport does not return the correct food report";

        when(serviceMock.getFoodReport(123)).thenReturn(foodReport);
        FoodReport actual = receiver.getFoodReport(123);

        assertEquals(foodReport, actual, assertMessage);
        verify(serviceMock, times(1)).getFoodReport(123);
    }

    @Test
    public void testCacheFoods() throws FoodCannotBeCachedException {
        String assertMessage = "cacheFoods does not cache the foods properly";

        when(cacheMock.getFoodsByName(List.of("description"))).thenReturn(foodCollection);
        receiver.cacheFoods(foodCollection);
        FoodCollection foodsInCache = cacheMock.getFoodsByName(List.of("description"));

        assertEquals(foodCollection, foodsInCache, assertMessage);
        verify(cacheMock, times(2)).saveFood(any());
    }
}
