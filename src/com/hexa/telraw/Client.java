package com.hexa.telraw;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class Attachment {
}

public class Client implements CompletionHandler<Void, Attachment> {

    public static int BUF_READ_SIZE = 1024;

    private class Reader extends Thread {
        private boolean running;

        @Override
        public void run() {
            if (channel == null)
                return;
            ByteBuffer buf = ByteBuffer.allocate(BUF_READ_SIZE);
            int n;

            running = true;
            while (running) {
                buf.clear();
                Future<Integer> ret = channel.read(buf);

                try {
                    n = ret.get();
                    System.err.println("Client.Reader: read " + n + " bytes\n");

                    if (n > 0) {
                        byte[] raw = new byte[n];
                        buf.get(raw);
                        /*
                         * String str = new String(raw, StandardCharsets.UTF_8);
                         * System.err.println("Buffer: [" + str + "]\n");
                         */
                        if (dataObs != null)
                            dataObs.onReceive(raw);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    private String host;
    private int port;
    private AsynchronousSocketChannel channel;
    private boolean connected;
    private ConnectionObserver conObs;
    private DataObserver dataObs;
    private Reader reader;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnectionObserver(ConnectionObserver obs) {
        this.conObs = obs;
    }

    public void setDataObserver(DataObserver obs) {
        this.dataObs = obs;
    }

    public void connect() throws IOException {
        channel = AsynchronousSocketChannel.open();
        SocketAddress serverAddr = new InetSocketAddress(host, port);
        channel.connect(serverAddr, null, this);
    }

    public void disconnect() throws IOException {
        if (reader != null)
            reader.running = false; // Stop reading thread
        if (channel != null)
            channel.close();

        try {
            reader.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        conObs.onDisconnected();
    }

    public void send(String str) {
        ByteBuffer buf = ByteBuffer.wrap(str.getBytes());
        channel.write(buf);
    }

    @Override
    public void completed(Void result, Attachment attachment) {
        connected = true;
        if (conObs != null)
            conObs.onConnected();
        // Start reading
        if (reader == null) {
            reader = new Reader();
            reader.start();
        }
    }

    @Override
    public void failed(Throwable exc, Attachment attachment) {
        connected = false;
        if (conObs != null)
            conObs.onConnectionFailure();
    }

}
