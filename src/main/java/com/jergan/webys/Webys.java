package com.jergan.webys;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Webys native runtime. JVE defines the project; HTML/CSS/JS define the UI. */
public class Webys extends Application {
    private WebEngine uiEngine;
    private WebEngine browserEngine;

    @Override
    public void start(Stage stage) {
        WebView uiView = new WebView();
        WebView browserView = new WebView();
        uiView.setPrefHeight(78);
        uiView.setMinHeight(78);
        uiView.setMaxHeight(78);

        uiEngine = uiView.getEngine();
        browserEngine = browserView.getEngine();
        browserEngine.setJavaScriptEnabled(true);

        WebysBridge bridge = new WebysBridge(browserEngine, uiEngine);

        uiEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) uiEngine.executeScript("window");
                window.setMember("webys", bridge);
                bridge.updateUi();
            }
        });

        browserEngine.locationProperty().addListener((obs, oldValue, newValue) -> bridge.updateUi());
        browserEngine.titleProperty().addListener((obs, oldValue, newValue) -> bridge.updateUi());
        browserEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) bridge.updateUi();
            if (newState == javafx.concurrent.Worker.State.FAILED) bridge.showError("Webys could not load this page.");
        });

        BorderPane root = new BorderPane();
        root.setTop(uiView);
        root.setCenter(browserView);

        stage.setTitle("Webys — Jergan Studio");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();

        loadLocalWeb("/web/index.html");
        bridge.navigate("https://www.google.com");
    }

    private void loadLocalWeb(String resource) {
        var url = getClass().getResource(resource);
        if (url == null) throw new IllegalStateException("Missing Webys web resource: " + resource);
        uiEngine.load(url.toExternalForm());
    }

    public static final class WebysBridge {
        private final WebEngine browser;
        private final WebEngine ui;

        WebysBridge(WebEngine browser, WebEngine ui) {
            this.browser = browser;
            this.ui = ui;
        }

        public String getName() { return "Webys"; }
        public String getVersion() { return "1.0.0"; }
        public String getAuthor() { return "Jergan Studio"; }

        public void navigate(String input) {
            if (input == null) return;
            String value = input.trim();
            if (value.isEmpty()) return;
            if (!value.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
                value = value.contains(".") && !value.contains(" ")
                        ? "https://" + value
                        : "https://www.google.com/search?q=" + encode(value);
            }
            browser.load(value);
        }

        public void back() { if (canGoBack()) browser.getHistory().go(-1); updateUi(); }
        public void forward() { if (canGoForward()) browser.getHistory().go(1); updateUi(); }
        public void reload() { browser.reload(); }
        public void home() { navigate("https://www.google.com"); }
        public void log(String message) { System.out.println("[Webys] " + message); }
        public String getUrl() { return browser.getLocation(); }
        public String getTitle() { return browser.getTitle(); }
        public boolean canGoBack() { return browser.getHistory().getCurrentIndex() > 0; }
        public boolean canGoForward() { return browser.getHistory().getCurrentIndex() < browser.getHistory().getEntries().size() - 1; }

        void updateUi() {
            try {
                JSObject window = (JSObject) ui.executeScript("window");
                window.call("webysLocationChanged", getUrl(), getTitle(), canGoBack(), canGoForward());
            } catch (Exception ignored) { }
        }

        void showError(String message) {
            try {
                JSObject window = (JSObject) ui.executeScript("window");
                window.call("webysError", message);
            } catch (Exception ignored) { }
        }

        private String encode(String text) { return URLEncoder.encode(text, StandardCharsets.UTF_8); }
    }

    public static void main(String[] args) { launch(args); }
}
