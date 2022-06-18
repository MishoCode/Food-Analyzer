package bg.sofia.uni.fmi.mjt.food.client;

public class ClientMain {
    public static void main(String[] args) {
        FoodAnalyzerClient foodAnalyzerClient = new FoodAnalyzerClient();
        foodAnalyzerClient.start();
    }
}
