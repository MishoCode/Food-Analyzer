package bg.sofia.uni.fmi.mjt.food.http.dto;

import java.util.Objects;

public class FoodReport {
    private final String description;
    private final String ingredients;
    private final LabelNutrients labelNutrients;

    public FoodReport(String description, String ingredients, LabelNutrients labelNutrient) {
        this.description = description;
        this.ingredients = ingredients;
        this.labelNutrients = labelNutrient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FoodReport that = (FoodReport) o;
        return Objects.equals(description, that.description) &&
               Objects.equals(ingredients, that.ingredients) &&
               Objects.equals(labelNutrients, that.labelNutrients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, ingredients, labelNutrients);
    }

    @Override
    public String toString() {
        return "FoodReport{" +
               "description:'" + description + '\'' +
               ", ingredients:'" + ingredients + '\'' +
               ", labelNutrient:" + labelNutrients +
               '}';
    }
}
