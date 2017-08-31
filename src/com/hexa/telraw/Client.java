package com.hexa.telraw;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

class Attachment {

}

public class Client implements CompletionHandler<Void, Attachment> {

    private class ReadHandler implements CompletionHandler<Long, Attachment> {

        @Override
        public void completed(Long result, Attachment attachment) {
            // TODO Auto-generated method stub

        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            // TODO Auto-generated method stub

        }

    }

    private class WriteHandler implements CompletionHandler<Integer, Attachment> {

        @Override
        public void completed(Integer result, Attachment attachment) {
            // TODO Auto-generated method stub

        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            // TODO Auto-generated method stub

        }

    }

    private String host;
    private int port;
    private AsynchronousSocketChannel channel;
    private boolean connected;
    private ConnectionObserver conObs;
    private DataObserver dataObs;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        connected = false;
    }
    
    public boolean isConnected() {
        return connected;
    }

    public void setDataObserver(DataObserver obs) {
        this.dataObs = obs;
    }

    public void setConnectionObserver(ConnectionObserver obs) {
        this.conObs = obs;
    }

    public void connect() throws IOException {
        channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress(host, port);
        channel.connect(serverAddr, null, this);
    }

    public void disconnect() {
    }

    public void send(String str) {
    }

    @Override
    public void completed(Void result, Attachment attachment) {
        connected = true;
        if (conObs != null) conObs.onConnected();
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        connected = false;
        if (conObs != null) conObs.onConnectionFailure();
    }

}
