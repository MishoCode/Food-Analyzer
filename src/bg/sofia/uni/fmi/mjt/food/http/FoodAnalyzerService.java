package bg.sofia.uni.fmi.mjt.food.http;

import com.google.gson.Gson;
import bg.sofia.uni.fmi.mjt.food.exceptions.FoodHttpClientException;
import bg.sofia.uni.fmi.mjt.food.exceptions.factory.ExceptionFactory;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodCollection;
import bg.sofia.uni.fmi.mjt.food.http.dto.FoodReport;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class FoodAnalyzerService implements Service {
    private static final String API_ENDPOINT_SCHEME = "https";
    private static final String API_ENDPOINT_HOST = "api.nal.usda.gov";
    private static final String API_ENDPOINT_SEARCH_PATH = "/fdc/v1/foods/search";
    private static final String API_ENDPOINT_REPORT_PATH = "/fdc/v1/food/%d";
    private static final String API_ENDPOINT_SEARCH_QUERY = "api_key=%s&query=%s&requireAllWords=true";
    private static final String API_ENDPOINT_REPORT_QUERY = "api_key=%s";
    private static final String API_KEY = "WClXpu3aU18wfLgMPeUXGQ44j9QRcVmw0kRFXjOr";
    private static final String PARAM_DELIMITER = " ";
    public static final String INVALID_FOOD_NAMES_MSG = "foodNames cannot be null or an empty list";
    public static final String COULD_NOT_RETRIEVE_FOODS_MSG = "Could not retrieve food information";
    private static final Gson GSON = new Gson();

    private final HttpClient foodHttpClient;
    private final String apiKey;


    public FoodAnalyzerService(HttpClient foodHttpClient) {
        this(foodHttpClient, API_KEY);
    }

    public FoodAnalyzerService(HttpClient foodHttpClient, String apiKey) {
        this.foodHttpClient = foodHttpClient;
        this.apiKey = apiKey;
    }

    public FoodCollection getFood(List<String> foodNames) throws FoodHttpClientException {
        if (foodNames == null || foodNames.isEmpty()) {
            throw new IllegalArgumentException(INVALID_FOOD_NAMES_MSG);
        }

        HttpResponse<String> response = sendRequest(
            API_ENDPOINT_SEARCH_PATH,
            API_ENDPOINT_SEARCH_QUERY.formatted(apiKey, joinParameters(foodNames)));

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            FoodCollection foodCollection = GSON.fromJson(response.body(), FoodCollection.class);
            return foodCollection.isEmpty() ? null : foodCollection;
        }

        throw ExceptionFactory.of(response.statusCode());
    }

    public FoodReport getFoodReport(int fdcId) throws FoodHttpClientException {
        HttpResponse<String> response = sendRequest(
            API_ENDPOINT_REPORT_PATH.formatted(fdcId),
            API_ENDPOINT_REPORT_QUERY.formatted(apiKey));

        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            return GSON.fromJson(response.body(), FoodReport.class);
        }

        throw ExceptionFactory.of(response.statusCode());
    }

    private HttpResponse<String> sendRequest(String path, String query) throws FoodHttpClientException {
        try {
            URI uri = new URI(API_ENDPOINT_SCHEME, API_ENDPOINT_HOST, path, query, null);
            HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
            return foodHttpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new FoodHttpClientException(COULD_NOT_RETRIEVE_FOODS_MSG, e);
        }
    }

    private String joinParameters(List<String> foodNames) {
        return String.join(PARAM_DELIMITER, foodNames);
    }
}
