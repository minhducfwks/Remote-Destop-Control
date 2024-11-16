package client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import common.EventConstant;

public class ActionHandler implements KeyListener, MouseListener, MouseMotionListener {
    private DatagramSocket socket;
    private final InetAddress serverAddress;
    private final Integer serverPort;
    private double serverWidth, serverHeight, clientWidth, clientHeight;

    public ActionHandler(InetAddress _serverAddress, Integer _serverPort, double _serverWidth,
            double _serverHeight, double _clientWidth, double _clientHeight) {
        this.serverAddress = _serverAddress;
        this.serverPort = _serverPort;
        this.serverWidth = _serverWidth;
        this.serverHeight = _serverHeight;
        this.clientWidth = _clientWidth;
        this.clientHeight = _clientHeight;
        System.out.println(serverPort + " " + serverAddress);

        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        String command = "";
        command += EventConstant.KEY_PRESS.value;
        int xBtn = e.getKeyCode();
        command += ";" + xBtn;
        sendEvent(command);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String command = "";
        command += EventConstant.KEY_RELEASE.value;
        int xBtn = e.getKeyCode();
        command += ";" + xBtn;
        sendEvent(command);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double xScale = (double) serverWidth / clientWidth;
        double yScale = (double) serverHeight / clientHeight;

        String command = "";
        command += EventConstant.MOUSE_MOVE.value;
        command += ";";
        command += (int) (e.getX() * xScale);
        command += ";";
        command += (int) (e.getY() * yScale);
        sendEvent(command);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        String command = "";
        command += EventConstant.MOUSE_PRESS.value;
        int xBtn = e.getButton();
        command += ";" + xBtn;
        sendEvent(command);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        String command = "";
        command += EventConstant.MOUSE_RELEASE.value;
        int xBtn = e.getButton();
        command += ";" + xBtn;
        sendEvent(command);
    }

    private void sendEvent(String message) {
        try {
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.length(), serverAddress,
                    serverPort);
            System.out.println(message);
            socket.send(sendPacket);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
