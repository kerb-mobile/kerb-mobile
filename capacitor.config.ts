import type { CapacitorConfig } from '@capacitor/cli';

/**
 * Kerb mobile shells.
 *
 * The apps are native iOS/Android projects that load the existing, unmodified
 * https://www.kerbgo.com site in the platform WebView. Nothing is rebuilt or
 * re-hosted here: the app talks to the same cto.new deployment, the same
 * sessions, database, dispatch, payments and Mapbox integration as the browser.
 *
 * `www/` is not the app UI. It only holds the branded offline screen that
 * Capacitor shows (server.errorPath) when the live site cannot be reached.
 */
const config: CapacitorConfig = {
  appId: 'com.kerbgo.app',
  appName: 'Kerb',
  webDir: 'www',
  server: {
    // Live site, unmodified.
    url: 'https://www.kerbgo.com',
    // Keep Kerb's own pages inside the app WebView. Anything else (Stripe 3-D
    // Secure, tel:, mailto:, external map apps) is handed to the system.
    allowNavigation: ['www.kerbgo.com', 'kerbgo.com', '*.kerbgo.com'],
    // Branded failure screen instead of a blank WebView when the site is
    // unreachable. Shipped in www/index.html.
    errorPath: 'index.html',
  },
  ios: {
    // Reserve space for the notch/home indicator; WKWebView keeps its own cookie
    // jar, so Kerb's session cookie persists between launches unchanged.
    contentInset: 'automatic',
  },
  android: {
    // Load-bearing: keeps the WebView origin https://www.kerbgo.com so the
    // site's session cookie and localStorage stay first-party.
    androidScheme: 'https',
  },
  plugins: {
    SplashScreen: {
      launchShowDuration: 1200,
      launchAutoHide: true,
      backgroundColor: '#0c0f11',
      showSpinner: false,
      iosSpinnerStyle: 'small',
    },
    Keyboard: {
      resize: 'native',
      resizeOnFullScreen: true,
    },
  },
};

export default config;
