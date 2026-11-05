# Changelog

## [1.6.0] - November 5, 2026
- Update Kotlin to 2.4.21.
- Update Gradle to 9.8.2.
- Update Android Gradle Plugin to 9.5.0. The Android target now builds with the `com.android.kotlin.multiplatform.library` plugin.
- Update GitLive to 2.7.0. It brings the Firebase Android BoM 34.18.0, so apps still depending on Firebase `-ktx` artifacts must migrate, as they are no longer part of the BoM.
- Update compileSdk to 37, required by AndroidX Core 1.19.1. Apps using KMPFirebase must compile against Android API 37 or later.
- Update Coroutines to 1.11.0.
- Update KotlinX Serialization to 1.12.0.
- Update KMPKit to 1.0.14.
- Publish sources jars for every target.

## [1.5.0] - September 11, 2025
- Update Kotlin to 2.2.10.
- Update Gradle to 8.13.0.
- Update GitLiveApp to 2.3.0.

## [1.4.1] - July 15, 2025
- Update Kotlin to 2.2.0.
- Update Compose Multiplatform to 1.8.2.
- Update Gradle to 8.11.1.
- Update Coroutines to 1.10.2.
- Update KMPKit to 1.0.10.

## [1.4.0] - February 24, 2025
- Initial commit for KMPFirebase. This code used to be in [kmp-bom](https://github.com/Tweener/kmp-bom/tree/main/kmp-firebase) repository, which will be archived soon.
- Maven dependency and package name remain the same. 
