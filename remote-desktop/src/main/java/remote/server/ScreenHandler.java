package remote.server;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

import remote.common.RequestConnection;

public class ScreenHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ScreenHandler.class.getName());
    private static final int PACKET_SIZE = 1024; // Max packet size
    private static final int HEADER_SIZE = 4; // Size of the header
    private static final float JPEG_QUALITY = 0.75f; // JPEG quality (0.0 - 1.0)
    private static final int TIMEOUT = 1000;

    private final List<RequestConnection> clients;
    private final DatagramSocket datagramSocket;
    private volatile boolean running = true; // Flag to control the running state

    private Robot robot;
    Rectangle screenRect;
    BufferedImage screenFullImage;

    public ScreenHandler(DatagramSocket serverDatagram, List<RequestConnection> clients) {
        this.datagramSocket = serverDatagram;
        this.clients = clients;
        try {
            robot = new Robot();
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            screenFullImage = robot.createScreenCapture(screenRect);
        } catch (AWTException ex) {
            logger.log(Level.SEVERE, " Error while capturing  the screen", ex);
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                synchronized (clients) {
                    while (clients.isEmpty()) {
                        logger.log(Level.INFO, "No clients connected. Waiting for clients...");
                        clients.wait();
                    }
                }
                // Capture screenshot
                byte[] screenshotData = captureScreenshot();
                synchronized (clients) {
                    for (RequestConnection client : clients) {
                        InetAddress clientAddress = client.getAddress();
                        int clientPort = client.getPORT();

                        logger.log(Level.INFO, "Sending screenshot to client: {0}:{1}, Size: {2} bytes",
                                new Object[] { clientAddress, clientPort, screenshotData.length });

                        sendScreenshotToClient(screenshotData, clientAddress, clientPort);
                    }
                }

                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Thread interrupted", e);
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IOException occurred", e);
            } catch (AWTException e) {
                logger.log(Level.SEVERE, "AWTException occurred", e);
            }
        }
    }

    public void stop() {
        running = false; // Set running to false to stop the loop
    }

    private byte[] captureScreenshot() throws AWTException, IOException {

        // Compress image with adjustable quality
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(JPEG_QUALITY);

        writer.setOutput(ImageIO.createImageOutputStream(byteArrayOutputStream));
        writer.write(null, new javax.imageio.IIOImage(screenFullImage, null, null), param);
        writer.dispose();

        return byteArrayOutputStream.toByteArray();
    }

    private void sendScreenshotToClient(byte[] screenshotData, InetAddress clientAddress, int clientPort)
            throws IOException {
        int offset = 0;
        int sequenceNumber = 0; // Số thứ tự cho gói tin

        while (offset < screenshotData.length) {
            int length = Math.min(PACKET_SIZE, screenshotData.length - offset);
            byte[] packetData = new byte[length + HEADER_SIZE]; // +4 cho số thứ tự

            // Gán số thứ tự
            System.arraycopy(intToByteArray(sequenceNumber), 0, packetData, 0, HEADER_SIZE);
            System.arraycopy(screenshotData, offset, packetData, HEADER_SIZE, length);

            // Tạo gói tin UDP và gửi tới client
            DatagramPacket packet = new DatagramPacket(packetData, packetData.length, clientAddress, clientPort);
            datagramSocket.send(packet);

            // Bắt đầu bộ đếm thời gian cho ACK
            if (!awaitAck(sequenceNumber, clientAddress, clientPort)) {
                // Nếu không nhận được ACK, gửi lại gói tin này
                logger.log(Level.WARNING, "Resending packet: {0}", sequenceNumber);
                datagramSocket.send(packet);
            }

            offset += length; // Di chuyển offset để gửi phần tiếp theo
            sequenceNumber++; // Tăng số thứ tự
        }
    }

    private boolean awaitAck(int sequenceNumber, InetAddress clientAddress, int clientPort) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < TIMEOUT) {
            byte[] ackBuffer = new byte[HEADER_SIZE];
            DatagramPacket ackPacket = new DatagramPacket(ackBuffer, ackBuffer.length);
            try {
                datagramSocket.setSoTimeout(TIMEOUT);
                datagramSocket.receive(ackPacket);
                int ackNumber = byteArrayToInt(ackBuffer);
                if (ackNumber == sequenceNumber) {
                    return true; // Nhận được ACK
                }
            } catch (SocketTimeoutException e) {
                logger.log(Level.INFO, "Timeout waiting for ACK", e);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "IOException in awaitAck", e);
            }
        }
        return false;
    }

    private int byteArrayToInt(byte[] bytes) {
        return ((bytes[0] & 0xFF) << 24) |
                ((bytes[1] & 0xFF) << 16) |
                ((bytes[2] & 0xFF) << 8) |
                (bytes[3] & 0xFF);
    }

    private byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

}
