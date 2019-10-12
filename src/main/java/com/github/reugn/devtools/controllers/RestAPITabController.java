package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.RestResponse;
import com.github.reugn.devtools.services.RestService;
import com.github.reugn.devtools.utils.Elements;
import com.github.reugn.devtools.utils.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RestAPITabController implements Initializable, Logger {

    @FXML
    private ComboBox<String> methodComboBox;
    @FXML
    private TextField uriTextField;
    @FXML
    private Button sendButton;
    @FXML
    private VBox requestHeadersVBox;
    @FXML
    private TextArea requestBodyTextArea;
    @FXML
    private TextArea responseBodyTextArea;
    @FXML
    private TextArea responseHeadersTextArea;
    @FXML
    private Label responseStatusLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uriTextField.setPrefWidth(512);
        HBox.setMargin(methodComboBox, new Insets(10, 5, 10, 5));
        HBox.setMargin(uriTextField, new Insets(10, 5, 10, 0));
        HBox.setMargin(sendButton, new Insets(10, 5, 10, 0));

        methodComboBox.getItems().setAll("GET", "POST", "PUT", "DELETE");
        methodComboBox.setValue("GET");

        requestHeadersVBox.setPadding(new Insets(10));
        addHeader();
    }

    @FXML
    private void handleSend(ActionEvent actionEvent) {
        clear();
        if (!validateInput()) return;
        RestAPIController.instance().restHistoryTabPane.getSelectionModel().getSelectedItem().setText(tabTitle());
        try {
            Map<String, String> headers = requestHeadersVBox.getChildren().stream().map(n -> {
                List<Node> h = ((HBox) n).getChildren();
                return new Pair<>(((TextField) h.get(0)).getText(), ((TextField) h.get(1)).getText());
            }).filter(p -> !p.getKey().isEmpty() && !p.getValue().isEmpty())
                    .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
            RestResponse response = RestService.request(methodComboBox.getValue(),
                    uriTextField.getText(),
                    headers,
                    requestBodyTextArea.getText());
            responseBodyTextArea.setText(response.getBody());
            responseHeadersTextArea.setText(response.getHeaders());
            responseStatusLabel.setText("STATUS: " + response.getStatus() + ", TIME: " + response.getTime() + "ms");
        } catch (Exception e) {
            responseStatusLabel.setText(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    private void clear() {
        responseStatusLabel.setText("");
        responseBodyTextArea.setText("");
        responseHeadersTextArea.setText("");
    }

    private boolean validateInput() {
        uriTextField.setBorder(Border.EMPTY);
        if (uriTextField.getText().isEmpty()) {
            uriTextField.setBorder(Elements.alertBorder);
            return false;
        }
        return true;
    }

    private static int tabUrlLength = 12;

    private String tabTitle() {
        String pUrl = uriTextField.getText().length() > tabUrlLength
                ? uriTextField.getText().substring(0, tabUrlLength)
                : uriTextField.getText();
        return methodComboBox.getValue() + " " + pUrl;
    }

    @FXML
    private void handleAddHeader(ActionEvent actionEvent) {
        addHeader();
    }

    private void addHeader() {
        try {
            Node n = FXMLLoader.load(this.getClass().getResource("/views/rest_api_header.fxml"));
            requestHeadersVBox.getChildren().add(n);
        } catch (Exception e) {
        }
    }

    @FXML
    private void handleRemoveHeader(ActionEvent actionEvent) {
        int hsize = requestHeadersVBox.getChildren().size();
        if (hsize > 1)
            requestHeadersVBox.getChildren().remove(hsize - 1);
    }
}
