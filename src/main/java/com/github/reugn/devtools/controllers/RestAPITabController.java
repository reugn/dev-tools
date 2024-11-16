package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.Request;
import com.github.reugn.devtools.models.RestResponse;
import com.github.reugn.devtools.services.JsonService;
import com.github.reugn.devtools.services.RestService;
import com.github.reugn.devtools.utils.Elements;
import com.github.reugn.devtools.utils.HttpHeadersTextField;
import com.github.reugn.devtools.utils.ResourceLoader;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RestAPITabController extends ResourceLoader implements Initializable {

    private static final Logger log = LogManager.getLogger(RestAPITabController.class);
    private static final String headerNodeResourcePath = "/views/rest_api_header.fxml";
    private static final int tabTitleLength = 12;

    private final RestService restService;
    private final JsonService jsonService;
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
    @FXML
    private TabPane responseTabPane;
    @FXML
    private VBox responseVbox;

    @Inject
    public RestAPITabController(Provider<FXMLLoader> fxmlLoaderProvider,
                                RestService restService, JsonService jsonService) {
        super(fxmlLoaderProvider);
        this.restService = restService;
        this.jsonService = jsonService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        uriTextField.setPrefWidth(420);
        HBox.setMargin(methodComboBox, new Insets(10, 5, 10, 5));
        HBox.setMargin(uriTextField, new Insets(10, 5, 10, 0));
        HBox.setMargin(sendButton, new Insets(10, 5, 10, 0));
        VBox.setVgrow(responseHeadersTextArea, Priority.ALWAYS);
        VBox.setVgrow(responseTabPane, Priority.ALWAYS);
        VBox.setVgrow(responseVbox, Priority.ALWAYS);

        methodComboBox.getItems().setAll("GET", "POST", "PUT", "DELETE");
        methodComboBox.setValue("GET");

        requestHeadersVBox.setPadding(new Insets(10));
        addHeader();
    }

    @FXML
    private void handleSend(@SuppressWarnings("unused") ActionEvent actionEvent) {
        clear();
        if (!validateInput()) return;

        RestAPIController.instance().innerTabPane.getSelectionModel().getSelectedItem().setText(tabTitle());
        Map<String, String> headers = requestHeadersVBox.getChildren().stream()
                .map(n -> {
                    List<Node> h = ((HBox) n).getChildren();
                    return new Pair<>(((TextField) h.get(0)).getText(), ((TextField) h.get(1)).getText());
                })
                .filter(p -> !p.getKey().isEmpty() && !p.getValue().isEmpty())
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));

        sendButton.setDisable(true);
        Request request = new Request(uriTextField.getText(),
                methodComboBox.getValue(),
                headers,
                requestBodyTextArea.getText());
        restService.request(request, this::requestCompleted, this::requestFailed);
    }

    private void requestFailed(Throwable e) {
        Platform.runLater(() -> {
            sendButton.setDisable(false);
            responseStatusLabel.setText(e.getClass().getName() + ": " + e.getMessage());
        });
    }

    private void requestCompleted(RestResponse res) {
        Platform.runLater(() -> {
            responseBodyTextArea.setText(tryPrettyPrint(res.getBody()));
            responseHeadersTextArea.setText(res.getHeaders());
            responseStatusLabel.setText(String.format("STATUS: %d, TIME: %dms", res.getStatus(), res.getTime()));
            sendButton.setDisable(false);
        });
    }

    private String tryPrettyPrint(String response) {
        if (response.startsWith("[") || response.startsWith("{")) {
            try {
                return jsonService.format(response);
            } catch (IOException e) {
                log.debug("Could not pretty print: {}", response);
            }
        }
        return response;
    }

    private void clear() {
        uriTextField.setBorder(Border.EMPTY);
        responseStatusLabel.setText("");
        responseBodyTextArea.setText("");
        responseHeadersTextArea.setText("");
    }

    private boolean validateInput() {
        if (uriTextField.getText().isEmpty()) {
            uriTextField.setBorder(Elements.alertBorder);
            return false;
        }
        return true;
    }

    String tabTitle() {
        String pUrl = uriTextField.getText().length() > tabTitleLength
                ? uriTextField.getText().substring(0, tabTitleLength)
                : uriTextField.getText();
        return methodComboBox.getValue() + " " + pUrl;
    }

    @FXML
    private void handleAddHeader(@SuppressWarnings("unused") ActionEvent actionEvent) {
        addHeader();
    }

    private void addHeader() {
        Node header = loadFXML(headerNodeResourcePath);
        requestHeadersVBox.getChildren().add(header);
    }

    @FXML
    private int handleRemoveHeader(@SuppressWarnings("unused") ActionEvent actionEvent) {
        int headerSize = requestHeadersVBox.getChildren().size();
        if (headerSize > 1)
            requestHeadersVBox.getChildren().remove(headerSize - 1);

        return requestHeadersVBox.getChildren().size();
    }

    public void loadRequest(Request req) {
        while (handleRemoveHeader(new ActionEvent()) > 1) {
        }

        uriTextField.setText(req.getUrl());
        requestBodyTextArea.setText(req.getBody());

        boolean first = true;
        for (Map.Entry<String, String> entry : req.getHeaders().entrySet()) {
            if (first) {
                HBox header = (HBox) requestHeadersVBox.getChildren().get(0);
                setHeader(header, entry.getKey(), entry.getValue());
                first = false;
            } else {
                HBox header = (HBox) loadFXML(headerNodeResourcePath);
                setHeader(header, entry.getKey(), entry.getValue());
                requestHeadersVBox.getChildren().add(header);
            }
        }

        methodComboBox.getItems().forEach(httpMethod -> {
            if (httpMethod.equals(req.getMethod())) {
                methodComboBox.getSelectionModel().select(httpMethod);
            }
        });
    }

    private void setHeader(HBox header, String key, String value) {
        HttpHeadersTextField htf = (HttpHeadersTextField) header.getChildren().get(0);
        htf.setText(key);
        TextField tf = (TextField) header.getChildren().get(1);
        tf.setText(value);
    }
}
