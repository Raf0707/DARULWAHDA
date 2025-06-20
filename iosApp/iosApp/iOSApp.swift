import shared          // ← автогенерируемый KMP-фреймворк
import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup { ComposeRoot() }
    }
}

struct ComposeRoot: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        /* Функция создаётся Kotlin-Gradle-плагином; авто-дополнение Xcode
           подскажет точное имя. Обычно это:
           Ru_darulwahda_appAppKt.AppViewController()             */
        return Ru_darulwahda_appAppKt.AppViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
