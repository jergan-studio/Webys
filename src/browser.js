export function normalizeUrl(input) {
  const value = input.trim();
  if (!value) return "https://www.google.com";
  if (/^[a-z][a-z0-9+.-]*:\/\//i.test(value)) return value;
  if (value.includes(".") && !value.includes(" ")) return `https://${value}`;
  return `https://www.google.com/search?q=${encodeURIComponent(value)}`;
}

export function webysLog(message) {
  console.log(`[Webys] ${message}`);
}
