package bg.sofia.uni.fmi.mjt.food.http;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodReport;

import java.util.List;

public interface Service {
    /**
     * Retrieves foods by foodNames
     *
     * @throws FoodHttpClientException if an error occurs while trying to retrieve data
     */
    FoodCollection getFood(List<String> foodNames) throws FoodHttpClientException;

    /**
     * Returns foodReport for the food with fdcId
     *
     * @throws FoodHttpClientException if an error occurs while trying to retrieve data
     */
    FoodReport getFoodReport(int fdcId) throws FoodHttpClientException;
}
