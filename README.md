# Webys

Webys is a desktop web browser built with **Java** and **JavaScript**.

## Stack

- Java 17+
- JavaFX 21
- JavaFX WebView
- JavaScript
- Maven

## Project structure

```text
Webys/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/jergan/webys/Webys.java
    └── resources/web/
        ├── index.html
        ├── browser.js
        └── style.css
```

## Run

Install JDK 17+ and Maven, then run:

```bash
mvn javafx:run
```

Webys opens a desktop browser window with navigation controls, an address bar, page loading, and a JavaScript bridge.

## Java ↔ JavaScript bridge

The Java application exposes `window.webys` to loaded pages. The initial bridge provides:

```javascript
webys.getName();
webys.getVersion();
webys.log("Hello from JavaScript");
```

The bridge is intentionally small so more browser APIs can be added later.

## Roadmap

- Tabs
- Bookmarks
- History
- Downloads
- Webys settings
- Custom new-tab page
- Keyboard shortcuts
- JavaScript/browser APIs
- Windows packaging and installer
