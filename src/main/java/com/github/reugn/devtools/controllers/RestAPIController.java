package com.github.reugn.devtools.controllers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.reugn.devtools.services.Request;
import com.github.reugn.devtools.services.RestService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


public class RestAPIController extends TabPaneController {

    @FXML
    private static RestAPIController self;
    @FXML
    private TextField searchField;
    @FXML
    ListView<String> historyListView;
    @FXML
    TabPane innerTabPane;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        self = this;
        
        historyListView.setOnMouseClicked(event -> {
        	if (event.getClickCount() == 2) {
		    	if (historyListView.getSelectionModel().isEmpty())
		    		return;
		    	
		    	try {
		    		String[] split = historyListView.getSelectionModel().getSelectedItem().split(" - ");
		    		this.handleNewTabwithData(RestService.REQ_HISTORY_MAP.get(split[2]));
		    	} catch (Exception e) {
		        	Logger.getLogger(getClass()).error(e.getMessage(), e);
				}
		    	
		    	event.consume();
        	}
        });
        
        MenuItem clearHistoryItem = new MenuItem("Clear History");
        clearHistoryItem.setOnAction(event -> {
        	historyListView.getItems().clear();
        	RestService.REQ_HISTORY_MAP.clear();
        });
        
        MenuItem openRequestItem = new MenuItem("Open request");
        openRequestItem.setOnAction(event -> {
        	if (historyListView.getSelectionModel().isEmpty())
        		return;
        	
        	try {
	    		String[] split = historyListView.getSelectionModel().getSelectedItem().split(" - ");
	    		this.handleNewTabwithData(RestService.REQ_HISTORY_MAP.get(split[2]));
	    	} catch (Exception e) {
	        	Logger.getLogger(getClass()).error(e.getMessage(), e);
			}
        });
        MenuItem exportItem = new MenuItem("Export to JSON");
        exportItem.setOnAction(event -> {
        	FileChooser fileChooser = new FileChooser();
    		File selectedFile = fileChooser.showSaveDialog(null);
    		
    		if (selectedFile == null) return;
    		try {
    			String json = new ObjectMapper().writeValueAsString(RestService.REQ_HISTORY_MAP.values());
    			json = json.replaceAll("\\\\t","").replaceAll("\\\\n","")
    					   .replaceAll("\\\\","").replaceAll("\"\\{", "{").replaceAll("}\"", "}");
    			Files.write(Paths.get(selectedFile.getPath()), json.getBytes(), StandardOpenOption.CREATE);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		Logger.getLogger(getClass()).info("File saved at " + new Date().toString());
        });
        MenuItem importItem = new MenuItem("Import from JSON");
        importItem.setOnAction(event -> {
        	FileChooser fileChooser = new FileChooser();
    		File selectedFile = fileChooser.showOpenDialog(null);
    		
    		if (selectedFile == null) return;
    		String content = null;
    		try {
    			content = Files.lines(Paths.get(selectedFile.getPath()))
                        .collect(Collectors.joining(System.lineSeparator()));
    			
    			ArrayNode arrayNode = new ObjectMapper().readValue(content, ArrayNode.class);
    			List<Request> reqs = new ArrayList<>();
    			for (int i = 0; i< arrayNode.size(); i++) {
    				Request req = new Request(arrayNode.get(i));
    				reqs.add(req);
    			}
    			RestService.fillReqMapFromList(reqs);
        		historyListView.setItems(FXCollections.observableArrayList(RestService.getReqHistory()));
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		Logger.getLogger(getClass()).info("File loaded at " + new Date().toString());
        });
        
        historyListView.setContextMenu(new ContextMenu(openRequestItem, exportItem, importItem, clearHistoryItem));
        
        VBox.setVgrow(historyListView, Priority.ALWAYS);
        searchField.setOnKeyPressed(event -> {
        	if (event.getCode() == KeyCode.ENTER) {
        		if (!searchField.getText().isEmpty()) {
	        		List<String> filtered = RestService.getReqHistory().stream().filter(x -> x.contains(searchField.getText())).collect(Collectors.toList());
	        		historyListView.setItems(FXCollections.observableArrayList(filtered));
        		}
        		else {
	        		historyListView.setItems(FXCollections.observableArrayList(RestService.getReqHistory()));
        		}
        	}
        });
        RestService.registerController(this);
    }

    static RestAPIController instance() {
        return self;
    }

    @Override
    protected String getInnerResource() {
        return "/views/rest_api_tab.fxml";
    }

	public ListView<String> getHistoryListView() {
		return historyListView;
	}
    
}
