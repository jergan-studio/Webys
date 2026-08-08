const searchForm = document.getElementById('searchForm');
const search = document.getElementById('search');
const status = document.getElementById('status');
const back = document.getElementById('back');
const forward = document.getElementById('forward');
const reload = document.getElementById('reload');
const home = document.getElementById('home');

searchForm.addEventListener('submit', (event) => {
  event.preventDefault();
  const value = search.value.trim();
  if (value && window.webys) window.webys.navigate(value);
});

back.onclick = () => window.webys?.back();
forward.onclick = () => window.webys?.forward();
reload.onclick = () => window.webys?.reload();
home.onclick = () => window.webys?.home();

window.webysLocationChanged = (url, title, canBack, canForward) => {
  if (url) search.value = url;
  back.disabled = !canBack;
  forward.disabled = !canForward;
  status.textContent = title ? `${title} • Webys` : 'Jergan Studio • Webys';
  document.title = title ? `Webys - ${title}` : 'Webys';
};

window.webysError = (message) => {
  status.textContent = message || 'Webys could not load this page.';
};
