buildscript {
    val agp_version by extra("8.9.1")
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48")
    }
}
    plugins {
        id("com.android.application") version "8.9.1" apply false
        id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    }
