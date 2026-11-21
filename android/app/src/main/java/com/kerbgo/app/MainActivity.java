package com.kerbgo.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

import com.getcapacitor.Bridge;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.BridgeWebViewClient;

import org.json.JSONArray;

/**
 * Kerb Android bridge.
 *
 * The WebView loads the live, unmodified https://www.kerbgo.com site (configured
 * in capacitor.config.ts). This activity adds the one piece of UI that has to be
 * native: the driver-only navigation chooser.
 *
 * Drivers get a "Navigate" control on the driver dashboard. Tapping it offers
 * Kerb's own in-app Mapbox map (the default, unchanged), Google Maps or Waze
 * (Apple Maps does not exist on Android). Riders never see it: the control only
 * appears on /driver, the dashboard that shows the active trip. Sub-pages such as
 * /driver/earnings and /driver/documents have no trip, so it stays hidden there.
 *
 * Destination hand-off: the dropoff address is read from one documented hook —
 * an element carrying `data-kerb-destination`. If the trip page does not expose
 * it, the app says so rather than opening a navigation app pointed at a guessed
 * address.
 */
public class MainActivity extends BridgeActivity {

    private static final String DRIVER_ROUTE = "/driver";

    /** Kerb brand orange (#FD5204). */
    private static final int BRAND_ORANGE = Color.rgb(253, 82, 4);

    /**
     * Runs inside the site's page when a driver taps Navigate.
     *
     * Contract (the only one):
     *   &lt;... data-kerb-destination="12 Main St, Springfield"&gt;
     *
     * The attribute value wins; the element's text is accepted as a convenience
     * for markup that renders the address as text. Nothing else on the page is
     * inspected, so a wrong address cannot be picked up from an unrelated link.
     */
    private static final String DESTINATION_SCRIPT =
            "(function () {"
            + "  var el = document.querySelector('[data-kerb-destination]');"
            + "  if (!el) { return ''; }"
            + "  var value = el.getAttribute('data-kerb-destination') || el.textContent || '';"
            + "  return value.replace(/\\s+/g, ' ').trim();"
            + "})();";

    private Button navigateButton;
    private final Handler routeHandler = new Handler(Looper.getMainLooper());
    private boolean isShowingChooser = false;
    private String riderPatchSource;
    private long lastPatchCheckAt;

