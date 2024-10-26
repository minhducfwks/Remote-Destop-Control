// package remote.server;

// import java.net.DatagramSocket;
// import java.net.SocketException;
// import java.util.concurrent.Executor;
// import java.util.concurrent.Executors;
// import java.util.logging.Level;
// import java.util.logging.Logger;
// import remote.common.LoggingService;

// public class Server1 {
//     private static final Logger logger = Logger.getLogger(Server1.class.getName());
//     private DatagramSocket serverDatagramSocket;
//     private final Executor executor;
//     private int PORT;

//     public Server1(int port) {
//         // create 10 threads to handle
//         executor = Executors.newFixedThreadPool(10);
//         this.PORT = port;
//         try {
//             serverDatagramSocket = new DatagramSocket(PORT);
//             logger.log(Level.INFO, "Server started on port {0} at {1}",
//                     new Object[] { PORT, LoggingService.getCurrentTime() });
//             while (!serverDatagramSocket.isClosed()) {
//                 // create a new task to handle the incoming request
//                 executor.execute(new RequestHandler1(serverDatagramSocket));
//                 Thread.sleep(10);
//             }

//         } catch (SocketException e) {
//             logger.log(Level.SEVERE, "Error creating server socket", e);

//         } catch (InterruptedException e) {
//             logger.log(Level.SEVERE,  "Error handling incoming requests", e);

//         }
//     }

// }