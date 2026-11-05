import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SourcesJar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish)
}

kotlin {
    applyDefaultHierarchyTemplate()

    android {
        namespace = ProjectConfiguration.KMPFirebase.namespace
        compileSdk = ProjectConfiguration.KMPFirebase.compileSDK
        minSdk = ProjectConfiguration.KMPFirebase.minSDK

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(ProjectConfiguration.Compiler.jvmTarget))
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "kmpfirebase"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.napier)
            implementation(libs.android.annotations)

            // Tweener
            implementation(libs.kmpkit)

            // Coroutines
            implementation(libs.kotlin.coroutines.core)

            // Firebase GitLive
            api(libs.bundles.firebase)

            implementation(libs.kotlin.serialization.json)
        }

        androidMain.dependencies {
            // Coroutines
            implementation(libs.kotlin.coroutines.android)

            // Android
            implementation(libs.android.core)
        }

        iosMain.dependencies {

        }
    }
}

// region Publishing

group = ProjectConfiguration.KMPFirebase.Maven.group
version = ProjectConfiguration.KMPFirebase.versionName

mavenPublishing {
    publishToMavenCentral(automaticRelease = true)

    // Only disable signing if the flag is explicitly set to false
    val signAllPublicationsProperty = findProperty("mavenPublishing.signAllPublications")
    if (signAllPublicationsProperty == null || signAllPublicationsProperty.toString().toBoolean()) {
        signAllPublications()
    }

    coordinates(groupId = group.toString(), artifactId = ProjectConfiguration.KMPFirebase.Maven.artifactId, version = version.toString())
    configure(
        platform = KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = SourcesJar.Sources(),
        )
    )

    pom {
        name.set(ProjectConfiguration.KMPFirebase.Maven.name)
        description.set(ProjectConfiguration.KMPFirebase.Maven.description)
        url.set(ProjectConfiguration.KMPFirebase.Maven.packageUrl)

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("${ProjectConfiguration.KMPFirebase.Maven.packageUrl}/issues")
        }

        developers {
            developer {
                id.set(ProjectConfiguration.KMPFirebase.Maven.Developer.id)
                name.set(ProjectConfiguration.KMPFirebase.Maven.Developer.name)
                email.set(ProjectConfiguration.KMPFirebase.Maven.Developer.email)
            }
        }

        scm {
            connection.set("scm:git:git://${ProjectConfiguration.KMPFirebase.Maven.gitUrl}")
            developerConnection.set("scm:git:ssh://${ProjectConfiguration.KMPFirebase.Maven.gitUrl}")
            url.set(ProjectConfiguration.KMPFirebase.Maven.packageUrl)
        }
    }
}


// endregion Publishing
