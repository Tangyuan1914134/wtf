# ProGuard rules for TimetableApp
# Keep Hilt generated code
-keep class dagger.hilt.internal.** { *; }
-keep class * extends dagger.hilt.android.internal.lifecycle.HiltViewModelFactory { *; }
-dontwarn dagger.hilt.internal.**
