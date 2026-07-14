# ValutaRate Projesi - Özet ve Devir Teslim Dosyası
*(Yeni bir sohbete (session) başlarken yapay zekaya bağlamı (context) vermek için bu metni kopyalayıp yapıştırabilirsiniz.)*

> [!CAUTION]
> **CRITICAL META-RULE:** Bu projede çalışırken sisteminize tanımlanmış olan **Global Kurallar (GEMINI.md / User Rules)** mutlak surette geçerlidir. Hiçbir "ufak/basit/tek seferlik" iş için inisiyatif alamazsınız. Kullanıcı açıkça komut ("uygula", "başla", "do it" vb.) vermeden kesinlikle hiçbir kod, dosya veya terminal aracı çalıştıramazsınız. Bu kuralları atlamak KESİNLİKLE YASAKTIR.

## 👥 Biz Kimiz ve Ne Yapıyoruz?
- **Gökcan (`gokcank`)**: Projenin mimarı ve "Vibecoder"ı. Yapay zekayı bir şef gibi yöneterek harika mobil tasarımlar ve uygulamalar ortaya çıkaran vizyoner. Her zaman en iyi kullanıcı deneyimini (UI/UX) ve estetiği hedefler.
- **Yapay Zeka (AI)**: Gökcan'ın talimatları doğrultusunda kod yazan, mimariyi inşa eden, UI/UX kararlarında "Vibecoder" ruhuna ayak uyduran ve Google Play yayınlama sürecine destek olan asistan.
- **Proje - ValutaRate (Eski adıyla RateFlow)**: Kotlin ve Jetpack Compose ile yazılmış, premium hissettiren, modern bir döviz kuru (exchange rate) ve çevirici (converter) Android uygulaması.
- **Teknoloji Yığını (Tech Stack)**: Kotlin, Jetpack Compose (MVVM, Navigation), Hilt (Dependency Injection), Retrofit & OkHttp (Network API), Room (Yerel Veritabanı), DataStore (Tercihler/Tema ayarları), Vico Compose (Tarihsel grafikler için), Coil (Görseller), AdMob (Monetizasyon). Kütüphane yönetimi Version Catalog (`libs.versions.toml`) ile yapılmaktadır.

## ✅ Bugüne Kadar Neler Yaptık?
1. **Mimari ve Veri Akışı**: API üzerinden canlı kurların çekilmesi, Room veritabanı ile (Official Rates ve Historical Rates olarak) yerel önbelleğe (cache) alınması işlemi tamamlandı. Uygulama çevrimdışıyken de son verilerle çalışabiliyor.
2. **Premium Arayüz (UI/UX)**: Glassmorphism (GlassCard) ağırlıklı, renk geçişli (gradient), çok şık bir tasarım dili oturtuldu. "Minimal, Dark, Cyberpunk" gibi özel renk paletlerinin yanı sıra kullanıcının Sistem/Açık/Koyu tema arasında geçiş yapabilmesi sağlandı.
3. **Grafikler ve Çevirici**: Geçmişe dönük (historical) kurların veritabanında saklanması ve Vico kütüphanesi kullanılarak bir ModalBottomSheet içerisinde "LineChart" olarak kullanıcıya sunulması başarıyla eklendi. "Converter" (Çevirici) ekranı yapılandırıldı ve hizalamaları kusursuzlaştırıldı.
4. **Çeviri ve Yerelleştirme**: Türkçe, İngilizce, Almanca ve Fransızca dilleri için tam destek sağlandı. Ekran hizalamaları tüm dillere uyumlu hale getirildi.
5. **Monetizasyon**: AdMob entegrasyonu (Banner ve Interstitial reklamlar) başarıyla yapıldı.
6. **Sürüm ve Dağıtım**: Uygulamanın v1.1.0 (versionCode: 2) sürümü için `assembleRelease` ve `bundleRelease` derlemeleri hatasız tamamlandı. Keystore ve signing configs ayarlandı.

## 🔒 Güvenlik ve Gizlilik Politikamız
Bu projede güvenlik ve açık kaynak (Open Source) standartları **en yüksek önceliğe** sahiptir. Yeni kod yazarken veya mimari önerisinde bulunurken şu kurallar KESİNLİKLE ihlal edilemez:
1. **GitHub'a Asla Gitmemesi Gerekenler**: Hiçbir API Anahtarı (Döviz API'si, vb.), AdMob ID'si, Şifre veya Keystore dosyası (*.jks*, *.keystore*) kaynak kodlarına (örn: `MainActivity.kt`, `Constants.kt`) doğrudan yazılamaz (hardcoded).
2. **local.properties Mimarisi**: Sistemin çalışmasını sağlayan tüm gizli kilitler **sadece** geliştiricinin bilgisayarındaki `local.properties` dosyasından okunur. Bu dosya `.gitignore` listesindedir.
3. **BuildConfig Entegrasyonu**: Kod içindeki değişkenler, `build.gradle.kts` üzerinden `local.properties` okunarak `BuildConfig` ve `manifestPlaceholders` (AdMob App ID için) değişkenlerine dönüştürülür.
4. **Kişisel Kasa (Vault) Yedeklemesi**: GitHub'a gönderilmeyen tüm kritik gizli dosyalar (`local.properties`, `valutarate.jks`), doğrudan geliştirici tarafından güvenli bir alanda (Personal Vault) yedeklenmelidir.

## 📝 Çalışma ve Görev Prensibimiz
Bu projede katı bir onay mekanizması uygulanır:
- AI, kodu doğrudan ve izinsiz bir şekilde **DEĞİŞTİREMEZ**. 
- Karmaşık işler (yeni mimari, veritabanı değişikliği, büyük UI eklentileri) `implementation_plan.md` üzerinden planlanıp Gökcan'ın onayına sunulur.
- Onay alındıktan sonra mutlaka **`task.md`** dosyası oluşturulup/güncellenip `[ ]`, `[/]`, `[x]` şeklinde adım adım ilerlenir.
- Geliştirici açıkça "Uygula", "Başla" veya "Proceed" demeden terminalde çalışan kod değiştirme işlemleri yapılamaz.
