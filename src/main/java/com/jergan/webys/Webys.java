package com.jergan.webys;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class Webys extends Application {
    private WebEngine engine;
    private TextField addressBar;

    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();
        engine = webView.getEngine();
        engine.setJavaScriptEnabled(true);

        Button back = new Button("←");
        Button forward = new Button("→");
        Button reload = new Button("⟳");
        Button home = new Button("⌂");
        addressBar = new TextField("https://www.google.com");
        Button go = new Button("Go");

        addressBar.setOnAction(e -> navigate(addressBar.getText()));
        go.setOnAction(e -> navigate(addressBar.getText()));
        back.setOnAction(e -> { if (engine.getHistory().getCurrentIndex() > 0) engine.getHistory().go(-1); });
        forward.setOnAction(e -> { if (engine.getHistory().getCurrentIndex() < engine.getHistory().getEntries().size() - 1) engine.getHistory().go(1); });
        reload.setOnAction(e -> engine.reload());
        home.setOnAction(e -> navigate("https://www.google.com"));

        HBox toolbar = new HBox(6, back, forward, reload, home, addressBar, go);
        HBox.setHgrow(addressBar, javafx.scene.layout.Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setTop(toolbar);
        root.setCenter(webView);

        engine.locationProperty().addListener((obs, oldLocation, newLocation) -> addressBar.setText(newLocation));
        engine.titleProperty().addListener((obs, oldTitle, newTitle) -> {
            if (newTitle != null && !newTitle.isBlank()) stage.setTitle("Webys - " + newTitle);
            else stage.setTitle("Webys");
        });

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) installJavaScriptBridge();
        });

        stage.setTitle("Webys");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(new Scene(root, 1200, 800));
        stage.show();

        navigate("https://www.google.com");
    }

    private void navigate(String input) {
        String url = input.trim();
        if (url.isEmpty()) return;

        if (!url.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
            if (url.contains(".") && !url.contains(" ")) {
                url = "https://" + url;
            } else {
                url = "https://www.google.com/search?q=" + encode(url);
            }
        }
        engine.load(url);
    }

    private String encode(String text) {
        try {
            return java.net.URLEncoder.encode(text, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return text;
        }
    }

    private void installJavaScriptBridge() {
        try {
            JSObject window = (JSObject) engine.executeScript("window");
            window.setMember("webys", new WebysBridge());
        } catch (Exception ignored) {
            // Some pages restrict script access; the browser still works normally.
        }
    }

    public static class WebysBridge {
        public String getName() { return "Webys"; }
        public String getVersion() { return "0.1.0"; }
        public void log(String message) { System.out.println("[Webys] " + message); }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
