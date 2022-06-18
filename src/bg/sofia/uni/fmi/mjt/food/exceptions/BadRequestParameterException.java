package bg.sofia.uni.fmi.mjt.food.exceptions;

public class BadRequestParameterException extends FoodHttpClientException {
    public BadRequestParameterException(String message) {
        super(message);
    }
}
