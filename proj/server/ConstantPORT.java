package server;
public enum ConstantPORT {
    REQUEST_PORT(8888),
    SCREEN_PORT(8989),
    MOUSE_PORT(8787),
    FILE_PORT(8899);
    public final Integer value;
    private ConstantPORT(Integer _port){
        this.value = _port;
    }
}
