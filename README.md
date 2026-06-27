# RateFlow 💱

RateFlow is a modern, premium, and highly dynamic currency converter application built natively for Android. Designed with a stunning **Glassmorphism** UI and fluid animations, RateFlow offers a seamless experience for tracking and converting currencies using the official indicative exchange rates provided by the **TCMB (Central Bank of the Republic of Turkey)**.

## ✨ Features

- **TCMB Integration:** Fetches daily official cross rates directly from the Central Bank.
- **Glassmorphism UI:** A premium, translucent, and deeply aesthetic user interface that feels alive.
- **Dynamic Theming:** Built-in dynamic mesh backgrounds with custom palettes (including *Sunset* and *Vice City* neon vibes).
- **Localization (i18n):** Full support for 4 languages out of the box: English, Turkish, German, and French.
- **Responsive Converter:** Instantly convert between different currencies with an intuitive switch mechanic.
- **Offline Support:** Caches the latest rates locally so you can check conversions even without an internet connection.

## 🛠 Tech Stack & Architecture

RateFlow is built using modern Android development practices and cutting-edge technologies:

- **100% Kotlin**
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
- **Dependency Injection:** Dagger Hilt
- **Networking:** Retrofit + OkHttp
- **Database / Caching:** Room Database
- **Preferences:** Preferences DataStore (for Themes and Languages)
- **Concurrency:** Kotlin Coroutines & Flows
- **Monetization:** AdMob integration (Safely configured via `local.properties`)

## 🚀 Getting Started

To run this project locally, clone the repository and open it in Android Studio.

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17
- Android SDK 35+

### Security Configuration
For security reasons, AdMob IDs and Keystore configurations are not pushed to this repository. To compile the project successfully, create a `local.properties` file in the root directory and add the following dummy keys (or your real AdMob keys):

```properties
ADMOB_APP_ID=ca-app-pub-3940256099942544~3347511713
ADMOB_BANNER_AD_UNIT_ID=ca-app-pub-3940256099942544/6300978111
ADMOB_INTERSTITIAL_AD_UNIT_ID=ca-app-pub-3940256099942544/1033173712
```

## 🤝 Contribution
Feedback, bug reports, and pull requests are always welcome! Feel free to open an issue if you encounter any problems or have feature requests.

## 👨‍💻 Developer
Developed with ❤️ by **gokcank**.
