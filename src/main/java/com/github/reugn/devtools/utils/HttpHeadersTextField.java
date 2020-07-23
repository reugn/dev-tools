package com.github.reugn.devtools.utils;

import com.google.common.net.HttpHeaders;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class HttpHeadersTextField extends TextField {

    private static final Logger log = Logger.getLogger(HttpHeadersTextField.class);

    private static final SortedSet<String> entries;
    private static final int maxEntries = 10;

    private final ContextMenu entriesPopup;

    static {
        entries = new TreeSet<>();
        Field[] declaredFields = HttpHeaders.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                try {
                    entries.add((String) field.get(null));
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
    }

    public HttpHeadersTextField() {
        super();
        entriesPopup = new ContextMenu();
        textProperty().addListener((observableValue, s, s2) -> {
            if (getText().length() == 0) {
                entriesPopup.hide();
            } else {
                LinkedList<String> searchResult = new LinkedList<>(entries.subSet(getText(),
                        getText() + Character.MAX_VALUE));
                if (entries.size() > 0) {
                    populatePopup(searchResult);
                    if (!entriesPopup.isShowing()) {
                        entriesPopup.show(HttpHeadersTextField.this, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });
        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> entriesPopup.hide());
    }

    public SortedSet<String> getEntries() {
        return entries;
    }

    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(actionEvent -> {
                setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}