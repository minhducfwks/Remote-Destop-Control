package remote.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import remote.common.LoggingService;
import remote.common.RequestConnection;
import remote.common.ServerConfiguration;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final List<RequestConnection> clients;
    private static final ServerConfiguration serverConfiguration = new ServerConfiguration();
    private static final int MAX_CLIENTS = 20;

    public Server() {
        // Use a fixed thread pool to limit the number of concurrent client threads
        this.threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        this.clients = new ArrayList<>();

        try {
            // Start the server socket on the specified port
            serverSocket = new ServerSocket(serverConfiguration.getTCPPORT());
            logger.log(Level.INFO, "Server started at port {0} on {1}",
                    new Object[] { serverConfiguration.getTCPPORT(), LoggingService.getCurrentTime() });

            // Register a shutdown hook to clean up resources when the server stops
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                shutdownServer();
            }));

            // Continuously listen for new client connections
            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleNewClient(clientSocket);
                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        logger.log(Level.INFO, "Server socket closed. No longer accepting connections.");
                    } else {
                        logger.log(Level.SEVERE, "Error accepting client connection: {0}", e.getMessage());
                    }
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error while initializing server socket on port {0}: {1}",
                    new Object[] { serverConfiguration.getTCPPORT(), e.getMessage() });
        }
    }

    // Method to handle new client connections
    private void handleNewClient(Socket clientSocket) {
        logger.log(Level.INFO, "Incoming client connection from {0}:{1} at {2}",
                new Object[] { clientSocket.getInetAddress(), clientSocket.getPort(),
                        LoggingService.getCurrentTime() });

        // Add the client to the client list in a synchronized manner to avoid
        // concurrency issues
        synchronized (clients) {
            if (clients.size() >= MAX_CLIENTS) {
                logger.log(Level.WARNING, "Max clients reached. Connection from {0}:{1} rejected at {2}",
                        new Object[] { clientSocket.getInetAddress(), clientSocket.getPort(),
                                LoggingService.getCurrentTime() });
                try {
                    clientSocket.close(); // Close the connection if the limit is reached
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error closing rejected client connection: {0}", e.getMessage());
                }
            } else {
                threadPool.submit(new RequestHandler(clientSocket,serverConfiguration,clients));
            }
        }
    }

    // Method to gracefully shut down the server and release resources
    private void shutdownServer() {
        logger.log(Level.INFO, "Shutting down server at {0}", LoggingService.getCurrentTime());
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during server shutdown: {0}", e.getMessage());
        }
        logger.log(Level.INFO, "Server shutdown complete.");
    }

    public static void main(String[] args) {
        Server server = new Server();
    }
}
