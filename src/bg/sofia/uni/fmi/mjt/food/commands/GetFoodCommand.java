package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodCannotBeCachedException;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;

import java.util.List;

public class GetFoodCommand extends Command {
    protected static final String NO_FOODS_FOUND = "no foods found";
    protected static final String GET_FOOD_CMD = "get-food";
    protected static final String CASHING_ERROR_MSG = "problems while saving the food information";

    public GetFoodCommand(int minArgsCount, int maxArgsCount, Receiver receiver) {
        super(minArgsCount, maxArgsCount, GET_FOOD_CMD, receiver);
    }

    @Override
    public String execute(List<String> arguments) {
        if (arguments.size() < minArgsCount || arguments.size() > maxArgsCount) {
            return INVALID_ARGS_COUNT_MSG;
        }

        String response;
        try {
            FoodCollection foodCollection = receiver.getFood(arguments);
            if (foodCollection == null) {
                return NO_FOODS_FOUND;
            }
            receiver.cacheFoods(foodCollection);
            response = foodCollection.toString();
        } catch (FoodHttpClientException e) {
            return String.format(SERVICE_ERROR_MSG, e.getMessage());
        } catch (FoodCannotBeCachedException e) {
            return CASHING_ERROR_MSG;
        }

        return response;
    }
}
