package com.hexa.telraw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class TelRawController {
    @FXML
    private TextField txfHost;
    @FXML
    private TextField txfPort;
    @FXML
    private Button btnConnect;
    @FXML
    private TextArea txaInput;
    @FXML
    private TextArea txaTx;
    @FXML
    private TextArea txaRx;
    @FXML
    private Button btnSend;

    @FXML
    protected void handleBtnConnectAction(ActionEvent event) {
        System.out.println("handleBtnConnectAction: connect " + txfHost.getText() + ":" + txfPort.getText());
    }

    @FXML
    protected void handleBtnSendAction(ActionEvent event) {
        System.out.println("handleSubmitButtonAction: " + txaInput.getText());
        txaTx.appendText(txaInput.getText() + "\n");
    }
}
