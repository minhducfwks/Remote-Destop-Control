package remote.common;

import java.io.Serializable;
import java.net.InetAddress;

public class RequestConnection implements Serializable {
    private String deviceName;
    private String typeOs;
    private String username;
    private String password;
    private InetAddress address;
    private int PORT;

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int pORT) {
        PORT = pORT;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getTypeOs() {
        return typeOs;
    }

    public void setTypeOs(String typeOs) {
        this.typeOs = typeOs;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress srcAddress) {
        this.address = srcAddress;
    }

    @Override
    public String toString() {
        return "RequestConnection [deviceName=" + deviceName + ", typeOs=" + typeOs + ", username=" + username
                + ", password=" + password + ", srcAddress=" + address + "]";
    }

}
