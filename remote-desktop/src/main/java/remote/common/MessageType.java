package remote.common;

public enum MessageType {
    IMAGE_TYPE(-1),
    STRING_TYPE(-2),
    MOUSE_ACTION_TYPE(-3),
    FILE_TYPE(-4);

    private int flag;

    private MessageType(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

}
