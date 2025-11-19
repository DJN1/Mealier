# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationException

# Keep generated serializers
-keep class com.davidniederweis.mealier.data.model.**$$serializer { *; }

# Keep companion objects and serializer methods on serializable classes
-keepclassmembers class com.davidniederweis.mealier.data.model.** {
    *** Companion;
}
-keepclasseswithmembers class com.davidniederweis.mealier.data.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}