# Project-specific ProGuard rules.

# Keep application entry points and Room models stable when R8 shrinks the debug APK.
-keep class com.albasroh.absensi.** { *; }
-keepattributes *Annotation*
