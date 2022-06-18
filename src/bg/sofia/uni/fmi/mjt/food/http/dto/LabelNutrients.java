package bg.sofia.uni.fmi.mjt.food.http.dto;

import java.util.Objects;

public class LabelNutrients {
    private final Nutrient fat;
    private final Nutrient carbohydrates;
    private final Nutrient fiber;
    private final Nutrient protein;
    private final Nutrient calories;

    public LabelNutrients(Nutrient fat, Nutrient carbohydrates,
                          Nutrient fiber, Nutrient protein, Nutrient calories) {
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.fiber = fiber;
        this.protein = protein;
        this.calories = calories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabelNutrients that = (LabelNutrients) o;
        return Objects.equals(fat, that.fat) && Objects.equals(carbohydrates, that.carbohydrates) &&
               Objects.equals(fiber, that.fiber) && Objects.equals(protein, that.protein) &&
               Objects.equals(calories, that.calories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fat, carbohydrates, fiber, protein, calories);
    }

    @Override
    public String toString() {
        return "LabelNutrient{" +
               "fat:" + fat +
               ", carbohydrates:" + carbohydrates +
               ", fiber:" + fiber +
               ", protein:" + protein +
               ", calories:" + calories +
               '}';
    }
}
