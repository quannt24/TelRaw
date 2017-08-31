package com.hexa.telraw;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class TelRawController {
    @FXML
    private TextArea txaInput;
    @FXML
    private TextArea txaTx;
    @FXML
    private TextArea txaRx;
    @FXML
    private Button btnSend;

    @FXML
    protected void handleBtnSendAction(ActionEvent event) {
        System.out.println("handleSubmitButtonAction: " + txaInput.getText());
        txaTx.appendText(txaInput.getText() + "\n");
    }
}
