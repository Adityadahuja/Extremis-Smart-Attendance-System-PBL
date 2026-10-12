/**
 * Extremis - Core Frontend Utilities
 * Minimal vanilla JS for Geolocation, Camera capture, and PWA registration.
 */

// Register Service Worker for PWA
if ('serviceWorker' in navigator) {
  window.addEventListener('load', () => {
    navigator.serviceWorker.register('/sw.js').then(
      (reg) => console.log('Extremis PWA ServiceWorker registered with scope:', reg.scope),
      (err) => console.warn('ServiceWorker registration failed:', err)
    );
  });
}

// Toast helper
function showToast(message, type = 'info') {
  let container = document.getElementById('toastContainer');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <div style="flex: 1; font-size: 0.9rem;">${message}</div>
    <button onclick="this.parentElement.remove()" style="background:none; border:none; color:#94a3b8; cursor:pointer;">&times;</button>
  `;
  container.appendChild(toast);

  setTimeout(() => {
    if (toast.parentElement) toast.remove();
  }, 4500);
}

// Modal helper
function openModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.add('show');
}

function closeModal(id) {
  const el = document.getElementById(id);
  if (el) el.classList.remove('show');
}

// Geolocation helper
function fetchCurrentPosition() {
  return new Promise((resolve, reject) => {
    if (!navigator.geolocation) {
      reject(new Error("Geolocation is not supported by your browser."));
      return;
    }
    navigator.geolocation.getCurrentPosition(
      (pos) => {
        resolve({
          lat: pos.coords.latitude,
          lng: pos.coords.longitude,
          accuracy: pos.coords.accuracy
        });
      },
      (err) => {
        let msg = "Could not obtain GPS location.";
        switch (err.code) {
          case err.PERMISSION_DENIED:
            msg = "Location permission denied. Please allow location access in your browser/device settings.";
            break;
          case err.POSITION_UNAVAILABLE:
            msg = "Location information unavailable. Check if device GPS is turned on.";
            break;
          case err.TIMEOUT:
            msg = "Location request timed out. Please retry with better signal.";
            break;
        }
        reject(new Error(msg));
      },
      {
        enableHighAccuracy: true,
        timeout: 15000,
        maximumAge: 0
      }
    );
  });
}
