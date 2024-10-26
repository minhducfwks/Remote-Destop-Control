package remote.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import remote.common.RequestConnection;
import remote.common.ServerConfiguration;

public class RequestHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());
    private final Socket clientSocket;
    private final ServerConfiguration serverConfiguration;
    private ExecutorService taskPool;
    private final List<RequestConnection> clients;

    public RequestHandler(Socket socket, ServerConfiguration serverConfiguration, List<RequestConnection> clients) {
        this.clientSocket = socket;
        this.serverConfiguration = serverConfiguration;
        this.clients = clients;

    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Verifying client request ...");

        try {
            ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

            RequestConnection request = (RequestConnection) inputStream.readObject();
            if (!isValidRequest(request)) {
                sendResponse(dataOutputStream, "denied");
                return;
            }

            sendResponse(dataOutputStream, "ok");
            logger.log(Level.INFO, "Client verified and added.");
            addClient(request);
            this.taskPool = Executors.newFixedThreadPool(3);
        } catch (IOException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Error during verification: ", e);
        }
    }

    private boolean isValidRequest(RequestConnection request) {
        if (request == null || !serverConfiguration.verifyPassword(request.getPassword())) {
            logger.log(Level.WARNING, "Invalid client request or password.");
            return false;
        }
        return true;
    }

    private void sendResponse(DataOutputStream outputStream, String response) throws IOException {
        outputStream.writeUTF(response);
        outputStream.flush();
    }

    // Thêm client vào danh sách
    private void addClient(RequestConnection client) {
        synchronized (clients) {
            clients.add(client);
            logger.log(Level.INFO, "Client added: {0}:{1}. Total clients: {2}",
                    new Object[] { client.getAddress(), client.getPORT(), clients.size()});
        }
    }
}
