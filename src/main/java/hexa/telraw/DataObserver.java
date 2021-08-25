package hexa.telraw;

public interface DataObserver {

    void onReceive(byte[] raw);
    void onSent(byte[] raw);
    void onSendFail(byte[] raw);

}
