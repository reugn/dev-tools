package com.github.reugn.devtools.controllers;

import com.github.reugn.devtools.utils.ResourceLoader;
import com.google.inject.Provider;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public abstract class TabPaneController extends ResourceLoader implements Initializable {

    @FXML
    protected Tab addNewTab;
    @FXML
    protected TabPane innerTabPane;

    protected TabPaneController(Provider<FXMLLoader> fxmlLoaderProvider) {
        super(fxmlLoaderProvider);
    }

    protected abstract String getInnerResource();

    @FXML
    protected void handleNewTab() {
        if (addNewTab.isSelected()) {
            Tab newTab = new Tab("New");
            newTab.setContent(loadFXML(getInnerResource()));
            updateTabPane(newTab);
        }
    }

    protected void updateTabPane(Tab newTab) {
        int tabsSize = innerTabPane.getTabs().size();
        Tab plusTab = innerTabPane.getTabs().remove(tabsSize - 1);
        innerTabPane.getTabs().addAll(newTab, plusTab);
        innerTabPane.getSelectionModel().select(newTab);
    }
}
