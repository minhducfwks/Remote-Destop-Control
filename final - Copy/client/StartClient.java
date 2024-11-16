package client;

import java.awt.Graphics;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import common.ConstantPacket;

public class StartClient extends JFrame {
    private DatagramSocket socket;
    private InetAddress serverIP;
    public BufferedImage image;

    public StartClient(String _serverIP) {
        this.setTitle("demo");
        this.setSize(500, 400);
        this.setDefaultCloseOperation(3);
        this.setDropTarget(new DropTarget(this, new DropDataHandler(socket, 8888, serverIP)));
        this.setVisible(true);
        try {
            this.serverIP = InetAddress.getByName(_serverIP);
        } catch (UnknownHostException e) {
            System.err.println("Undefine server address");
        }
        try {
            System.out.println("Start Client: Creating socket...");
            socket = new DatagramSocket();
            String message = "connect";

            byte[] buffer = message.getBytes();
            // redefine port after complete structure of proj
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverIP,
                    8888);
            socket.send(packet);
            System.out.println("Packet sent");
            buffer = new byte[ConstantPacket.MESSAGE_BUFFER_SIZE.value];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);
            message = new String(response.getData(), 0, response.getLength());
            String[] serverInfo = message.split("\\|");
            Integer screenPort = Integer.parseInt(serverInfo[0]);
            String multicastIP = serverInfo[1];
            Double screenWidth = Double.parseDouble(serverInfo[2]);
            Double screenHeight = Double.parseDouble(serverInfo[3]);
            Integer mousePort = Integer.parseInt(serverInfo[4]);
            Integer filePort = Integer.parseInt(serverInfo[5]);
            System.out.println(screenPort + " - " + multicastIP);

            new Thread(new ReceiverScreen(multicastIP, screenPort, this)).start();
            ActionHandler actionHandler = new ActionHandler(serverIP, mousePort, screenWidth,
                    screenHeight, this.getWidth(), this.getHeight());
            this.addKeyListener(actionHandler);
            this.addMouseListener(actionHandler);
            this.addMouseMotionListener(actionHandler);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        int newWidth = getWidth(); // Chiều rộng mới (có thể thay đổi tùy ý)
        int newHeight = getHeight(); // Chiều cao mới (có thể thay đổi tùy ý)

        g.drawImage(image, 0, 0, newWidth, newHeight, this);
    }

}