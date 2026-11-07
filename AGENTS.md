# ValutaRate

Android currency tracker and converter (`com.gokcank.valutarate`) built on the Central Bank of Turkey's (TCMB) official
indicative rates, with offline cache, banner ads and TR/EN/DE/FR UI.

## Build & test
- JDK 17; AGP 8.7.3; Gradle wrapper 9.1.0; compile/target SDK 36, minSdk 26.
- Debug APK: `./gradlew assembleDebug`
- Release AAB: `./gradlew bundleRelease` (R8 full mode, resource shrinking, full native debug symbols)
- Unit tests: `./gradlew testDebugUnitTest`
- Lint: `./gradlew lint` (non-blocking: `abortOnError = false`)
- CI: `.github/workflows/android_ci.yml` (testDebugUnitTest + assembleDebug on push/PR to `master`/`main`).
- Default branch is `master`.
- `local.properties` keys read by the build: `ADMOB_APP_ID`, `ADMOB_BANNER_AD_UNIT_ID`, `ADMOB_INTERSTITIAL_AD_UNIT_ID`,
  `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. Keystore path is `app/valutarate.jks` (git-ignored).
  Without them: Google test ad IDs, and the release build is unsigned.

## Layout
- Clean Architecture + MVVM + Hilt under `app/src/main/java/com/gokcank/valutarate/`:
  `data/` (Retrofit/OkHttp, `remote/tcmb/` XML feed + parser, Room cache), `domain/`, `presentation/`
  (`screens/`, `components/` incl. `GlassCard`, `LineChart`, `Sparkline`; `ads/`; `localization/`; `navigation/`), `di/`.
- The live theme is `ui/theme/`; `presentation/theme/` is an empty leftover.
- UI strings live in `presentation/localization/AppStrings.kt` (TR, EN, DE, FR, switchable at runtime), not in
  `res/values*/strings.xml`. Add every new string in all four languages.

## Project rules
- TCMB is the only rate source. Feeds: `https://www.tcmb.gov.tr/kurlar/today.xml` and
  `https://www.tcmb.gov.tr/kurlar/YYYYMM/DDMMYYYY.xml`. HTTPS only.
- TCMB numbers use decimal commas: normalize `,` to `.` and parse with `Locale.US`.
- No XML on weekends/holidays: step back day by day to the last published date; never hard-code a holiday calendar.
- Rates are fixed at 15:30 Turkey time; before that, show the previous business day. Show a static timestamp, no countdown.
- Charts are custom Compose Canvas (`LineChart`, `Sparkline`); do not add a chart library.
- Ads: adaptive bottom banners only, behind the UMP consent flow. No interstitial or rewarded ads.
- R8 full mode is on: every Room entity/DAO, Retrofit model and serialized class needs keep rules in `app/proguard-rules.pro`.
- Bottom sheets open immediately with a shimmer; data loads after.
- Play Console "What's new": max 500 characters per language; write TR, EN, DE, FR, ~150–250 characters each.
- Commit messages: conventional commits (`feat(ui): ...`, `fix(chart): ...`).

## Status (as of 2026-11-07)
- v1.3.0 (`versionCode = 7`), published on Google Play per project notes. Last code change 2026-08-25.
- Tests are minimal (one unit test file, one instrumented test).

## Open items
- UI micro-polish of the Converter and chart bottom sheet.
- Backlog, only on request: crypto rates, home-screen widget, rate alerts, ad-free premium.

## More context
Background, decisions and history are in the second brain: `/home/gokcank/ikinci-beyin/projects/valutarate.md` (read on demand).
