package bg.sofia.uni.fmi.mjt.food.server;

import bg.sofia.uni.fmi.mjt.food.cache.ServerCache;
import bg.sofia.uni.fmi.mjt.food.commands.CommandInvoker;
import bg.sofia.uni.fmi.mjt.food.commands.Receiver;
import bg.sofia.uni.fmi.mjt.food.exceptions.ServerSocketException;
import bg.sofia.uni.fmi.mjt.food.http.FoodAnalyzerService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.http.HttpClient;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FoodAnalyzerServer {
    private static final int DEFAULT_PORT = 7070;
    private static final String DEFAULT_HOST = "localhost";
    private static final int BUFFER_SIZE = 16384;
    private static final Logger LOGGER = Logger.getLogger(FoodAnalyzerServer.class.getName());
    private static final String LOGGER_PATH = "server_errors.log";
    private static final String SERVER_SOCKET_ERROR_MSG =
        "There is a problem with the bg.sofia.uni.fmi.mjt.food.server socket";
    private static final String FAILED_COMMUNICATION_MSG = "An error occurred in the server";
    private static final String FILE_HANDLER_ERROR_MSG = "An error occurred in the FileHandler.";

    private final String host;
    private final int port;
    private final ByteBuffer buffer;
    private final CommandInvoker commandInvoker;
    private final Receiver receiver = new Receiver(
        new FoodAnalyzerService(HttpClient.newHttpClient()),
        new ServerCache());

    private boolean isStarted = true;

    public FoodAnalyzerServer(String host, int port) {
        this.host = host;
        this.port = port;
        this.buffer = ByteBuffer.allocate(BUFFER_SIZE);
        commandInvoker = new CommandInvoker(receiver);
        initLogger();
    }

    public FoodAnalyzerServer() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.bind(new InetSocketAddress(DEFAULT_HOST, DEFAULT_PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (isStarted) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = keys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey currentKey = keyIterator.next();
                    if (currentKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) currentKey.channel();

                        buffer.clear();
                        int readBytes = -1;
                        try {
                            readBytes = socketChannel.read(buffer);
                        } catch (SocketException e) {
                            System.out.printf("Client %s failed%n", socketChannel.getRemoteAddress());
                        }

                        if (readBytes < 0) {
                            System.out.printf("Client %s has closed the connection%n",
                                socketChannel.getRemoteAddress());
                            socketChannel.close();
                            continue;
                        }

                        handleKeyIsReadable(socketChannel);
                    } else if (currentKey.isAcceptable()) {
                        handleKeyIsAcceptable(currentKey, selector);
                    }

                    keyIterator.remove();
                }
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, FAILED_COMMUNICATION_MSG, e);
            //throw new ServerSocketException(SERVER_SOCKET_ERROR_MSG, e);
        }
    }

    public void stop() {
        isStarted = false;
    }

    private void handleKeyIsReadable(SocketChannel socketChannel) throws IOException {
        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).strip();
        System.out.printf("Message %s received from client %s%n", message, socketChannel.getRemoteAddress());

        String response = commandInvoker.invoke(message);
        if (response != null) {
            System.out.printf("Sending response [%s] to client %s%n", response, socketChannel.getRemoteAddress());
            response += System.lineSeparator();
            buffer.clear();
            buffer.put(response.getBytes(StandardCharsets.UTF_8));
            buffer.flip();
            socketChannel.write(buffer);
        }
    }

    private void handleKeyIsAcceptable(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = serverSocketChannel.accept();
        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);

        System.out.printf("Connection from client %s accepted%n", accept.getRemoteAddress());
    }

    private void initLogger() {
        try {
            Handler fileHandler = new FileHandler(LOGGER_PATH);
            LOGGER.addHandler(fileHandler);
            fileHandler.setLevel(Level.ALL);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, FILE_HANDLER_ERROR_MSG, e);
        }
    }
}
