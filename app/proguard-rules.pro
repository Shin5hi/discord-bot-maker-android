# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep all data classes used for serialization
-keepclassmembers class com.discordbotmaker.android.** {
    <fields>;
}

# Keep Compose runtime classes
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# Keep Kotlin coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Keep Ktor client classes
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }
