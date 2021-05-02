package com.github.reugn.devtools.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.Request;
import com.github.reugn.devtools.services.RestService;
import javafx.collections.FXCollections;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReqHistoryListView extends ListView<Request> {

    private RestAPIController parentController;

    public ReqHistoryListView() {
        this(null);
    }

    public ReqHistoryListView(RestAPIController controller) {
        super();
        this.parentController = controller;
        this.initContextMenu();

        this.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openRequestWithData();
                event.consume();
            }
        });
    }

    private void initContextMenu() {
        MenuItem clearHistoryItem = new MenuItem("Clear History");
        clearHistoryItem.setOnAction(event -> {
            this.getItems().clear();
            RestService.REQ_HISTORY_LIST.clear();
        });

        MenuItem openRequestItem = new MenuItem("Open Request");
        openRequestItem.setOnAction(event -> openRequestWithData());

        MenuItem deleteRequestItem = new MenuItem("Delete Request");
        deleteRequestItem.setOnAction(event -> deleteRequest());

        MenuItem exportItem = new MenuItem("Export to JSON");
        exportItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showSaveDialog(null);

            if (selectedFile == null) return;
            try {
                String json = new ObjectMapper().writeValueAsString(this.getItems());
                json = json.replaceAll("\\\\n", "")
                        .replaceAll("\\\\", "")
                        .replaceAll("\"\\{", "{")
                        .replaceAll("}\"", "}");
                Files.write(Paths.get(selectedFile.getPath()), json.getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e) {
                Logger.getLogger(getClass()).error(e.getMessage(), e);
            }
            Logger.getLogger(getClass()).info("File saved at " + new Date().toString());
        });

        MenuItem importItem = new MenuItem("Import from JSON");
        importItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile == null) return;
            String content;
            try {
                content = Files.lines(Paths.get(selectedFile.getPath()))
                        .collect(Collectors.joining(System.lineSeparator()));

                ArrayNode arrayNode = new ObjectMapper().readValue(content, ArrayNode.class);
                List<Request> reqs = new ArrayList<>();
                for (int i = 0; i < arrayNode.size(); i++) {
                    Request req = new Request(arrayNode.get(i));
                    reqs.add(req);
                }
                RestService.addToHistoryReqList(reqs);
                this.setItems(FXCollections.observableArrayList(RestService.getReqHistory()));
            } catch (IOException e) {
                Logger.getLogger(getClass()).error(e.getMessage(), e);
            }
            Logger.getLogger(getClass()).info("File loaded at " + new Date().toString());
        });

        this.setContextMenu(new ContextMenu(openRequestItem, deleteRequestItem, importItem, exportItem, clearHistoryItem));
    }

    private void openRequestWithData() {
        if (this.getSelectionModel().isEmpty())
            return;

        try {
            if (parentController == null)
                throw new Exception("Parent RestAPIController has not been set!");

            Request req = this.getSelectionModel().getSelectedItem();
            parentController.handleNewTabwithData(req);
        } catch (Exception e) {
            Logger.getLogger(getClass()).error(e.getMessage(), e);
        }
    }

    private void deleteRequest() {
        if (this.getSelectionModel().isEmpty())
            return;

        Request req = this.getSelectionModel().getSelectedItem();
        this.getItems().remove(req);
    }

    public void setParentController(RestAPIController controller) {
        this.parentController = controller;
    }

}
