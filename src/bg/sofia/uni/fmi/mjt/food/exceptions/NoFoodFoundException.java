package bg.sofia.uni.fmi.mjt.food.exceptions;

public class NoFoodFoundException extends FoodHttpClientException {
    public NoFoodFoundException(String message) {
        super(message);
    }
}
