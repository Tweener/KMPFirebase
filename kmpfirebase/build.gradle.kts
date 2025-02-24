import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.dokka)
    id("maven-publish")
    id("signing")
}

android {
    namespace = ProjectConfiguration.KMPFirebase.namespace
    compileSdk = ProjectConfiguration.KMPFirebase.compileSDK

    defaultConfig {
        minSdk = ProjectConfiguration.KMPFirebase.minSDK

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        getByName("debug") {
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = ProjectConfiguration.Compiler.javaCompatibility
        targetCompatibility = ProjectConfiguration.Compiler.javaCompatibility
    }
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")

        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(ProjectConfiguration.Compiler.jvmTarget))
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "kmpfirebase"
            isStatic = true
        }
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "kmpfirebase.js"
            }
        }
        binaries.executable()
    }

    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "kmpfirebase.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.napier)
            implementation(libs.android.annotations)

            // Compose
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.materialIconsExtended)
            implementation(compose.runtime)
            implementation(compose.components.resources)
            implementation(libs.compose.multiplatform.material3)

            // Tweener
            implementation(libs.kmpkit)

            // Coroutines
            implementation(libs.kotlin.coroutines.core)
        }

        androidMain.dependencies {
            // Compose
            api(compose.preview)
            api(compose.uiTooling)
            implementation(libs.android.activity.compose)

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

// Dokka configuration
tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        jdkVersion.set(ProjectConfiguration.Compiler.jvmTarget.toInt())
        languageVersion.set(libs.versions.kotlin)

        sourceLink {
            localDirectory.set(rootProject.projectDir)
            remoteUrl.set(URI(ProjectConfiguration.KMPFirebase.Maven.packageUrl + "/tree/main").toURL())
            remoteLineSuffix.set("#L")
        }
    }
}

publishing {
    publications {
        publications.withType<MavenPublication> {
            artifact(tasks["dokkaJavadocJar"])

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
    }
}

signing {
    if (project.hasProperty("signing.gnupg.keyName")) {
        println("Signing lib...")
        useGpgCmd()
        sign(publishing.publications)
    }
}

// endregion Publishing
