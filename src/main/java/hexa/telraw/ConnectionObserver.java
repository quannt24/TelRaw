package hexa.telraw;

public interface ConnectionObserver {

    void onConnected();

    void onConnectionFailure();

    void onDisconnected();

}
