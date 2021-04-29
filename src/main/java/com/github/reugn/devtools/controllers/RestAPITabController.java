package com.github.reugn.devtools.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.github.reugn.devtools.models.RestResponse;
import com.github.reugn.devtools.services.JsonService;
import com.github.reugn.devtools.services.Request;
import com.github.reugn.devtools.services.RestService;
import com.github.reugn.devtools.utils.Elements;
import com.github.reugn.devtools.utils.HttpHeadersTextField;

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

public class RestAPITabController implements Initializable {

    private static final Logger log = Logger.getLogger(RestAPITabController.class);

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

    private void prettyPrint() {
        String data = responseBodyTextArea.getText();
        
        if (data.startsWith("[") || data.startsWith("{")) {
	        try {
	            String pretty = JsonService.format(data);
	            responseBodyTextArea.setText(pretty);
	        } catch (Exception e) {
	            log.warn("Could not pretty print");
	            responseBodyTextArea.setText(data);
	        }
        }
    }
    
    @FXML
    private void handleSend(ActionEvent actionEvent) {
        clear();
        
        if (!validateInput()) return;
        RestAPIController.instance().innerTabPane.getSelectionModel().getSelectedItem().setText(tabTitle());
    	
        if (!uriTextField.getText().startsWith("http://") && !uriTextField.getText().startsWith("https://")) {
        	this.requestFailed(new Exception("Not valid url !"));
        	return;
        }
    	
        Map<String, String> headers = requestHeadersVBox.getChildren().stream().map(n -> {
            List<Node> h = ((HBox) n).getChildren();
            return new Pair<>(((TextField) h.get(0)).getText(), ((TextField) h.get(1)).getText());
        }).filter(p -> !p.getKey().isEmpty() && !p.getValue().isEmpty())
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        
        sendButton.setDisable(true);
        Request req = new Request(uriTextField.getText(), methodComboBox.getValue(), headers, requestBodyTextArea.getText());
		RestService.requestAsync(req,this::requestCompleted, this::requestFailed);
        
    }

    private void requestFailed(Exception e) {
    	Platform.runLater(() -> {
    		sendButton.setDisable(false);
            responseStatusLabel.setText(e.getClass().getName() + ": " + e.getMessage());
    	});
    }
    
    private void requestCompleted(RestResponse res) {
    	Platform.runLater(() -> {
    		responseBodyTextArea.setText(res.getBody());
            this.prettyPrint();
            responseHeadersTextArea.setText(res.getHeaders());
            responseStatusLabel.setText("STATUS: " + res.getStatus() + ", TIME: " + res.getTime() + "ms");
            sendButton.setDisable(false);
    	});

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

    private static final int tabTitleLength = 12;

    String tabTitle() {
        String pUrl = uriTextField.getText().length() > tabTitleLength
                ? uriTextField.getText().substring(0, tabTitleLength)
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
            log.warn(e.getMessage(), e);
        }
    }

    @FXML
    private int handleRemoveHeader(ActionEvent actionEvent) {
        int hsize = requestHeadersVBox.getChildren().size();
        if (hsize > 1)
            requestHeadersVBox.getChildren().remove(hsize - 1);
        
        return requestHeadersVBox.getChildren().size();
    }
    
	public void loadRequest(Request req) {
		while (handleRemoveHeader(new ActionEvent()) > 1) {}
		
		uriTextField.setText(req.getUrl());
		requestBodyTextArea.setText(req.getBody());
		try {
			for (String key : req.getHeaders().keySet()) {
				HBox n = FXMLLoader.load(this.getClass().getResource("/views/rest_api_header.fxml"));
				HttpHeadersTextField htf = (HttpHeadersTextField) n.getChildren().get(0);
				htf.setText(key);
				TextField tf = (TextField) n.getChildren().get(1);
				tf.setText(req.getHeaders().get(key));

				requestHeadersVBox.getChildren()
						.add(n);
			}
			methodComboBox.getItems().forEach(x -> {
				if (x.equals(req.getMethod()))
					methodComboBox.getSelectionModel().select(x);
			});
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		}
	}
}
