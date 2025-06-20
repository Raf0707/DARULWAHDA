import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.compose") version "1.6.10"
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
        commonMain.dependencies {
            kotlin {
                sourceSets {
                    val commonMain by getting {
                        dependencies {
                            implementation(compose.runtime)
                            implementation(compose.foundation)
                            implementation(compose.material3)
                        }
                    }
                    val androidMain by getting {
                        dependencies {
                            implementation("androidx.activity:activity-compose:1.9.0")
                            implementation("androidx.webkit:webkit:1.10.0")
                        }
                    }
                    // iosMain — без особых зависимостей
                }
            }

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
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
