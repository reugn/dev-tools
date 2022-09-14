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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger log = LogManager.getLogger(ReqHistoryListView.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private RestService restService;
    private RestAPIController parentController;

    public ReqHistoryListView() {
        this(null);
    }

    public ReqHistoryListView(RestAPIController controller) {
        super();
        this.parentController = controller;
        initContextMenu();

        setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openRequestWithData();
                event.consume();
            }
        });
    }

    private void initContextMenu() {
        MenuItem clearHistoryItem = new MenuItem("Clear History");
        clearHistoryItem.setOnAction(event -> {
            getItems().clear();
            restService.clearRequestHistory();
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
                String json = mapper.writeValueAsString(getItems())
                        .replaceAll("\\\\n", "")
                        .replaceAll("\\\\", "")
                        .replaceAll("\"\\{", "{")
                        .replaceAll("}\"", "}");
                Files.write(Paths.get(selectedFile.getPath()), json.getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e) {
                log.error("Error in initContextMenu", e);
            }
            log.info("File saved at {}", new Date());
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

                ArrayNode arrayNode = mapper.readValue(content, ArrayNode.class);
                List<Request> requestList = new ArrayList<>();
                for (int i = 0; i < arrayNode.size(); i++) {
                    Request request = new Request(arrayNode.get(i));
                    requestList.add(request);
                }
                restService.addToRequestHistory(requestList);
                setItems(FXCollections.observableArrayList(restService.getRequestHistory()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            log.info("File loaded at {}", new Date());
        });

        setContextMenu(new ContextMenu(openRequestItem,
                deleteRequestItem,
                importItem,
                exportItem,
                clearHistoryItem));
    }

    private void openRequestWithData() {
        if (getSelectionModel().isEmpty())
            return;

        if (parentController != null) {
            Request request = getSelectionModel().getSelectedItem();
            parentController.handleNewTabWithData(request);
        } else {
            log.error("Parent RestAPIController has not been set.");
        }
    }

    private void deleteRequest() {
        if (getSelectionModel().isEmpty())
            return;

        Request request = getSelectionModel().getSelectedItem();
        getItems().remove(request);
        restService.removeFromRequestHistory(request);
    }

    public void setParentController(RestAPIController controller) {
        this.parentController = controller;
        this.restService = parentController.getRestService();
    }
}
