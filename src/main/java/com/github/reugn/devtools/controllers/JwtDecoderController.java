package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.JwtToken;
import com.github.reugn.devtools.services.JwtService;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static com.github.reugn.devtools.models.JwtToken.*;

public class JwtDecoderController implements Initializable {

    private static final Logger log = LogManager.getLogger(JwtDecoderController.class);

    private final JwtService jwtService;

    @FXML
    private TextArea tokenTextArea;
    @FXML
    private TextArea headerTextArea;
    @FXML
    private TextArea payloadTextArea;
    @FXML
    private TextArea secretTextArea;
    @FXML
    private TextArea publicKeyTextArea;
    @FXML
    private TextArea privateKeyTextArea;
    @FXML
    private Label secretLabel;
    @FXML
    private Label publicKeyLabel;
    @FXML
    private Label privateKeyLabel;
    @FXML
    private ChoiceBox<String> algorithmChoiceBox;
    @FXML
    private Label messageLabel;

    @Inject
    public JwtDecoderController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        algorithmChoiceBox.getItems().addAll(
                HS256, HS384, HS512,
                RS256, RS384, RS512,
                ES256, ES384, ES512
        );
        algorithmChoiceBox.getSelectionModel().select(0);

        tokenTextArea.setWrapText(true);
        setVisibleSecret(true);
    }

    @SuppressWarnings("unused")
    public void handleVerify(ActionEvent actionEvent) {
        if (tokenTextArea.getText().isEmpty()) {
            setMessageError("Token is not specified");
            return;
        }
        messageLabel.setText("");
        boolean valid;
        try {
            valid = jwtService.verify(tokenTextArea.getText(), buildSignature(),
                    algorithmChoiceBox.getValue());
        } catch (Exception e) {
            log.error("Error in handleVerify", e);
            valid = false;
        }
        if (valid) {
            setMessageSuccess("Signature verified");
        } else {
            setMessageError("Invalid signature");
        }
    }

    private JwtToken.Signature buildSignature() {
        return JwtToken.Signature.create()
                .withSecret(secretTextArea.getText())
                .withPublicKey(publicKeyTextArea.getText())
                .withPrivateKey(privateKeyTextArea.getText())
                .build();
    }

    @SuppressWarnings("unused")
    public void handleDecode(ActionEvent actionEvent) {
        if (tokenTextArea.getText().isEmpty()) {
            setMessageError("Token is not specified");
            return;
        }
        messageLabel.setText("");
        try {
            JwtToken token = jwtService.decode(tokenTextArea.getText());
            headerTextArea.setText(token.getHeader());
            payloadTextArea.setText(token.getPayload());
        } catch (Exception e) {
            log.error("Error in handleDecode", e);
            setMessageError("Failed to decode token");
        }
    }

    @SuppressWarnings("unused")
    public void handleEncode(ActionEvent actionEvent) {
        if (headerTextArea.getText().isEmpty()) {
            setMessageError("Header is not specified");
            return;
        }
        if (payloadTextArea.getText().isEmpty()) {
            setMessageError("Payload is not specified");
            return;
        }
        if (secretTextArea.isVisible() && secretTextArea.getText().isEmpty()) {
            setMessageError("Secret is not specified");
            return;
        }
        if (publicKeyTextArea.isVisible() && publicKeyTextArea.getText().isEmpty()) {
            setMessageError("Public key is not specified");
            return;
        }
        if (privateKeyTextArea.isVisible() && privateKeyTextArea.getText().isEmpty()) {
            setMessageError("Private key is not specified");
            return;
        }
        messageLabel.setText("");
        try {
            JwtToken token = new JwtToken(
                    headerTextArea.getText(),
                    payloadTextArea.getText(),
                    buildSignature()
            );
            String encoded = jwtService.encode(token, algorithmChoiceBox.getValue());
            tokenTextArea.setText(encoded);
        } catch (Exception e) {
            log.error("Error in handleEncode", e);
            setMessageError("Failed to encode token");
        }
    }

    @SuppressWarnings("unused")
    public void handleClear(ActionEvent actionEvent) {
        tokenTextArea.clear();
        headerTextArea.clear();
        payloadTextArea.clear();
        secretTextArea.clear();
        publicKeyTextArea.clear();
        privateKeyTextArea.clear();
        messageLabel.setText("");
    }

    @SuppressWarnings("unused")
    public void handleAlgorithmChoice(ActionEvent actionEvent) {
        switch (algorithmChoiceBox.getValue()) {
            case HS256:
            case HS384:
            case HS512:
                setVisibleSecret(true);
                break;
            default:
                setVisibleSecret(false);
        }
    }

    private void setVisibleSecret(boolean b) {
        secretTextArea.setManaged(b);
        secretTextArea.setVisible(b);
        secretLabel.setManaged(b);
        secretLabel.setVisible(b);
        publicKeyTextArea.setManaged(!b);
        publicKeyTextArea.setVisible(!b);
        publicKeyLabel.setManaged(!b);
        publicKeyLabel.setVisible(!b);
        privateKeyTextArea.setManaged(!b);
        privateKeyTextArea.setVisible(!b);
        privateKeyLabel.setManaged(!b);
        privateKeyLabel.setVisible(!b);
    }

    @SuppressWarnings("SameParameterValue")
    private void setMessageSuccess(String message) {
        messageLabel.setTextFill(Color.GREEN);
        messageLabel.setText(message);
    }

    private void setMessageError(String message) {
        messageLabel.setTextFill(Color.RED);
        messageLabel.setText(message);
    }
}
