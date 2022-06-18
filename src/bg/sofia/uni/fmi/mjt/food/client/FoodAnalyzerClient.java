package bg.sofia.uni.fmi.mjt.food.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodAnalyzerClient {
    private static final int DEFAULT_PORT = 7070;
    private static final String DEFAULT_HOST = "localhost";
    private static final int BUFFER_SIZE = 16384;

    private static final String UNSUCCESSFUL_CONNECTION_MSG = "An error occurred in the client I/O.";
    private static final String FILE_HANDLER_ERROR_MSG = "An error occurred in the FileHandler.";
    private static final String CLIENT_CANNOT_CONNECT_MSG =
        """
            Unable to connect to the server. \
            Try again later or contact administrator by providing the logs in %s
            """;
    private static final String CONNECTED_CLIENT_MSG = "Connected to the server.";
    private static final String DISCONNECTED_CLIENT_MSG = "[Disconnected]";
    private static final String DISCONNECT_COMMAND = "quit";
    private static final String PROMPT = "=> ";

    private static final Logger LOGGER = Logger.getLogger(FoodAnalyzerClient.class.getName());
    private static final String LOGGER_PATH = "client_errors.log";

    private final String host;
    private final int port;
    private final ByteBuffer buffer;

    public FoodAnalyzerClient() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public FoodAnalyzerClient(String host, int port) {
        this.port = port;
        this.host = host;
        buffer = ByteBuffer.allocate(BUFFER_SIZE);
        initLogger();
    }

    public void start() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(host, port));
            System.out.println(CONNECTED_CLIENT_MSG);

            while (true) {
                System.out.print(PROMPT);
                String message = "";
                while (message.trim().isEmpty()) {
                    message = scanner.nextLine();
                }
                if (message.equals(DISCONNECT_COMMAND)) {
                    break;
                }

                sendMessage(message, socketChannel);
                System.out.println(getResponse(socketChannel));
            }

            System.out.println(DISCONNECTED_CLIENT_MSG);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, UNSUCCESSFUL_CONNECTION_MSG, e);
            System.out.printf(CLIENT_CANNOT_CONNECT_MSG, LOGGER_PATH);
        }
    }

    private void sendMessage(String message, SocketChannel socketChannel) throws IOException {
        buffer.clear();
        buffer.put(message.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        socketChannel.write(buffer);
    }

    private String getResponse(SocketChannel socketChannel) throws IOException {
        buffer.clear();
        socketChannel.read(buffer);
        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private void initLogger() {
        try {
            LOGGER.setUseParentHandlers(false);
            Handler fileHandler = new FileHandler(LOGGER_PATH);
            LOGGER.addHandler(fileHandler);
            fileHandler.setLevel(Level.ALL);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, FILE_HANDLER_ERROR_MSG, e);
        }
    }
}
