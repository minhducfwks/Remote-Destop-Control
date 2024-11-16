package server;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import common.ConstantPacket;

public class StartServer {
    private DatagramSocket serverSocket; // server for request handling
    private DatagramSocket serverScreenSocket; // server for screen sending (multicast socket)
    private InetAddress multicastAddress;

    public StartServer() {
        try {
            System.out.println("Start Server: Starting server on port " +
                    ConstantPORT.REQUEST_PORT.value);

            // initial the server
            this.serverSocket = new DatagramSocket(ConstantPORT.REQUEST_PORT.value);
            byte[] buffer = new byte[ConstantPacket.MESSAGE_BUFFER_SIZE.value];
            this.serverScreenSocket = new DatagramSocket();

            // temporary define. redefin after complete structure
            this.multicastAddress = InetAddress.getByName("230.0.0.1");

            // define a constant response message to client
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            String deniedMessage = "denied";
            String acceptMessage = String.valueOf(ConstantPORT.SCREEN_PORT.value) + "|"
                    + multicastAddress.getHostAddress() + "|" + dim.getWidth() + "|" + dim.getHeight() + "|"
                    + ConstantPORT.MOUSE_PORT.value + "|" + String.valueOf(ConstantPORT.FILE_PORT.value);
            DatagramPacket deniedPacket = new DatagramPacket(deniedMessage.getBytes(),
                    deniedMessage.length());
            DatagramPacket acceptPacket = new DatagramPacket(acceptMessage.getBytes(),
                    acceptMessage.length());

            new Thread(new SendScreen(serverScreenSocket, multicastAddress)).start();

            int count = 0;
            while (true) {
                synchronized (this) {
                    try {
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        serverSocket.receive(request);

                        String message = new String(request.getData(), 0, request.getLength());
                        System.out.println("Received message: " + message);
                        if (!message.equals("connect")) {
                            deniedPacket.setAddress(request.getAddress());
                            deniedPacket.setPort(request.getPort());
                            serverSocket.send(deniedPacket);
                        } else {
                            count++;
                            acceptPacket.setAddress(request.getAddress());
                            acceptPacket.setPort(request.getPort());
                            serverSocket.send(acceptPacket);
                            if (count == 1) {
                                new Thread(new ActionReceiver(ConstantPORT.MOUSE_PORT.value, request.getAddress()))
                                        .start();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
    }
}