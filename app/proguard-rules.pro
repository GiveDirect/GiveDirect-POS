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

-dontobfuscate
-dontwarn com.google.**
-dontwarn com.moandjiezana.**
-dontwarn com.sun.**
-dontwarn javassist.**
-dontwarn javax.**
-dontwarn jersey.repackaged.**
-dontwarn net.bytebuddy.**
-dontwarn net.i2p.crypto.eddsa.**
-dontwarn org.aopalliance.**
-dontwarn org.apache.**
-dontwarn org.glassfish.**
-dontwarn org.jvnet.**
-dontwarn org.mockito.**
-dontwarn org.objenesis.**
-dontwarn org.stellar.sdk.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *
