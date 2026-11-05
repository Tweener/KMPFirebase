import org.gradle.api.JavaVersion

/**
 * @author Vivien Mahe
 * @since 24/02/2025
 */

object ProjectConfiguration {

    object KMPFirebase {
        const val packageName = "com.tweener.firebase"
        const val versionName = "1.5.0"
        const val namespace = "$packageName.android"
        const val compileSDK = 37
        const val minSDK = 24

        object Maven {
            const val name = "KMPFirebase"
            const val description = "A Kotlin Multiplatform library which wraps GitLiveApp Firebase SDK and provides a more straightforward implementation."
            const val group = "io.github.tweener"
            const val artifactId = "kmp-firebase"
            const val packageUrl = "https://github.com/Tweener/KMPFirebase"
            const val gitUrl = "github.com:Tweener/KMPFirebase.git"

            object Developer {
                const val id = "Tweener"
                const val name = "Vivien Mahé"
                const val email = "vivien@tweener-labs.com"
            }
        }
    }

    object Compiler {
        val javaCompatibility = JavaVersion.VERSION_21
        val jvmTarget = javaCompatibility.toString()
    }
}
