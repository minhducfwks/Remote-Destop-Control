package remote.common;

import java.io.Serializable;

public class MessagePacket implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String checksum;
    private MessageType messageType;
    private byte[] data;

    public MessagePacket() {
    }

    public MessagePacket(int id, String checksum, MessageType messageType, byte[] data) {
        this.id = id;
        this.checksum = checksum;
        this.messageType = messageType;
        this.data = data;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MessagePacket{");
        sb.append("id=").append(id);
        sb.append(", checksum=").append(checksum);
        sb.append(", messageType=").append(messageType);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

}
