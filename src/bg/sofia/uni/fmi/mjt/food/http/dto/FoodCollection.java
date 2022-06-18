package bg.sofia.uni.fmi.mjt.food.http.dto;

import java.util.List;
import java.util.Objects;

public class FoodCollection {
    private final List<Food> foods;

    public FoodCollection(List<Food> foods) {
        this.foods = foods;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public boolean isEmpty() {
        return foods.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodCollection that = (FoodCollection) o;
        return Objects.equals(foods, that.foods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foods);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Foods:");
        result.append(System.lineSeparator());
        for (Food food : foods) {
            result.append(food.toString());
            result.append(System.lineSeparator());
        }

        return result.toString();
    }
}
