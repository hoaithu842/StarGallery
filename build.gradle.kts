// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
}

//allprojects {
//    repositories {
//        google()
//        jcenter()
//        flatDir {
//            dirs("libs")
//        }
//    }
//    dependencies {
//        classpath("com.github.dcendents:android-maven-gradle-plugin:2.0")
//        classpath("com.google.gms:google-services:4.3.10")
//    }
//}
buildscript {
    repositories {
        mavenCentral()
        google()
        flatDir {
            dirs("libs")
        }
        jcenter()
        // Add any required repositories here
    }
    dependencies {
        // Add the classpath dependency
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.0")
        classpath("com.google.gms:google-services:4.3.10")
    }
}
allprojects {
    repositories {

        // Add any required repositories here
    }

}