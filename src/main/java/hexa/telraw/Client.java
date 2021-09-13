package hexa.telraw;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class Attachment {
    byte[] msg;
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
                Future<Integer> ret = channel.read(buf);

                try {
                    n = ret.get();
                    System.err.println("Client.Reader: read " + n + " bytes\n");

                    if (n > 0) {
                        byte[] raw = new byte[n];
                        buf.rewind();
                        buf.get(raw);
                        buf.clear();
                        /*
                         * String str = new String(raw, StandardCharsets.UTF_8);
                         * System.err.println("Buffer: [" + str + "]\n");
                         */
                        if (dataObs != null)
                            dataObs.onReceive(raw);
                    } else if (n < 0) {
                        System.err.println("End of stream");
                        running = false;
                        if (conObs != null)
                            conObs.onDisconnected();
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    // TODO Auto-generated catch block
                    System.err.println("Connection is closing");
                    e.printStackTrace();
                }
            }
        }
    }

    private class WriteHandler implements CompletionHandler<Integer, Attachment> {

        @Override
        public void completed(Integer result, Attachment attachment) {
            if (attachment != null) {
                if (dataObs != null)
                    dataObs.onSent(attachment.msg);
            }
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            if (dataObs != null) {
                if (attachment != null)
                    dataObs.onSendFail(attachment.msg);
                else
                    dataObs.onSendFail(null);
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
    private WriteHandler writeHandler;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
        connected = false;
        writeHandler = new WriteHandler();
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
        if (channel != null)
            channel.close();

        if (reader != null) {
            reader.running = false; // Stop reading thread
            try {
                reader.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (conObs != null)
            conObs.onDisconnected();
    }

    public void send(byte[] msg) {
        ByteBuffer buf = ByteBuffer.wrap(msg);
        Attachment att = new Attachment();
        att.msg = msg;
        channel.write(buf, att, writeHandler);
    }

    /*
     * Implement CompletionHandler
     * -------------------------------------------------------
     */
    
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

    // Connection fails
    @Override
    public void failed(Throwable exc, Attachment attachment) {
        connected = false;
        if (conObs != null)
            conObs.onConnectionFailure();
    }

}
