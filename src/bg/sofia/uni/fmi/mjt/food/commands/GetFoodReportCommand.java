package bg.sofia.uni.fmi.mjt.food.commands;

import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodReport;

import java.util.List;

public class GetFoodReportCommand extends Command {
    private static final int FIRST_ARG = 0;
    protected static final String INVALID_FDC_ID =
        "Invalid fdcId argument: fdcId should contain only numbers";
    protected static final String GET_FOOD_REPORT_CMD = "get-food-report";

    public GetFoodReportCommand(int minArgsCount, int maxArgsCount, Receiver receiver) {
        super(minArgsCount, maxArgsCount, GET_FOOD_REPORT_CMD, receiver);
    }

    @Override
    public String execute(List<String> arguments) {
        if (arguments.size() < minArgsCount || arguments.size() > maxArgsCount) {
            return INVALID_ARGS_COUNT_MSG;
        }

        try {
            FoodReport foodReport = receiver.getFoodReport(Integer.parseInt(arguments.get(FIRST_ARG)));
            return foodReport.toString();
        } catch (FoodHttpClientException e) {
            return String.format(SERVICE_ERROR_MSG, e.getMessage());
        } catch (NumberFormatException e) {
            return INVALID_FDC_ID;
        }
    }
}
