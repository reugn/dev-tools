package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.models.Request;
import com.google.inject.Provider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;

import static java.util.Objects.requireNonNull;

public abstract class TabPaneController implements Initializable {

    private static final Logger log = LogManager.getLogger(TabPaneController.class);

    protected final Provider<FXMLLoader> fxmlLoaderProvider;
    @FXML
    protected Tab addNewTab;
    @FXML
    protected TabPane innerTabPane;

    protected TabPaneController(Provider<FXMLLoader> fxmlLoaderProvider) {
        this.fxmlLoaderProvider = fxmlLoaderProvider;
    }

    protected abstract String getInnerResource();

    @FXML
    protected void handleNewTab() {
        if (addNewTab.isSelected()) {
            Tab newTab = new Tab("New");
            newTab.setContent(loadInnerResource(fxmlLoaderProvider.get()));
            updateTabPane(newTab);
        }
    }

    public void handleNewTabWithData(Request request) {
        FXMLLoader fxmlLoader = fxmlLoaderProvider.get();
        Node node = loadInnerResource(fxmlLoader);
        RestAPITabController controller = fxmlLoader.getController();
        controller.loadRequest(request);
        Tab newTab = new Tab(controller.tabTitle());
        newTab.setContent(node);
        updateTabPane(newTab);
    }

    private void updateTabPane(Tab newTab) {
        int tabsSize = innerTabPane.getTabs().size();
        Tab plusTab = innerTabPane.getTabs().remove(tabsSize - 1);
        innerTabPane.getTabs().addAll(newTab, plusTab);
        innerTabPane.getSelectionModel().select(newTab);
    }

    private Node loadInnerResource(FXMLLoader fxmlLoader) {
        try {
            fxmlLoader.setLocation(requireNonNull(getClass().getResource(getInnerResource())));
            return fxmlLoader.load();
        } catch (IOException e) {
            log.warn("Failed to load resource", e);
            throw new UncheckedIOException(e);
        }
    }
}
