package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.Logger;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

abstract public class TabPaneController implements Initializable, Logger {

    @FXML
    protected Tab addNewTab;
    @FXML
    protected TabPane innerTabPane;

    protected abstract String getInnerResource();

    @FXML
    protected void handleNewTab(Event event) {
        if (addNewTab.isSelected()) {
            int selected = innerTabPane.getSelectionModel().getSelectedIndex();
            int n = innerTabPane.getTabs().size();
            Tab plus = innerTabPane.getTabs().remove(n - 1);
            Tab newTab = new Tab("New");
            try {
                newTab.setContent(FXMLLoader.load(this.getClass().getResource(getInnerResource())));
            } catch (Exception e) {
            }
            innerTabPane.getTabs().addAll(newTab, plus);
            innerTabPane.getSelectionModel().select(selected);
        }
    }
}
