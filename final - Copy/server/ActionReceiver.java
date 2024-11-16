package server;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import common.ConstantPacket;

public class ActionReceiver implements Runnable {
    private DatagramSocket serverSocket;

    private final Integer serverPort;
    private final InetAddress clientAddress;

    private Robot robot;

    public ActionReceiver(Integer _serverPort, InetAddress _clientAddress) {
        this.serverPort = _serverPort;
        this.clientAddress = _clientAddress;
        try {
            this.serverSocket = new DatagramSocket(this.serverPort);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        try {
            this.robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[ConstantPacket.MESSAGE_BUFFER_SIZE.value];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        while (true) {
            try {
                serverSocket.receive(packet);
                if (!packet.getAddress().equals(clientAddress)) {
                    return;
                }
                String message = new String(packet.getData(), 0, packet.getData().length).trim();
                String[] splitMsg = message.split(";");
                if (splitMsg.length < 3) {
                    System.out.println("Invalid message format: " + message);
                    continue;
                }
                try {
                    int event = Integer.parseInt(splitMsg[0]);
                    System.out.println(event);
                    switch (event) {
                        case -1:
                            robot.mouseMove(Integer.parseInt(splitMsg[1]), Integer.parseInt(splitMsg[2]));
                            break;
                        case -2:
                            robot.mousePress(Integer.parseInt(splitMsg[1]));
                            break;
                        case -3:
                            robot.mouseRelease(Integer.parseInt(splitMsg[1]));
                            break;
                        case -4:
                            robot.keyPress(Integer.parseInt(splitMsg[1]));
                            break;
                        case -5:
                            robot.keyRelease(Integer.parseInt(splitMsg[1]));
                            break;
                        default:
                            System.out.println("Unknown event: " + event);
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Failed to parse message: " + message);
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
