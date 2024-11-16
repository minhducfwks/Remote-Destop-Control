package common;

public enum ConstantPacket {
    MAX_BUFFER_SIZE(30000),
    MESSAGE_BUFFER_SIZE(127);

    public final Integer value;

    private ConstantPacket(int _value) {
        this.value = _value;
    }
}
