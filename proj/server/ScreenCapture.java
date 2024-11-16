package server;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScreenCapture implements Runnable {
    private Robot robot;
    private Rectangle capture;
    private byte[] imageByteArray;

    public ScreenCapture(Robot robot, Rectangle capture) {
        this.robot = robot;
        this.capture = capture;
    }

    @Override
    public void run() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (true) {
            try {
                BufferedImage screenShot = robot.createScreenCapture(capture);

                baos.reset(); 
                ImageIO.write(screenShot, "jpeg", baos);

                synchronized (this) {
                    imageByteArray = baos.toByteArray();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized byte[] getImageByteArray() {
        return imageByteArray;
    }

}
