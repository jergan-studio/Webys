import "./style.css";
import { WebviewWindow } from "@tauri-apps/api/webviewWindow";

const app = document.querySelector<HTMLDivElement>("#app")!;

app.innerHTML = `
  <header class="toolbar">
    <button id="back">←</button>
    <button id="forward">→</button>
    <button id="reload">⟳</button>
    <button id="home">⌂</button>
    <input id="address" placeholder="Search or enter URL" autocomplete="off" />
    <span class="brand">Webys</span>
  </header>
  <main class="home">
    <h1>Webys</h1>
    <p>Jergan Studio</p>
  </main>
`;

const address = document.querySelector<HTMLInputElement>("#address")!;
const back = document.querySelector<HTMLButtonElement>("#back")!;
const forward = document.querySelector<HTMLButtonElement>("#forward")!;
const reload = document.querySelector<HTMLButtonElement>("#reload")!;
const home = document.querySelector<HTMLButtonElement>("#home")!;

function normalizeUrl(input: string): string {
  const value = input.trim();
  if (!value) return "https://www.google.com";
  if (/^[a-z][a-z0-9+.-]*:\/\//i.test(value)) return value;
  if (value.includes(".") && !value.includes(" ")) return `https://${value}`;
  return `https://www.google.com/search?q=${encodeURIComponent(value)}`;
}

async function navigate(input: string) {
  const url = normalizeUrl(input);
  address.value = url;
  // Browser navigation will be moved into the Tauri WebView window as tabs are added.
  console.log("Webys navigate:", url);
}

address.addEventListener("keydown", (event) => {
  if (event.key === "Enter") navigate(address.value);
});

back.addEventListener("click", () => console.log("Webys back"));
forward.addEventListener("click", () => console.log("Webys forward"));
reload.addEventListener("click", () => location.reload());
home.addEventListener("click", () => navigate("https://www.google.com"));

void WebviewWindow;
