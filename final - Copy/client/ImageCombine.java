package client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

public class ImageCombine implements Runnable {
    private StartClient frame;
    private Map<Integer, byte[]> data;

    public ImageCombine(StartClient frame, Map<Integer, byte[]> data) {
        this.frame = frame;
        this.data = data;
    }

    @Override
    public void run() {
        BufferedImage image = null;
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        try {
            for (byte[] packetData : data.values()) {
                byteStream.write(packetData);
            }
            byte[] imageByteArray = byteStream.toByteArray();
            image = ImageIO.read(new ByteArrayInputStream(imageByteArray));
            if (image != null) {
                frame.image = image;
                frame.repaint();
            } else {
                System.err.println("Image reading failed!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                byteStream.close(); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
