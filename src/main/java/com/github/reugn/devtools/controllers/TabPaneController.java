package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.Request;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.log4j.Logger;

abstract public class TabPaneController implements Initializable {

    private static final Logger log = Logger.getLogger(TabPaneController.class);

    @FXML
    protected Tab addNewTab;
    @FXML
    protected TabPane innerTabPane;

    protected abstract String getInnerResource();

    @FXML
    protected void handleNewTab() {
        if (addNewTab.isSelected()) {
            int selected = innerTabPane.getSelectionModel().getSelectedIndex();
            int n = innerTabPane.getTabs().size();
            Tab plus = innerTabPane.getTabs().remove(n - 1);
            Tab newTab = new Tab("New");
            try {
                newTab.setContent(FXMLLoader.load(this.getClass().getResource(getInnerResource())));
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
            innerTabPane.getTabs().addAll(newTab, plus);
            innerTabPane.getSelectionModel().select(selected);
        }
    }

    public void handleNewTabwithData(Request request) {
        try {
            int n = innerTabPane.getTabs().size();
            Tab plus = innerTabPane.getTabs().remove(n - 1);
            FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(getInnerResource()));
            Node node = fxmlLoader.load();
            RestAPITabController controller = fxmlLoader.getController();
            controller.loadRequest(request);
            Tab newTab = new Tab(controller.tabTitle());
            newTab.setContent(node);
            innerTabPane.getTabs().addAll(newTab, plus);
            innerTabPane.getSelectionModel().select(newTab);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
