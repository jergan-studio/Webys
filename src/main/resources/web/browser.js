const searchForm = document.getElementById('searchForm');
const search = document.getElementById('search');
const info = document.getElementById('info');

searchForm.addEventListener('submit', (event) => {
  event.preventDefault();
  const value = search.value.trim();
  if (!value) return;

  if (window.webys) {
    window.webys.log(`Search requested: ${value}`);
    info.textContent = `Webys ${window.webys.getVersion()} • JavaScript bridge connected`;
  }
});

if (window.webys) {
  info.textContent = `Webys ${window.webys.getVersion()} • JavaScript bridge connected`;
}
