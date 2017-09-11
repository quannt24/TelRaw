package com.hexa.telraw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class TelRawController implements ConnectionObserver, DataObserver {
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
    private TextArea txaTxChar;
    @FXML
    private TextArea txaRxChar;
    @FXML
    private Button btnSend;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnClearInput;
    @FXML
    private ListView<String> ltvSnippet;
    @FXML
    private Button btnClearComm;

    private Client client;
    private ObservableList<String> listSnipStr = FXCollections.observableArrayList();
    private ArrayList<Snippet> listSnippet;

    public TelRawController() {
        System.err.println("Path: " + System.getProperty("user.dir"));
        listSnippet = new ArrayList<>();

        // Load snippets from file
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(System.getProperty("user.dir") + "\\snippet.txt");
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line1 = null;
            String line2 = null;
            while ((line1 = br.readLine()) != null) {
                if ((line2 = br.readLine()) != null) {
                    Snippet snip = new Snippet(line1, line2);
                    listSnippet.add(snip);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
                if (isr != null)
                    isr.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize() {
        pinConnect.setVisible(false);

        for (int i = 0; i < listSnippet.size(); i++)
            listSnipStr.add(listSnippet.get(i).toString());

        ltvSnippet.setItems(listSnipStr);
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
                client.setDataObserver(this);
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
        System.err.println("handleBtnSendAction: " + txaInput.getText());
        if (client.isConnected()) {
            // Parse input string
            client.send(RawStringParser.toRaw(txaInput.getText()));
        } else {
            // TODO Popup error
        }
    }

    @FXML
    protected void handleBtnSaveAction(ActionEvent event) {
        DialogAddSnippet dialog = new DialogAddSnippet(txaInput.getText());
        Optional<Snippet> optSnip = dialog.showAndWait();
        if (optSnip.isPresent()) {
            listSnippet.add(optSnip.get());
            listSnipStr.add(optSnip.get().toString());
        }
    }

    @FXML
    protected void handleBtnClearInputAction(ActionEvent event) {
        txaInput.clear();
    }

    @FXML
    protected void handleLtvSnippetOnMouseClicked(MouseEvent event) {
        int i = ltvSnippet.getSelectionModel().getSelectedIndex();
        if (i >= 0)
            txaInput.setText(listSnippet.get(i).getContent());
    }

    @FXML
    protected void handleBtnClearCommAction(ActionEvent event) {
        txaTx.clear();
        txaRx.clear();
        txaTxChar.clear();
        txaRxChar.clear();
    }

    /*
     * Implement ConnectionObserver
     * -------------------------------------------------------
     */

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

        client = null;
    }

    /*
     * Implement DataObserver
     * -------------------------------------------------------
     */
    @Override
    public void onReceive(byte[] raw) {
        // Update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txaRx.appendText(RawStringParser.toReadable(raw) + "\n");
                txaTx.appendText("\n");
                txaRxChar.appendText(RawStringParser.toReadableChar(raw) + "\n");
                txaTxChar.appendText("\n");
            }
        });
    }

    @Override
    public void onSent(byte[] raw) {
        // Update UI
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txaTx.appendText(RawStringParser.toReadable(raw) + "\n");
                txaRx.appendText("\n");
                txaTxChar.appendText(RawStringParser.toReadableChar(raw) + "\n");
                txaRxChar.appendText("\n");
            }
        });
    }

    @Override
    public void onSendFail(byte[] raw) {
        // TODO Auto-generated method stub
        System.err.println("onSendFail");
    }
}
