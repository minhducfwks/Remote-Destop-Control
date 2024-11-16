package common;

public enum EventConstant {
    MOUSE_MOVE(-1),
    MOUSE_PRESS(-2),
    MOUSE_RELEASE(-3),
    KEY_PRESS(-4),
    KEY_RELEASE(-5);

    public final Integer value;
    private EventConstant(int _value){
        this.value = _value;
    }
}