    private final Runnable routeCheck = new Runnable() {
        @Override
        public void run() {
            refreshNavigateButton();
            routeHandler.postDelayed(this, 800);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        installHttpErrorGuard();
        addNavigateButton();
        handleBackNavigation();
    }

    /**
     * Keeps 401 and 403 off the outage screen.
     *
     * Capacitor loads the bundled error page for any main-frame HTTP error, which would
     * report a withheld page as downtime. A 401 or 403 means the site is up and declined
     * this request — an expired session, a permission rule, or bot protection — so the
     * app must not offer a Retry card for it. Same rule as iOS.
     *
     * Capacitor exposes no hook for this, but it does expose the client itself, so the
     * bridge's client is swapped for one that skips the swap on those two statuses and
     * defers to Capacitor for everything else.
     */
    private void installHttpErrorGuard() {
        Bridge bridge = getBridge();
        if (bridge == null) {
            return;
        }
        bridge.setWebViewClient(new BridgeWebViewClient(bridge) {
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if (request.isForMainFrame() && isWithheld(errorResponse)) {
                    return;
                }
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
    }

    private static boolean isWithheld(WebResourceResponse response) {
        if (response == null) {
            return false;
        }
        int status = response.getStatusCode();
        return status == 401 || status == 403;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Coming back from the background is the moment to re-check a dead screen,
        // so the usual interval is skipped once.
        lastRecoveryProbeAt = 0;
        routeHandler.removeCallbacks(routeCheck);
        routeHandler.post(routeCheck);
    }

    @Override
    public void onPause() {
        routeHandler.removeCallbacks(routeCheck);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        routeHandler.removeCallbacks(routeCheck);
        super.onDestroy();
    }

    /**
     * The site is a multi-page web app, so Android's back button should walk the
     * WebView's history before it leaves Kerb.
     */
    private void handleBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                WebView webView = getBridge() != null ? getBridge().getWebView() : null;
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

    private void addNavigateButton() {
        ViewGroup root = findViewById(android.R.id.content);
        if (root == null) {
            return;
        }

        float density = getResources().getDisplayMetrics().density;
        int margin = Math.round(16 * density);
        int paddingH = Math.round(18 * density);
        int paddingV = Math.round(10 * density);

        navigateButton = new Button(this);
        navigateButton.setText("Navigate");
        navigateButton.setAllCaps(false);
        navigateButton.setTextColor(Color.WHITE);
        navigateButton.setTextSize(14f);
        navigateButton.setPadding(paddingH, paddingV, paddingH, paddingV);

        GradientDrawable background = new GradientDrawable();
        background.setColor(BRAND_ORANGE);
        background.setCornerRadius(24 * density);
        navigateButton.setBackground(background);
        navigateButton.setVisibility(View.GONE);
        navigateButton.setOnClickListener(v -> showNavigationChooser());

        // The shell patch pins a tab bar along the bottom of the page, so the
        // pill has to float above it instead of sitting on the Account tab.
        int insetBottom = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsets windowInsets = getWindow().getDecorView().getRootWindowInsets();
            if (windowInsets != null) {
                insetBottom = windowInsets.getInsets(WindowInsets.Type.systemBars()).bottom;
            }
        }
        int tabBarClearance = Math.round(56 * density) + insetBottom;

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.END | Gravity.BOTTOM);
        params.setMargins(0, 0, margin, margin + tabBarClearance);

        if (root instanceof FrameLayout) {
            root.addView(navigateButton, params);
        } else {
            root.addView(navigateButton);
        }
    }

    private void refreshNavigateButton() {
        if (navigateButton == null) {
            return;
        }
        String path = currentPath();
        boolean isDriverRoute = DRIVER_ROUTE.equals(path) || (DRIVER_ROUTE + "/").equals(path);
        navigateButton.setVisibility(isDriverRoute ? View.VISIBLE : View.GONE);
        if (isDriverRoute) {
            navigateButton.bringToFront();
        }
        applyShellPatchIfNeeded(path);
        watchServerHealth();
    }

    // --- Server health -------------------------------------------------------
    //
    // Capacitor already swaps a main-frame HTTP error for the bundled error screen,
    // so Android needs no status probe of its own. What Capacitor does not do is
    // leave that screen once, so this watches only while it is up and reloads the
    // site when it serves again. Nothing here runs during normal use.

    private static final String REMOTE_URL = "https://www.kerbgo.com/";

    /** Past this age a probe is treated as lost, never as still in flight. */
    private static final long HEALTH_CHECK_TIMEOUT_MS = 10000;

    private long lastRecoveryProbeAt;
    private long healthCheckStartedAt;

    private interface HealthCallback {
        void onResult(boolean alive);
    }

    private boolean healthCheckInFlight() {
        if (healthCheckStartedAt == 0) {
            return false;
        }
        if (System.currentTimeMillis() - healthCheckStartedAt > HEALTH_CHECK_TIMEOUT_MS) {
            healthCheckStartedAt = 0;
            return false;
        }
        return true;
    }

    private void watchServerHealth() {
        if (getBridge() == null) {
            return;
        }
        WebView webView = getBridge().getWebView();
        String errorUrl = getBridge().getErrorUrl();
        if (webView == null || errorUrl == null) {
            return;
        }
        String url = webView.getUrl();
        if (url == null || !url.startsWith(errorUrl)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastRecoveryProbeAt < 5000 || healthCheckInFlight()) {
            return;
        }
        lastRecoveryProbeAt = now;
        probeRemote(alive -> {
            if (alive) {
                webView.loadUrl(REMOTE_URL);
            }
        });
    }

    private void probeRemote(HealthCallback callback) {
        healthCheckStartedAt = System.currentTimeMillis();
        new Thread(() -> {
            boolean alive = false;
            java.net.HttpURLConnection connection = null;
            try {
                connection = (java.net.HttpURLConnection) new java.net.URL(REMOTE_URL).openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                connection.setUseCaches(false);
                int code = connection.getResponseCode();
                alive = code > 0 && code < 400;
            } catch (Exception e) {
                alive = false;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            final boolean result = alive;
            routeHandler.post(() -> {
                healthCheckStartedAt = 0;
                callback.onResult(result);
            });
        }).start();
    }

    /**
     * Runs the bundled presentation patch (assets/public/shell-tweak.js) while the
     * WebView is on the rider home or the driver home.
     *
     * The patch is idempotent, so the shell asks the page whether it is already
     * installed and only injects when it is not. Checking every route tick means
     * the native chrome appears as soon as the page has the markup it needs,
     * instead of up to a throttle interval later on a cold start.
     */
    private void applyShellPatchIfNeeded(String path) {
        if (path == null || !("/app".equals(path) || "/app/".equals(path)
                || "/driver".equals(path) || "/driver/".equals(path))) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastPatchCheckAt < 400) {
            return;
        }
        lastPatchCheckAt = now;

        WebView webView = getBridge() != null ? getBridge().getWebView() : null;
        if (webView == null) {
            return;
        }
        if (riderPatchSource == null) {
            riderPatchSource = readAsset("public/shell-tweak.js");
        }
        if (riderPatchSource == null || riderPatchSource.isEmpty()) {
            return;
        }
        webView.evaluateJavascript("!!window.__kerbRiderPatchInstalled", value -> {
            if ("true".equals(value)) {
                return;
            }
            String currentPath = getRoutePath();
            if (currentPath == null || !("/app".equals(currentPath) || "/app/".equals(currentPath)
                    || "/driver".equals(currentPath) || "/driver/".equals(currentPath))) {
                return;
            }
            webView.evaluateJavascript(riderPatchSource, null);
        });
    }

    private String getRoutePath() {
        WebView webView = getBridge() != null ? getBridge().getWebView() : null;
        if (webView == null || webView.getUrl() == null) {
            return null;
        }
        return android.net.Uri.parse(webView.getUrl()).getPath();
    }

    private String readAsset(String name) {
        java.io.InputStream in = null;
        try {
            in = getAssets().open(name);
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            return out.toString("UTF-8");
        } catch (Exception e) {
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception ignored) {
                    // nothing to do
                }
            }
        }
    }

    private String currentPath() {
        try {
            WebView webView = getBridge().getWebView();
            if (webView == null) {
                return null;
            }
            String url = webView.getUrl();
            if (url == null) {
                return null;
            }
            return Uri.parse(url).getPath();
        } catch (Exception e) {
            return null;
        }
    }

    private void showNavigationChooser() {
        if (isShowingChooser) {
            return;
        }
        isShowingChooser = true;

        CharSequence[] options = new CharSequence[] { "Kerb map (in app)", "Google Maps", "Waze" };
        new AlertDialog.Builder(this)
                .setTitle("Navigate with")
                .setItems(options, (dialog, which) -> {
                    isShowingChooser = false;
                    if (which == 1) {
                        launchGoogleMaps();
                    } else if (which == 2) {
                        launchWaze();
                    }
                })
                .setOnDismissListener(dialog -> isShowingChooser = false)
                .show();
    }

    private void launchGoogleMaps() {
        resolveDestination(destination -> {
            if (destination == null) {
                showMissingDestination();
                return;
            }
            String encoded = Uri.encode(destination);
            if (!openUri("google.navigation:q=" + encoded + "&mode=d")) {
                if (!openUri("https://www.google.com/maps/dir/?api=1&destination=" + encoded)) {
                    showAppNotInstalled("Google Maps");
                }
            }
        });
    }

    private void launchWaze() {
        resolveDestination(destination -> {
            if (destination == null) {
                showMissingDestination();
                return;
            }
            // Waze's universal link opens the app when installed and the website
            // otherwise, so drivers always get turn-by-turn directions.
            String encoded = Uri.encode(destination);
            if (!openUri("waze://?q=" + encoded + "&navigate=yes")
                    && !openUri("https://waze.com/ul?q=" + encoded + "&navigate=yes")) {
                showAppNotInstalled("Waze");
            }
        });
    }

    private interface DestinationCallback {
        void onResult(String destination);
    }

    private void resolveDestination(DestinationCallback callback) {
        try {
            WebView webView = getBridge().getWebView();
            if (webView == null) {
                callback.onResult(null);
                return;
            }
            webView.evaluateJavascript(DESTINATION_SCRIPT, value -> {
                String destination = decodeJavaScriptString(value);
                callback.onResult(destination.isEmpty() ? null : destination);
            });
        } catch (Exception e) {
            callback.onResult(null);
        }
    }

    /** evaluateJavascript returns a JSON-encoded value, e.g. "\"12 Main St\"". */
    private static String decodeJavaScriptString(String raw) {
        if (raw == null) {
            return "";
        }
        String value = raw.trim();
        if (value.isEmpty() || "null".equals(value) || "undefined".equals(value)) {
            return "";
        }
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) {
            try {
                return new JSONArray("[" + value + "]").getString(0).trim();
            } catch (Exception e) {
                return "";
            }
        }
        return value;
    }

    private boolean openUri(String uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void showMissingDestination() {
        Toast.makeText(
                this,
                "Kerb could not read this trip's destination. Open the trip, then tap Navigate again.",
                Toast.LENGTH_LONG).show();
    }

    private void showAppNotInstalled(String appName) {
        Toast.makeText(this, appName + " could not be opened on this phone.", Toast.LENGTH_LONG).show();
    }
}
