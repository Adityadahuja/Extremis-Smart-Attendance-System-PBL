// Extremis Attendance Service Worker for PWA installation
const CACHE_NAME = 'extremis-v1';
const ASSETS_TO_CACHE = [
  '/',
  '/css/extremis.css',
  '/js/extremis.js',
  '/manifest.json',
  '/icons/icon-192.svg'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(ASSETS_TO_CACHE).catch(() => {
        // Silently continue if some assets fail caching during initial install
      });
    })
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.map((key) => {
          if (key !== CACHE_NAME) {
            return caches.delete(key);
          }
        })
      );
    })
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  // Only cache static CSS, JS, and image assets for performance; network-first for pages and APIs
  const url = new URL(event.request.url);
  if (url.pathname.startsWith('/css/') || url.pathname.startsWith('/js/') || url.pathname.startsWith('/icons/')) {
    event.respondWith(
      caches.match(event.request).then((cachedResponse) => {
        return cachedResponse || fetch(event.request);
      })
    );
  } else {
    // Dynamic endpoints (auth, checkin, etc.) always go to network
    event.respondWith(fetch(event.request).catch(() => caches.match(event.request)));
  }
});
