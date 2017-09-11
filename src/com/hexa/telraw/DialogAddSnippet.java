package com.hexa.telraw;

import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class DialogAddSnippet extends Dialog<Snippet> {

    private VBox root;
    private Label lblName;
    private TextField txfName;
    private Label lblContent;
    private TextArea txaContent;

    ButtonType bttOk;
    ButtonType bttCancel;

    public DialogAddSnippet() {
        this(null);
    }

    public DialogAddSnippet(String content) {
        this.setTitle("Add snippet");
        root = new VBox(4);

        lblName = new Label("Name:");
        root.getChildren().add(lblName);

        txfName = new TextField();
        txfName.setPromptText("Snippet name");
        root.getChildren().add(txfName);

        lblContent = new Label("Content:");
        root.getChildren().add(lblContent);

        txaContent = new TextArea();
        txaContent.setPromptText("Snippet content");
        root.getChildren().add(txaContent);

        this.getDialogPane().setContent(root);

        bttOk = new ButtonType("OK", ButtonData.OK_DONE);
        bttCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(bttOk);
        this.getDialogPane().getButtonTypes().add(bttCancel);

        this.setResultConverter(new Callback<ButtonType, Snippet>() {
            @Override
            public Snippet call(ButtonType btt) {
                if (btt == bttOk) {
                    return new Snippet(txfName.getText(), txaContent.getText());
                }
                return null;
            }
        });

        txaContent.setText(content);
    }

}
