import UIKit
import Capacitor

/**
 * Kerb iOS bridge.
 *
 * The WebView loads the live, unmodified https://www.kerbgo.com site (configured
 * in capacitor.config.ts). This class adds the one piece of UI that has to be
 * native: the driver-only navigation chooser.
 *
 * Drivers get a "Navigate" control on the driver dashboard. Tapping it offers
 * Kerb's own in-app Mapbox map (the default, unchanged), Apple Maps, Google Maps
 * or Waze. Riders never see it: the control only appears when the WebView is on
 * /driver, the dashboard that shows the active trip. Sub-pages such as
 * /driver/earnings and /driver/documents have no trip, so the control stays
 * hidden there.
 *
 * Destination hand-off: the dropoff address is read from one documented hook —
 * an element carrying `data-kerb-destination`. If the trip page does not expose
 * it, the app says so rather than opening a navigation app pointed at a guessed
 * address.
 */
class KerbBridgeViewController: CAPBridgeViewController {

    private let navigateButton = UIButton(type: .system)
    private var routeTimer: Timer?
    private var isShowingChooser = false
    private var riderPatchSource: String?
    private var lastRiderPatchAt = Date.distantPast

    /**
     * Runs inside the site's page when a driver taps Navigate.
     *
     * Contract (the only one):
     *   <... data-kerb-destination="12 Main St, Springfield">
     *
     * The attribute value wins; the element's text is accepted as a convenience
     * for markup that renders the address as text. Nothing else on the page is
     * inspected, so a wrong address cannot be picked up from an unrelated link.
     */
    private static let destinationScript = """
    (function () {
      var el = document.querySelector('[data-kerb-destination]');
      if (!el) { return ''; }
      var value = el.getAttribute('data-kerb-destination') || el.textContent || '';
      return value.replace(/\\s+/g, ' ').trim();
    })();
    """

    private enum NavigationTarget {
        case appleMaps
        case googleMaps
        case waze

        var label: String {
            switch self {
            case .appleMaps: return "Apple Maps"
            case .googleMaps: return "Google Maps"
            case .waze: return "Waze"
            }
        }
    }

