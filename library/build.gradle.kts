import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.PodspecTask

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinCocoapods)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.gironnetd"
version = "1.0.12"

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

//    val xcframeworkName = "Fibonacci"
//    val xcf = XCFramework(xcframeworkName)

    linuxX64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64(),
//    ).forEach {
//        it.binaries.framework {
//            baseName = xcframeworkName
//
//            // Specify CFBundleIdentifier to uniquely identify the framework
//            binaryOption("bundleId", "com.gironnetd.${xcframeworkName}")
//            xcf.add(this)
//            isStatic = true
//        }
//    }

    cocoapods {
        // Required properties
        // Specify the required Pod version here
        // Otherwise, the Gradle project version is used
        version = "${getVersion()}"
        summary = "Some description for a Kotlin/Native module"
        homepage = "https://github.com/gironnetd/fibonacci"
        authors = "Damien Gironnet"
        license = "{ :type => 'MIT', :text => 'License text'}"

        specRepos {
            url("https://github.com/Kotlin/kotlin-cocoapods-spec.git")
        }

        // Optional properties
        // Configure the Pod name here instead of changing the Gradle project name
        name = "fibonacci"

        ios.deploymentTarget = "13.5"
        osx.deploymentTarget = "10.15"
        tvos.deploymentTarget = "13.4"
        watchos.deploymentTarget = "6.2"

        val podspec = tasks["podspec"] as PodspecTask
        podspec.doLast {
            val newPodspecContent = file("${cocoapods.name}.podspec").readLines().map {
                if (it.contains("spec.source"))
                    "    spec.source                   = { :git => 'git@github.com:gironnetd/fibonacci.git', :tag => '$version' }\n" +
                    "    spec.vendored_frameworks      = 'library/build/cocoapods/framework/#{spec.name}.framework'" else it
            }
            file("${cocoapods.name}.podspec").writeText(newPodspecContent.joinToString(separator = "\n"))
        }

        framework {
            // Required properties
            // Framework name configuration. Use this property instead of deprecated 'frameworkName'
            baseName = "fibonacci"

//            // Specify CFBundleIdentifier to uniquely identify the framework
//            binaryOption("bundleId", "com.gironnetd.${baseName}")
//            xcf.add(this)

            // Optional properties
            // Specify the framework linking type. It's dynamic by default.
            isStatic = true
            // Dependency export
            // Uncomment and specify another project module if you have one:
            // export(project(":<your other KMP module>"))
            @OptIn(ExperimentalKotlinGradlePluginApi::class)
            transitiveExport = false // This is default.
        }

        // Maps custom Xcode configuration to NativeBuildType
        xcodeConfigurationToNativeBuildType["CUSTOM_DEBUG"] = NativeBuildType.DEBUG
        xcodeConfigurationToNativeBuildType["CUSTOM_RELEASE"] = NativeBuildType.RELEASE
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "io.github.gironnetd.fibonacci"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates(group.toString(), "fibonacci", version.toString())

    pom {
        name = "Fibonacci library"
        description = "A mathematics calculation library."
        inceptionYear = "2024"
        url = "https://github.com/gironnetd/fibonacci/"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "gironnetd"
                name = "Damien GIRONNET"
                url = "https://github.com/gironnetd"
            }
        }
        scm {
            url = "https://github.com/gironnetd/fibonacci/"
            connection = "scm:git:git://github.com/gironnetd/fibonacci.git"
            developerConnection = "scm:git:ssh://git@github.com/gironnetd/fibonacci.git"
        }
    }
}
