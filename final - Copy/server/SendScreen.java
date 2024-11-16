package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.imageio.ImageIO;

import common.ConstantPacket;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SendScreen implements Runnable {
    private final DatagramSocket serverSocket;
    private final InetAddress serverAddress;

    public SendScreen(DatagramSocket _serverSocket, InetAddress multicastIP) {
        this.serverSocket = _serverSocket;
        this.serverAddress = multicastIP;
    }

    @Override
    public void run() {
        try {
            Robot robot = new Robot();
            Rectangle capture = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[ConstantPacket.MAX_BUFFER_SIZE.value];

            int packetID = 0;
            while (true) {
                try {
                    BufferedImage screenShot = robot.createScreenCapture(capture);
                    baos.reset();
                    ImageIO.write(screenShot, "jpeg", baos);

                    byte[] imageByteArray = baos.toByteArray();
                    int numsOfPacket = (int) Math
                            .ceil((double) imageByteArray.length / (ConstantPacket.MAX_BUFFER_SIZE.value - 3));
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress,
                            ConstantPORT.SCREEN_PORT.value);
                    for (int i = 0; i < numsOfPacket; i++) {
                        buffer[0] = (byte) packetID;
                        buffer[1] = (byte) i;
                        buffer[2] = (byte) numsOfPacket;

                        int start = i * (ConstantPacket.MAX_BUFFER_SIZE.value - 3);
                        int length = Math.min(ConstantPacket.MAX_BUFFER_SIZE.value - 3, imageByteArray.length - start);
                        System.arraycopy(imageByteArray, start, buffer, 3, length);
                        serverSocket.send(packet);
                        // System.out.println("Send packet " + packetID + " " + i + "/" + numsOfPacket);
                    }
                    packetID %= 127;
                    packetID++;
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (HeadlessException e) {
            e.printStackTrace();
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }
}
