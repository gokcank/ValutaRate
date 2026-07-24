# ValutaRate — Custom ProGuard / R8 Rules
# ============================================================
# These rules prevent R8 from stripping classes that are
# accessed reflectively at runtime (Hilt DI, Room ORM,
# Retrofit/Gson network layer).
# ============================================================

# --- Hilt Dependency Injection ---
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

# --- Room Database ---
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public static ** Companion;
}

# --- Retrofit & OkHttp ---
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# --- Gson (JSON serialization) ---
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- App domain models & DTOs ---
-keep class com.gokcank.valutarate.domain.model.** { *; }
-keep class com.gokcank.valutarate.data.remote.tcmb.dto.** { *; }
-keep class com.gokcank.valutarate.data.local.entity.** { *; }

# --- Kotlin coroutines ---
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# --- AdMob / Google Play Services ---
-keep public class com.google.android.gms.ads.** { *; }
-keep public class com.google.ads.** { *; }
-dontwarn com.google.android.gms.**
