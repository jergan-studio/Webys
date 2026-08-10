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
    <button id="go">Go</button>
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
const go = document.querySelector<HTMLButtonElement>("#go")!;

function normalizeUrl(input: string): string {
  const value = input.trim();
  if (!value) return "https://www.google.com";
  if (/^[a-z][a-z0-9+.-]*:\/\//i.test(value)) return value;
  if (value.includes(".") && !value.includes(" ")) return `https://${value}`;
  return `https://www.google.com/search?q=${encodeURIComponent(value)}`;
}

async function navigate(input: string): Promise<void> {
  const url = normalizeUrl(input);
  address.value = url;
  const label = `web-${Date.now()}`;
  const webview = new WebviewWindow(label, {
    url,
    title: `Webys - ${url}`,
    width: 1200,
    height: 742,
    resizable: true,
  });
  webview.once("tauri://error", (event) => console.error("Webys WebView error:", event));
}

address.addEventListener("keydown", (event) => {
  if (event.key === "Enter") void navigate(address.value);
});
go.addEventListener("click", () => void navigate(address.value));
home.addEventListener("click", () => void navigate("https://www.google.com"));
back.addEventListener("click", () => console.log("Webys back requested"));
forward.addEventListener("click", () => console.log("Webys forward requested"));
reload.addEventListener("click", () => location.reload());
void back;
void forward;
void reload;
