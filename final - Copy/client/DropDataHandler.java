package client;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import common.ConstantPacket;

public class DropDataHandler implements DropTargetListener {
    private final DatagramSocket socket;
    private final int serverPort;
    private final InetAddress serverAddress;

    public DropDataHandler(DatagramSocket _socket, int _serverPort, InetAddress _serverAddress) {
        this.serverPort = _serverPort;
        this.serverAddress = _serverAddress;
        this.socket = _socket;
    }

    @Override
    public void drop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
            int id = 0;
            for (File file : files) {
                // System.out.println(file.getAbsolutePath());
                send(file,id++);
            }
            dtde.dropComplete(true);
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    private void send(File file, int id) {
        byte[] fileBuffer = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(fileBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numsOfPacket = (int) Math.ceil((double) fileBuffer.length / (ConstantPacket.MAX_BUFFER_SIZE.value - 3));
        byte[] buffer = new byte[ConstantPacket.MAX_BUFFER_SIZE.value];

        for (int i = 0; i < numsOfPacket; i++) {
            buffer[0] = (byte) id;  
            buffer[1] = (byte) i;   
            buffer[2] = (byte) numsOfPacket;  

            int start = i * (ConstantPacket.MAX_BUFFER_SIZE.value - 3);
            int length = Math.min(ConstantPacket.MAX_BUFFER_SIZE.value - 3, fileBuffer.length - start);
            System.arraycopy(fileBuffer, start, buffer, 3, length);

            DatagramPacket packet = new DatagramPacket(buffer, length + 3, serverAddress, serverPort);
            try {
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Sent packet " + i + "/" + numsOfPacket);
        }

    }
}
