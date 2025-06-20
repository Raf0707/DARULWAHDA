import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val composeV = "1.6.10"

        /* -------- commonMain ▸ уже есть ---------- */
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.compose.runtime:runtime:$composeV")
                implementation("org.jetbrains.compose.foundation:foundation:$composeV")
                implementation("org.jetbrains.compose.material3:material3:$composeV")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("androidx.activity:activity-compose:1.9.0")
                implementation("androidx.webkit:webkit:1.10.0")
            }
        }

        /* -------- iOS  -------- */
        val iosX64Main            by getting
        val iosArm64Main          by getting
        val iosSimulatorArm64Main by getting

        val iosMain by creating {
            dependsOn(commonMain)

            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)

            dependencies {
                /** главное: UI-UIKit! */
                implementation("org.jetbrains.compose.ui:ui-uikit:$composeV")

                /** Coroutines для Darwin-бинарей */
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0-nativeDarwin")
            }
        }

        /* -------- tests -------- */
        val commonTest by getting {
            dependencies { implementation(libs.kotlin.test) }
        }
    }

}

android {
    namespace = "ru.darulwahda.app"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
