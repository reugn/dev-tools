package com.github.reugn.devtools.utils;

import com.google.inject.Provider;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;

import static java.util.Objects.requireNonNull;

/**
 * Provides an interface for loading object hierarchies from FXML documents ensuring
 * proper dependency injection functionality.
 * <p>
 * Subclasses must provide a Guice-injected {@code Provider<FXMLLoader>}.
 */
public abstract class ResourceLoader {

    private static final Logger log = LogManager.getLogger(ResourceLoader.class);

    protected final Provider<FXMLLoader> fxmlLoaderProvider;

    protected ResourceLoader(Provider<FXMLLoader> fxmlLoaderProvider) {
        this.fxmlLoaderProvider = fxmlLoaderProvider;
    }

    protected Node loadFXML(String resourcePath) {
        return loadFXML(fxmlLoaderProvider.get(), resourcePath);
    }

    protected Node loadFXML(FXMLLoader fxmlLoader, String resourcePath) {
        try {
            fxmlLoader.setLocation(requireNonNull(getClass().getResource(resourcePath)));
            return fxmlLoader.load();
        } catch (IOException e) {
            log.warn("Error loading FXML from {}", resourcePath, e);
            throw new UncheckedIOException(e);
        }
    }
}
