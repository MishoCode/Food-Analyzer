package bg.sofia.uni.fmi.mjt.food.exceptions;

public class FoodHttpClientException extends Exception {
    public FoodHttpClientException(String message) {
        super(message);
    }

    public FoodHttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
