package com.hexa.telraw;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TelRawController implements ConnectionObserver {
    @FXML
    private TextField txfHost;
    @FXML
    private TextField txfPort;
    @FXML
    private Button btnConnect;
    @FXML
    private ProgressIndicator pinConnect;
    @FXML
    private TextArea txaInput;
    @FXML
    private TextArea txaTx;
    @FXML
    private TextArea txaRx;
    @FXML
    private Button btnSend;

    private Client client;

    @FXML
    public void initialize() {
        pinConnect.setVisible(false);
    }

    @FXML
    protected void handleBtnConnectAction(ActionEvent event) {
        System.err.println("handleBtnConnectAction: connect " + txfHost.getText() + ":" + txfPort.getText());

        String host = txfHost.getText();
        String port = txfPort.getText();

        // TODO Verify host and port
        if (host.length() > 0 && port.length() > 0) {
            if (client == null) {
                btnConnect.setDisable(true);
                pinConnect.setVisible(true);

                client = new Client(host, Integer.valueOf(port));
                client.setConnectionObserver(this);
                try {
                    System.err.println("handleBtnConnectAction: connecting...");
                    client.connect();
                } catch (IOException e) {
                    btnConnect.setDisable(false);

                    // TODO Popup error
                    e.printStackTrace();
                }
            } else {
                btnConnect.setDisable(true);
                pinConnect.setVisible(true);

                try {
                    System.err.println("handleBtnConnectAction: disconnecting...");
                    client.disconnect();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    client = null;
                }
            }
        } else {
            // TODO Popup error
        }
    }

    @FXML
    protected void handleBtnSendAction(ActionEvent event) {
        System.err.println("handleSubmitButtonAction: " + txaInput.getText());
        txaTx.appendText(txaInput.getText() + "\n");
    }

    @Override
    public void onConnected() {
        System.err.println("onConnected");

        // Update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pinConnect.setVisible(false);
                btnConnect.setText("Disconnect"); // TODO
                btnConnect.setDisable(false);
            }
        });
    }

    @Override
    public void onConnectionFailure() {
        System.err.println("onConnectionFailure");

        // Update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pinConnect.setVisible(false);
                btnConnect.setText("Connect"); // TODO
                btnConnect.setDisable(false);
            }
        });
    }

    @Override
    public void onDisconnected() {
        System.err.println("onDisconnected");

        // Update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                pinConnect.setVisible(false);
                btnConnect.setText("Connect"); // TODO
                btnConnect.setDisable(false);
            }
        });
    }
}
