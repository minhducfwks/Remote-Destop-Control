package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import common.ConstantPacket;

public class ReceiverScreen implements Runnable {
    private MulticastSocket socket;
    private StartClient frame;

    public ReceiverScreen(String _multicastIP, int _multicastPort, StartClient frame) {
        try {
            this.frame = frame;
            socket = new MulticastSocket(_multicastPort);
            socket.joinGroup(InetAddress.getByName(_multicastIP));
            System.out.println("Start ReceiverScreen at  " + _multicastIP + ":" + _multicastPort);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[ConstantPacket.MAX_BUFFER_SIZE.value];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        Map<Integer, byte[]> data = new ConcurrentHashMap<>();
        int packetID = -1;
        int totalPacket = 0;
        try {
            while (true) {
                socket.receive(packet);
                byte[] subData = packet.getData();

                if (packetID < subData[0] || packetID == 0) {
                    data.clear();
                    packetID = subData[0];
                }
                totalPacket = subData[2];
                int pcount = subData[1];
                data.put(pcount, Arrays.copyOfRange(subData, 3, subData.length));
                if (pcount == totalPacket - 1 && data.size() == totalPacket) {
                    new Thread(new ImageCombine(frame, data)).start();
                } else if (pcount == totalPacket) {
                    data.clear();
                    System.out.println("Missing packet " + packetID);
                    packetID = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