    override func viewDidLoad() {
        super.viewDidLoad()
        configureNavigateButton()
    }

    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        startWatchingRoute()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        routeTimer?.invalidate()
        routeTimer = nil
    }

    // MARK: - UI

    private func configureNavigateButton() {
        var configuration = UIButton.Configuration.filled()
        configuration.title = "Navigate"
        configuration.image = UIImage(systemName: "arrow.triangle.turn.up.right.diamond.fill")
        configuration.imagePadding = 6
        configuration.cornerStyle = .capsule
        // Kerb brand orange (#FD5204).
        configuration.baseBackgroundColor = UIColor(red: 0.992, green: 0.322, blue: 0.016, alpha: 1.0)
        configuration.baseForegroundColor = .white
        configuration.contentInsets = NSDirectionalEdgeInsets(top: 10, leading: 16, bottom: 10, trailing: 16)

        navigateButton.configuration = configuration
        navigateButton.translatesAutoresizingMaskIntoConstraints = false
        navigateButton.isHidden = true
        navigateButton.addTarget(self, action: #selector(presentNavigationChooser), for: .touchUpInside)
        view.addSubview(navigateButton)

        NSLayoutConstraint.activate([
            navigateButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16),
            // The shell patch pins a tab bar along the bottom of the page, so the
            // pill has to float above it instead of sitting on the Account tab.
            navigateButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -72)
        ])
    }

    private func startWatchingRoute() {
        routeTimer?.invalidate()
        // The site is a normal web app, so the only reliable signal for "the
        // driver route is open" is the URL the WebView is showing.
        routeTimer = Timer.scheduledTimer(withTimeInterval: 0.8, repeats: true) { [weak self] _ in
            self?.refreshNavigateButton()
        }
        refreshNavigateButton()
    }

    private func refreshNavigateButton() {
        guard let path = webView?.url?.path else {
            navigateButton.isHidden = true
            return
        }
        let isDriverRoute = path == "/driver" || path == "/driver/"
        navigateButton.isHidden = !isDriverRoute
        if isDriverRoute {
            view.bringSubviewToFront(navigateButton)
        }
        applyShellPatchIfNeeded(path: path)
    }

    /**
     * Runs the bundled presentation patch (www/shell-tweak.js) while the WebView is
     * on the rider home or the driver home. The patch is idempotent and keeps
     * itself applied across in-app route changes, so re-running it on a slow
     * cadence is cheap and covers full page loads.
     */
    private func applyShellPatchIfNeeded(path: String) {
        guard path == "/app" || path == "/app/" || path == "/driver" || path == "/driver/" else { return }
        guard Date().timeIntervalSince(lastRiderPatchAt) > 5 else { return }
        lastRiderPatchAt = Date()

        if riderPatchSource == nil {
            riderPatchSource = Bundle.main
                .url(forResource: "shell-tweak", withExtension: "js", subdirectory: "public")
                .flatMap { try? String(contentsOf: $0, encoding: .utf8) }
        }
        guard let source = riderPatchSource else { return }
        webView?.evaluateJavaScript(source)
    }

    // MARK: - Navigation chooser

    @objc private func presentNavigationChooser() {
        guard !isShowingChooser else { return }
        isShowingChooser = true

        let sheet = UIAlertController(title: "Navigate with", message: nil, preferredStyle: .actionSheet)

        sheet.addAction(UIAlertAction(title: "Kerb map (in app)", style: .default) { [weak self] _ in
            self?.isShowingChooser = false
        })
        sheet.addAction(UIAlertAction(title: NavigationTarget.appleMaps.label, style: .default) { [weak self] _ in
            self?.isShowingChooser = false
            self?.launch(.appleMaps)
        })
        // Offered even when the app is not installed: the https fallback opens
        // the same directions in the browser, so a driver is never stuck.
        sheet.addAction(UIAlertAction(title: NavigationTarget.googleMaps.label, style: .default) { [weak self] _ in
            self?.isShowingChooser = false
            self?.launch(.googleMaps)
        })
        sheet.addAction(UIAlertAction(title: NavigationTarget.waze.label, style: .default) { [weak self] _ in
            self?.isShowingChooser = false
            self?.launch(.waze)
        })
        sheet.addAction(UIAlertAction(title: "Cancel", style: .cancel) { [weak self] _ in
            self?.isShowingChooser = false
        })

        if let popover = sheet.popoverPresentationController {
            popover.sourceView = navigateButton
            popover.sourceRect = navigateButton.bounds
        }
        present(sheet, animated: true)
    }

    private func launch(_ target: NavigationTarget) {
        resolveDestination { [weak self] destination in
            guard let self = self else { return }
            guard let destination = destination else {
                self.showAlert(
                    title: "Destination not available",
                    message: "Kerb could not read this trip's destination. Open the trip in Kerb, then tap Navigate again."
                )
                return
            }

            let encoded = destination.addingPercentEncoding(
                withAllowedCharacters: CharacterSet.urlQueryAllowed.subtracting(CharacterSet(charactersIn: "&?=+#"))
            ) ?? destination

            let appLink: String
            let webLink: String
            switch target {
            case .appleMaps:
                appLink = "https://maps.apple.com/?daddr=\(encoded)&dirflg=d"
                webLink = appLink
            case .googleMaps:
                appLink = "comgooglemaps://?daddr=\(encoded)&directionsmode=driving"
                webLink = "https://www.google.com/maps/dir/?api=1&destination=\(encoded)"
            case .waze:
                appLink = "waze://?q=\(encoded)&navigate=yes"
                webLink = "https://waze.com/ul?q=\(encoded)&navigate=yes"
            }

            guard let primaryURL = URL(string: appLink) else { return }
            DispatchQueue.main.async {
                UIApplication.shared.open(primaryURL, options: [:]) { opened in
                    guard !opened else { return }
                    guard let fallbackURL = URL(string: webLink), webLink != appLink else {
                        self.showAppNotInstalled(target)
                        return
                    }
                    UIApplication.shared.open(fallbackURL, options: [:]) { fallbackOpened in
                        if !fallbackOpened { self.showAppNotInstalled(target) }
                    }
                }
            }
        }
    }

    private func resolveDestination(_ completion: @escaping (String?) -> Void) {
        guard let webView = webView else {
            completion(nil)
            return
        }
        webView.evaluateJavaScript(KerbBridgeViewController.destinationScript) { result, _ in
            let raw = (result as? String)?.trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
            completion(raw.isEmpty ? nil : raw)
        }
    }

    // MARK: - Alerts

    private func showAppNotInstalled(_ target: NavigationTarget) {
        showAlert(
            title: "\(target.label) not available",
            message: "\(target.label) could not be opened on this iPhone. Install it from the App Store, or use the Kerb map."
        )
    }

    private func showAlert(title: String, message: String) {
        // The action sheet is usually still dismissing when this runs, so wait
        // for it rather than dropping the alert.
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.35) { [weak self] in
            guard let self = self else { return }
            var presenter: UIViewController = self
            while let presented = presenter.presentedViewController {
                presenter = presented
            }
            if presenter is UIAlertController { return }
            let alert = UIAlertController(title: title, message: message, preferredStyle: .alert)
            alert.addAction(UIAlertAction(title: "OK", style: .default))
            presenter.present(alert, animated: true)
        }
    }
}

class SceneDelegate: UIResponder, UIWindowSceneDelegate {
    var window: UIWindow?

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        guard let windowScene = scene as? UIWindowScene else { return }

        window = UIWindow(windowScene: windowScene)
        window?.rootViewController = KerbBridgeViewController()
        window?.makeKeyAndVisible()

        SceneDelegateProxy.shared.scene(scene, willConnectTo: session, options: connectionOptions)
    }

    func scene(_ scene: UIScene, openURLContexts URLContexts: Set<UIOpenURLContext>) {
        SceneDelegateProxy.shared.scene(scene, openURLContexts: URLContexts)
    }

    func scene(_ scene: UIScene, continue userActivity: NSUserActivity) {
        SceneDelegateProxy.shared.scene(scene, continue: userActivity)
    }
}
