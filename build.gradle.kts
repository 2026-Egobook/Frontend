// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.dagger.hilt.android") version "2.54" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.8.5" apply false
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1" apply false
    id("com.google.devtools.ksp") version "2.1.0-1.0.29" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}