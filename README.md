[![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tweener/kmp-firebase?color=orange)](https://central.sonatype.com/artifact/io.github.tweener/kmp-firebase)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.21-blue.svg?logo=kotlin)](http://kotlinlang.org)
![gradle-version](https://img.shields.io/badge/gradle-9.5.0-blue?logo=gradle)
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](https://opensource.org/licenses/Apache-2.0)

[![Website](https://img.shields.io/badge/Author-vivienmahe.com-purple)](https://vivienmahe.com/)
[![X/Twitter](https://img.shields.io/twitter/follow/VivienMahe)](https://twitter.com/VivienMahe)

---

# KMPFirebase

**KMPFirebase** is a Kotlin Multiplatform library for Android and iOS which wraps the [GitLive Firebase Kotlin SDK](https://github.com/GitLiveApp/firebase-kotlin-sdk) and provides a more straightforward implementation for the Firebase services most apps use: Firestore, Remote Config, Cloud Functions, Analytics and Crashlytics.

<br>

Be sure to show your support by starring ⭐️ this repository!

---

## 🌟 Features

- 🗄️ **Firestore**: Read, listen to, create, add, update and delete documents in a single call, with your own `@Serializable` models.
- 🎛️ **Remote Config**: Fetch and read values with a default value returned when anything goes wrong.
- ☁️ **Cloud Functions**: Call an HTTPS callable function with typed request and response.
- 📊 **Analytics** and 💥 **Crashlytics**: Get the Firebase instances from common code.
- 📅 **Timestamps**: Convert Firestore `Timestamp` to and from `kotlinx.datetime` `LocalDate` and `LocalDateTime`.
- 🪵 **Logging**: Firestore calls, Remote Config values and every failure are logged with [Napier](https://github.com/AAkira/Napier).

---

## 🛠️ Installation

1. In your `settings.gradle.kts` file, add Maven Central to your repositories:
```Groovy
repositories {
  mavenCentral()
}
```

2. Then add KMPFirebase dependency to your `commonMain` source set.

<details>
	<summary>With version catalog:</summary>

Open `libs.versions.toml`:

```Groovy
[versions]
kmpFirebase = "1.6.0" // Check latest version

[libraries]
kmpfirebase = { group = "io.github.tweener", name = "kmp-firebase", version.ref = "kmpFirebase" }
```

Then in your module `build.gradle.kts` add:
```Groovy
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kmpfirebase)
        }
    }
}
```
</details>

<details>
	<summary>Without version catalog:</summary>

In your module `build.gradle.kts` add:
```Groovy
kotlin {
    sourceSets {
        commonMain.dependencies {
            val kmpFirebase_version = "1.6.0" // Check latest version

            implementation("io.github.tweener:kmp-firebase:$kmpFirebase_version")
        }
    }
}
```
</details>

The latest version is: [![Maven Central Version](https://img.shields.io/maven-central/v/io.github.tweener/kmp-firebase?color=orange)](https://central.sonatype.com/artifact/io.github.tweener/kmp-firebase)

### Requirements

- **Targets**: Android, `iosArm64` and `iosSimulatorArm64`.
- **Android**: `minSdk` 24, and your app must compile against Android API 37 or later (`compileSdk = 37`).
- **Kotlin serialization**: your Firestore models must be `@Serializable`, so apply the `org.jetbrains.kotlin.plugin.serialization` Gradle plugin to the module declaring them.

---

## 🔧 Configuration

KMPFirebase exposes the GitLive Firebase SDK modules it uses (`firebase-firestore`, `firebase-config`, `firebase-functions`, `firebase-crashlytics` and `firebase-analytics`) as `api` dependencies, so your app sets Firebase up the way the GitLive SDK requires:

- **Android**: add your `google-services.json` and apply the Google Services Gradle plugin.
- **iOS**: the Firebase iOS SDK is not linked transitively, so add it to your iOS app with Swift Package Manager or CocoaPods, then call `FirebaseApp.configure()` at launch, before using any Firebase API.

See the [GitLive Firebase Kotlin SDK README](https://github.com/GitLiveApp/firebase-kotlin-sdk) for the full setup and the supported Firebase iOS SDK versions.

To see KMPFirebase logs, initialize [Napier](https://github.com/AAkira/Napier) in your app, for instance with `Napier.base(DebugAntilog())`.

---

## 🧑‍💻 Usage

### Firestore

Declare your models by extending `FirestoreModel`, which holds the Firestore document ID:

```kotlin
@Serializable
data class City(
    override var id: String = "",
    val name: String,
    val population: Long,
) : FirestoreModel()
```

Then use `FirebaseFirestoreService` to read and write documents:

```kotlin
val firestoreService = FirebaseFirestoreService()

// Read all documents of a collection, with their document ID set in `id`
val cities: List<City> = firestoreService.getAll<City>(collection = "cities")

// Read a single document
val city: City = firestoreService.get<City>(collection = "cities", id = "LA")

// Listen to a single document
firestoreService.getAsFlow<City>(collection = "cities", id = "LA")
    .collect { city -> println(city.name) }

// Listen to a collection: the first emission holds every document, the next ones only the documents that changed
firestoreService.getAllAsFlow<City>(collection = "cities")
    .collect { changedCities -> println(changedCities.size) }

// Create a document with a given ID, and get it back
val createdCity: City = firestoreService.create<City>(collection = "cities", id = "SF", data = mapOf("name" to "San Francisco", "population" to 808_437L))

// Add a document with an ID generated by Firestore
firestoreService.add(collection = "cities", data = mapOf("name" to "Tokyo", "population" to 14_187_176L))

// Update some fields of a document
firestoreService.update(collection = "cities", id = "SF", data = mapOf("population" to 808_988L))

// Delete a document
firestoreService.delete(collection = "cities", id = "SF")
```

Only `getAll` and `getAllAsFlow` set `id` from the Firestore document ID. `get`, `getAsFlow` and `create` read `id` from the document data, so store it in the document if you need it there.

The `suspend` functions log and rethrow any error, so wrap them in a `try`/`catch` where needed.

#### Timestamps

Convert between Firestore `Timestamp` and `kotlinx.datetime` types:

```kotlin
val timestamp: Timestamp = LocalDateTime(2026, 11, 5, 9, 30).toTimestamp()
val dateTimestamp: Timestamp = LocalDate(2026, 11, 5).toTimestamp()

val localDateTime: LocalDateTime = timestamp.toLocalDateTime()
val localDate: LocalDate = timestamp.toLocalDate()
```

### Remote Config

`FirebaseRemoteConfigService` fetches and activates the Remote Config values on every read. Pass `isDebug = true` to lower the minimum fetch interval to 10 seconds while developing.

`FirebaseRemoteConfigDataSource` reads typed values, and returns `defaultValue` if the value can't be fetched or read:

```kotlin
val remoteConfigService = FirebaseRemoteConfigService(isDebug = BuildConfig.DEBUG)
val remoteConfig = FirebaseRemoteConfigDataSource(firebaseRemoteConfigService = remoteConfigService)

val isFeatureEnabled: Boolean = remoteConfig.getBoolean(key = "feature_enabled", defaultValue = false)
val maxItems: Long = remoteConfig.getLong(key = "max_items", defaultValue = 10L)
val welcomeMessage: String = remoteConfig.getString(key = "welcome_message", defaultValue = "Welcome!")
```

For any other type, call `remoteConfigService.get<T>(key, defaultValue)` directly.

### Cloud Functions

`FirebaseFunctionsService` calls an HTTPS callable function with a 30-second timeout, and returns `null` if the call fails:

```kotlin
@Serializable
data class GreetingRequest(val name: String)

@Serializable
data class GreetingResponse(val message: String)

val functionsService = FirebaseFunctionsService()

val response: GreetingResponse? = functionsService.callFunction<GreetingRequest, GreetingResponse>(
    functionName = "greet",
    data = GreetingRequest(name = "Ada"),
)
```

### Analytics and Crashlytics

Get the GitLive Firebase instances to use their full API:

```kotlin
val analytics: FirebaseAnalytics = FirebaseAnalyticsService().getAnalytics()
analytics.logEvent("city_viewed", mapOf("city" to "LA"))

val crashlytics: FirebaseCrashlytics = FirebaseCrashlyticsService().getCrashlytics()
crashlytics.recordException(exception)
```

---

## 📜 Licence

KMPFirebase is licensed under the [Apache-2.0](https://github.com/Tweener/KMPFirebase/blob/main/LICENSE).
