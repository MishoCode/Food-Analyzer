package bg.sofia.uni.fmi.mjt.food.cache;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodCannotBeCachedException;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerCacheTest {
    private static final String DESCRIPTION = "description";
    private static final String INVALID_GTIN_UPC = "12121";
    private static final String FOOD1_GTIN_UPC = "11111";
    private static final String FOOD3_GTIN_UPC = "33333";
    private static final String EXTENSION = ".txt";
    private static final String INVALID_PATH = "invalid\\path";

    private Cache cache;

    private static Food food1;
    private static Food food2;
    private static Food food3;

    private static Food nonBrandedFood1;
    private static Food nonBrandedFood2;
    private static Food nonBrandedFood3;

    @BeforeAll
    public static void setUpClass() {
        food1 = new Food(123, "description1", "11111");
        food2 = new Food(456, "description2", "22222");
        food3 = new Food(789, "description3", "33333");

        nonBrandedFood1 = new Food(123, "description1", null);
        nonBrandedFood2 = new Food(456, "description2", null);
        nonBrandedFood3 = new Food(789, "description3", null);
    }

    @BeforeEach
    public void setUp() {
        cache = new ServerCache();
    }


    @Test
    public void testSaveFoodWhenFoodIsBranded() throws FoodCannotBeCachedException {
        String cacheProblemAssertMessage = "branded foods are not cached in heap correctly";

        cache.saveFood(food1);
        Food actual = cache.getFoodByGtinUpc(FOOD1_GTIN_UPC);
        assertEquals(food1, actual, cacheProblemAssertMessage);

        String fileProblemsAssertMessage = "files for the branded food does not exists";
        File file = new File(cache.getPath() + File.separator + FOOD1_GTIN_UPC + EXTENSION);
        assertTrue(file.exists(), fileProblemsAssertMessage);
    }

    @Test
    public void testSaveBrandedFoodHeapCacheIsFull() throws FoodCannotBeCachedException {
        String assertMessage = "branded foods are not cached correctly when the heap is full";

        cache.saveFood(food1);
        cache.saveFood(food2);
        cache.saveFood(food3);

        File file = new File(cache.getPath() + File.separator + FOOD3_GTIN_UPC + EXTENSION);
        assertTrue(file.exists(), assertMessage);

        Food newFoodInHeap = cache.getFoodByGtinUpc(FOOD3_GTIN_UPC);
        assertEquals(food3, newFoodInHeap, assertMessage);

        Food foodInFile = cache.getFoodByGtinUpc(FOOD1_GTIN_UPC);
        assertEquals(food1, foodInFile, assertMessage);
    }

    @Test
    public void testBrandedFoodCannotCacheFood() {
        String assertMessage = "branded foods are not cached correctly";

        cache.setPath(INVALID_PATH);
        assertThrows(FoodCannotBeCachedException.class,
            () -> cache.saveFood(food1), assertMessage);
    }

    @Test
    public void testSaveNonBrandedFood() throws FoodCannotBeCachedException {
        String assertMessage = "non-branded foods are not saved correctly";

        cache.saveFood(nonBrandedFood1);
        FoodCollection expected = new FoodCollection(List.of(nonBrandedFood1));
        FoodCollection actual = cache.getFoodsByName(List.of(DESCRIPTION));
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testSaveNonBrandedFoodHeapCacheIsFull() throws FoodCannotBeCachedException {
        String assertMessage = "non-branded foods are not cached correctly when the heap is full";

        cache.saveFood(nonBrandedFood1);
        cache.saveFood(nonBrandedFood2);
        cache.saveFood(nonBrandedFood3);

        FoodCollection expected = new FoodCollection(List.of(nonBrandedFood2, nonBrandedFood3));
        FoodCollection actual = cache.getFoodsByName(List.of(DESCRIPTION));
        assertEquals(expected, actual, assertMessage);
    }

    @Test
    public void testGetFoodByGtinUpc() throws FoodCannotBeCachedException {
        String assertMessage =
            "getFoodByGtinUpc is expected to return null when the gtinUpc does not exist in the cache";

        cache.saveFood(food1);
        assertNull(cache.getFoodByGtinUpc(INVALID_GTIN_UPC), assertMessage);
    }

    @AfterEach
    public void clear() {
        File folder = new File(cache.getPath());
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().contains(EXTENSION)) {
                    f.delete();
                }
            }
        }
    }
}
