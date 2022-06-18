package bg.sofia.uni.fmi.mjt.food.exceptions.factory;

import bg.sofia.uni.fmi.mjt.food.exceptions.BadRequestParameterException;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.exceptions.NoFoodFoundException;

import java.net.HttpURLConnection;

public class ExceptionFactory {
    /*
    Factory method that constructs exception according to the specific http status code
     */
    public static FoodHttpClientException of(int statusCode) {
        if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
            return new BadRequestParameterException("Invalid request parameter");
        } else if (statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
            return new NoFoodFoundException("This food cannot be found");
        } else {
            return new FoodHttpClientException("Unexpected response code from food analyzer service");
        }
    }
}
