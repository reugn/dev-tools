package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.Request;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

public abstract class TabPaneController implements Initializable {

    private static final Logger log = LogManager.getLogger(TabPaneController.class);

    @FXML
    protected Tab addNewTab;
    @FXML
    protected TabPane innerTabPane;

    protected abstract String getInnerResource();

    @FXML
    protected void handleNewTab() {
        if (addNewTab.isSelected()) {
            int selected = innerTabPane.getSelectionModel().getSelectedIndex();
            int tabsSize = innerTabPane.getTabs().size();
            Tab plusTab = innerTabPane.getTabs().remove(tabsSize - 1);
            Tab newTab = new Tab("New");
            try {
                newTab.setContent(FXMLLoader.load(
                        requireNonNull(getClass().getResource(getInnerResource()))));
            } catch (Exception e) {
                log.warn("Failed to load tab content", e);
            }
            innerTabPane.getTabs().addAll(newTab, plusTab);
            innerTabPane.getSelectionModel().select(selected);
        }
    }

    public void handleNewTabWithData(Request request) {
        try {
            int tabsSize = innerTabPane.getTabs().size();
            Tab plusTab = innerTabPane.getTabs().remove(tabsSize - 1);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(getInnerResource()));
            Node node = fxmlLoader.load();
            RestAPITabController controller = fxmlLoader.getController();
            controller.loadRequest(request);
            Tab newTab = new Tab(controller.tabTitle());
            newTab.setContent(node);
            innerTabPane.getTabs().addAll(newTab, plusTab);
            innerTabPane.getSelectionModel().select(newTab);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
