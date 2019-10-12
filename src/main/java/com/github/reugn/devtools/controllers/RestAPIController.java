package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class RestAPIController implements Initializable, Logger {

    @FXML
    private Tab addNewTab;
    @FXML
    TabPane restHistoryTabPane;
    @FXML
    private static RestAPIController self;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        self = this;
    }

    static RestAPIController instance() {
        return self;
    }

    @FXML
    private void handleNewTab(Event event) {
        if (addNewTab.isSelected()) {
            int selected = restHistoryTabPane.getSelectionModel().getSelectedIndex();
            int n = restHistoryTabPane.getTabs().size();
            Tab plus = restHistoryTabPane.getTabs().remove(n - 1);
            Tab newTab = new Tab("New");
            try {
                newTab.setContent(FXMLLoader.load(this.getClass().getResource("/views/rest_api_tab.fxml")));
            } catch (Exception e) {
            }
            restHistoryTabPane.getTabs().addAll(newTab, plus);
            restHistoryTabPane.getSelectionModel().select(selected);
        }
    }
}
