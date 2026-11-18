package com.kerbgo.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;

import com.getcapacitor.BridgeActivity;

import org.json.JSONArray;

/**
 * Kerb Android bridge.
 *
 * The WebView loads the live, unmodified https://www.kerbgo.com site (configured
 * in capacitor.config.ts). This activity adds the one piece of UI that has to be
 * native: the driver-only navigation chooser.
 *
 * Drivers get a "Navigate" control while they are on a Kerb driver route. Tapping
 * it offers Kerb's own in-app Mapbox map (the default, unchanged), Google Maps or
 * Waze (Apple Maps does not exist on Android). Riders never see it.
 *
 * Destination hand-off: the dropoff address is read from one documented hook —
 * an element carrying `data-kerb-destination`. If the trip page does not expose
 * it, the app says so rather than opening a navigation app pointed at a guessed
 * address.
 */
public class MainActivity extends BridgeActivity {

    private static final String DRIVER_ROUTE_PREFIX = "/app/driver";

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
        addNavigateButton();
        handleBackNavigation();
    }

    @Override
    public void onResume() {
        super.onResume();
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

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.END | Gravity.BOTTOM);
        params.setMargins(0, 0, margin, margin);

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
        boolean isDriverRoute = path != null && path.startsWith(DRIVER_ROUTE_PREFIX);
        navigateButton.setVisibility(isDriverRoute ? View.VISIBLE : View.GONE);
        if (isDriverRoute) {
            navigateButton.bringToFront();
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
