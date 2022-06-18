package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.cache.Cache;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodCannotBeCachedException;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.http.Service;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodReport;

import java.util.List;

/**
 * An object that is used by commands for their execution
 */
public class Receiver {
    private final Service service;
    private final Cache cache;

    public Receiver(Service service, Cache cache) {
        this.service = service;
        this.cache = cache;
    }

    public FoodCollection getFood(List<String> foodNames) throws FoodHttpClientException {
        FoodCollection foodCollection = cache.getFoodsByName(foodNames);
        return foodCollection == null ? service.getFood(foodNames) : foodCollection;
    }

    public FoodReport getFoodReport(int fdcId) throws FoodHttpClientException {
        return service.getFoodReport(fdcId);
    }

    public Food getFoodByGtinUpc(String gtinUpc) {
        return cache.getFoodByGtinUpc(gtinUpc);
    }

    public void cacheFoods(FoodCollection foodCollection) throws FoodCannotBeCachedException {
        for (Food food : foodCollection.getFoods()) {
            cache.saveFood(food);
        }
    }
}
