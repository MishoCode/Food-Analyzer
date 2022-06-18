package bg.sofia.uni.fmi.mjt.food.cache;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodCannotBeCachedException;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;

import java.util.List;

public interface Cache {
    /**
     * Saves food in the cache. If the food is branded, it is cached both in a file and in the heap.
     * If the food is not branded, it is stored only in the heap.
     *
     * @throws FoodCannotBeCachedException if a problem occur while saving the food in a file
     */
    void saveFood(Food food) throws FoodCannotBeCachedException;

    /**
     * Returns the food from the cache with the given gtinUpc
     */
    Food getFoodByGtinUpc(String gtinUpc);

    /**
     * Returns all non-branded foods
     */
    FoodCollection getFoodsByName(List<String> foodNames);

    /**
     * Returns the directory where branded food files are stored
     */
    String getPath();

    /**
     * Sets value to the path where branded foods will be stored
     */
    void setPath(String path);
}
