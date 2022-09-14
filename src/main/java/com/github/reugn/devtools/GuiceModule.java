package com.github.reugn.devtools;

import com.github.reugn.devtools.services.*;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import javafx.fxml.FXMLLoader;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(AsciiService.class).to(AsciiServiceImpl.class).in(Scopes.SINGLETON);
        bind(EpochService.class).to(EpochServiceImpl.class).in(Scopes.SINGLETON);
        bind(HashService.class).to(HashServiceImpl.class).in(Scopes.SINGLETON);
        bind(JsonService.class).to(JsonServiceImpl.class).in(Scopes.SINGLETON);
        bind(LogService.class).to(LogServiceImpl.class).in(Scopes.SINGLETON);
        bind(RegexService.class).to(RegexServiceImpl.class).in(Scopes.SINGLETON);
        bind(RestService.class).to(RestServiceImpl.class).in(Scopes.SINGLETON);
    }

    @Provides
    public FXMLLoader getFXMLLoader(Injector injector) {
        FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(injector::getInstance);
        return loader;
    }
}
