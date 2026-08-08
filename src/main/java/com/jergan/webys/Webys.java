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

/**
 * Webys desktop shell.
 *
 * Workflow is intentionally similar to Websity:
 * Java starts the native window -> loads web/index.html -> JavaScript controls
 * the browser shell through the window.webys bridge -> Java drives WebView.
 */
public class Webys extends Application {
    private WebEngine uiEngine;
    private WebEngine browserEngine;

    @Override
    public void start(Stage stage) {
        WebView uiView = new WebView();
        WebView browserView = new WebView();

        uiEngine = uiView.getEngine();
        browserEngine = browserView.getEngine();
        browserEngine.setJavaScriptEnabled(true);

        WebysBridge bridge = new WebysBridge();
        bridge.setBrowser(browserEngine);
        bridge.setUi(uiEngine);

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
        if (url == null) {
            throw new IllegalStateException("Missing Webys web resource: " + resource);
        }
        uiEngine.load(url.toExternalForm());
    }

    public static class WebysBridge {
        private WebEngine browser;
        private WebEngine ui;

        void setBrowser(WebEngine browser) { this.browser = browser; }
        void setUi(WebEngine ui) { this.ui = ui; }

        public String getName() { return "Webys"; }
        public String getVersion() { return "1.0.0"; }
        public String getAuthor() { return "Jergan Studio"; }

        public void navigate(String input) {
            if (browser == null || input == null) return;
            String value = input.trim();
            if (value.isEmpty()) return;

            if (!value.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
                if (value.contains(".") && !value.contains(" ")) {
                    value = "https://" + value;
                } else {
                    value = "https://www.google.com/search?q=" + encode(value);
                }
            }
            browser.load(value);
        }

        public void back() {
            if (browser != null && browser.getHistory().getCurrentIndex() > 0) browser.getHistory().go(-1);
            updateUi();
        }

        public void forward() {
            if (browser != null && browser.getHistory().getCurrentIndex() < browser.getHistory().getEntries().size() - 1) browser.getHistory().go(1);
            updateUi();
        }

        public void reload() {
            if (browser != null) browser.reload();
        }

        public void home() {
            navigate("https://www.google.com");
        }

        public void log(String message) {
            System.out.println("[Webys] " + message);
        }

        public String getUrl() {
            return browser == null ? "" : browser.getLocation();
        }

        public String getTitle() {
            return browser == null ? "" : browser.getTitle();
        }

        public boolean canGoBack() {
            return browser != null && browser.getHistory().getCurrentIndex() > 0;
        }

        public boolean canGoForward() {
            return browser != null && browser.getHistory().getCurrentIndex() < browser.getHistory().getEntries().size() - 1;
        }

        void updateUi() {
            if (ui == null) return;
            try {
                JSObject window = (JSObject) ui.executeScript("window");
                window.call("webysLocationChanged", getUrl(), getTitle(), canGoBack(), canGoForward());
            } catch (Exception ignored) {
                // UI may not be ready yet.
            }
        }

        void showError(String message) {
            if (ui == null) return;
            try {
                JSObject window = (JSObject) ui.executeScript("window");
                window.call("webysError", message);
            } catch (Exception ignored) { }
        }

        private String encode(String text) {
            return URLEncoder.encode(text, StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
