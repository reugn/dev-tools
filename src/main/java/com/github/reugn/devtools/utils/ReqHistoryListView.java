package com.github.reugn.devtools.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.reugn.devtools.controllers.RestAPIController;
import com.github.reugn.devtools.models.HttpRequest;
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
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReqHistoryListView extends ListView<HttpRequest> {

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
                String json = mapper.writeValueAsString(getItems());
                Files.write(Paths.get(selectedFile.getPath()), json.getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e) {
                log.error("Failed to export to file", e);
            }
            log.info("File saved at {}", new Date());
        });

        MenuItem importItem = new MenuItem("Import from JSON");
        importItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile == null) return;

            try (Stream<String> stream = Files.lines(Paths.get(selectedFile.getPath()))) {
                String history = stream.collect(Collectors.joining(System.lineSeparator()));
                @SuppressWarnings("all")
                List<HttpRequest> requestList = mapper.readValue(history, new TypeReference<List<HttpRequest>>() {
                });
                restService.addToRequestHistory(requestList);
                setItems(FXCollections.observableArrayList(restService.getRequestHistory()));
            } catch (IOException e) {
                log.error("Failed to import from file", e);
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
        if (getSelectionModel().isEmpty()) {
            return;
        }

        if (parentController != null) {
            HttpRequest request = getSelectionModel().getSelectedItem();
            parentController.handleNewTabWithData(request);
        } else {
            log.error("Parent RestAPIController has not been set.");
        }
    }

    private void deleteRequest() {
        if (getSelectionModel().isEmpty()) {
            return;
        }

        HttpRequest request = getSelectionModel().getSelectedItem();
        getItems().remove(request);
        restService.removeFromRequestHistory(request);
    }

    public void setParentController(RestAPIController controller) {
        this.parentController = controller;
        this.restService = parentController.getRestService();
    }
}
