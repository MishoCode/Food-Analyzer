package bg.sofia.uni.fmi.mjt.food.cache;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodCannotBeCachedException;
import bg.sofia.uni.fmi.mjt.food.http.dto.Food;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ServerCache implements Cache {
    private static final String FOOD_PATH = ".\\resources\\";
    private static final int BRANDED_FOODS_MAX_SIZE = 2;
    private static final int NON_BRANDED_FOODS_MAX_SIZE = 2;
    private static final String FOOD_CANNOT_BE_STORED_IN_FILE_MSG = "A food cannot be stored in a file";
    private static final String EXTENSION = ".txt";

    private String path;
    private final Map<String, Food> brandedFoods = new HashMap<>();
    private final Queue<String> cachedGtinUpc = new LinkedList<>();
    private final Set<Food> nonBrandedFoods = new LinkedHashSet<>();

    public ServerCache() {
        this(FOOD_PATH);
    }

    public ServerCache(String path) {
        this.path = path;
    }

    @Override
    public void saveFood(Food food) throws FoodCannotBeCachedException {
        if (food.getGtinUpc() != null) {
            saveBrandedFood(food);
        } else {
            saveNonBrandedFood(food);
        }
    }

    @Override
    public Food getFoodByGtinUpc(String gtinUpc) {
        Food food = brandedFoods.get(gtinUpc);
        if (food != null) {
            return food;
        }

        Path path = Paths.get(this.path + File.separator + gtinUpc + EXTENSION);
        Food deserializedFood;
        try (var reader = new ObjectInputStream(Files.newInputStream(path))) {
            deserializedFood = (Food) reader.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }

        return deserializedFood;
    }

    @Override
    public FoodCollection getFoodsByName(List<String> foodNames) {
        List<Food> foods = nonBrandedFoods.stream()
            .filter(f -> filterByFoodNames(f, foodNames))
            .toList();

        return foods.isEmpty() ? null : new FoodCollection(foods);
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    private void saveBrandedFood(Food food) throws FoodCannotBeCachedException {
        Path path = Paths.get(this.path + File.separator + food.getGtinUpc() + EXTENSION);
        try (var writer = new ObjectOutputStream(Files.newOutputStream(path))) {
            writer.writeObject(food);
            writer.flush();
        } catch (IOException e) {
            throw new FoodCannotBeCachedException(FOOD_CANNOT_BE_STORED_IN_FILE_MSG, e);
        }

        if (!brandedFoods.containsKey(food.getGtinUpc())) {
            if (brandedFoods.size() == BRANDED_FOODS_MAX_SIZE) {
                brandedFoods.remove(cachedGtinUpc.poll());
            }
            brandedFoods.put(food.getGtinUpc(), food);
            cachedGtinUpc.add(food.getGtinUpc());
        }
    }

    private void saveNonBrandedFood(Food food) {
        if (nonBrandedFoods.size() == NON_BRANDED_FOODS_MAX_SIZE) {
            nonBrandedFoods.stream()
                .findFirst()
                .ifPresent(nonBrandedFoods::remove);
        }
        nonBrandedFoods.add(food);
    }

    private boolean filterByFoodNames(Food food, List<String> foodNames) {
        for (String foodName : foodNames) {
            if (food.getDescription().contains(foodName)) {
                return true;
            }
        }
        return false;
    }
}
