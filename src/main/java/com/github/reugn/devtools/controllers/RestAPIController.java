package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.Request;
import com.github.reugn.devtools.services.RestService;
import com.github.reugn.devtools.utils.ReqHistoryListView;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class RestAPIController extends TabPaneController {

    @FXML
    private static RestAPIController self;
    @FXML
    private TextField searchField;
    @FXML
    ReqHistoryListView historyListView;
    @FXML
    TabPane innerTabPane;
    @FXML
    private SplitPane splitPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        self = this;

        historyListView.setParentController(this);

        VBox.setVgrow(historyListView, Priority.ALWAYS);

        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (!searchField.getText().isEmpty()) {
                    List<Request> filtered = RestService.getReqHistory().stream()
                            .filter(x -> x.toString().contains(searchField.getText())).collect(Collectors.toList());
                    historyListView.setItems(FXCollections.observableArrayList(filtered));
                } else {
                    historyListView.setItems(FXCollections.observableArrayList(RestService.getReqHistory()));
                }
            }
        });

        splitPane.setDividerPositions(0.3f, 0.7f);

        initInnerTabPaneContextMenu();
        RestService.registerController(this);
    }

    private void initInnerTabPaneContextMenu() {
        MenuItem closeCurrentItem = new MenuItem("Close Current Tab");
        closeCurrentItem.setOnAction(event -> {
            Tab currentTab = innerTabPane.getSelectionModel().getSelectedItem();
            if (currentTab != null && currentTab.isClosable())
                innerTabPane.getTabs().remove(currentTab);
        });
        MenuItem closeAllItem = new MenuItem("Close All Tabs");
        closeAllItem.setOnAction(event ->
                innerTabPane.getTabs()
                        .setAll(
                                FXCollections.observableArrayList(
                                        innerTabPane.getTabs()
                                                .stream()
                                                .filter(tab -> !tab.isClosable())
                                                .collect(Collectors.toList()))
                        )
        );
        innerTabPane.setContextMenu(new ContextMenu(closeCurrentItem, closeAllItem));
    }

    static RestAPIController instance() {
        return self;
    }

    @Override
    protected String getInnerResource() {
        return "/views/rest_api_tab.fxml";
    }

    public ListView<Request> getHistoryListView() {
        return historyListView;
    }

}
