/*
 * Copyright 2021-2023 Appmattus Limited
 * Copyright 2024 Detomarco
 * Copyright 2024 Michal Browarski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.adarshr.gradle.testlogger.theme.ThemeType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jreleaser.model.Active

plugins {
    kotlin("jvm")
    id("io.gitlab.arturbosch.detekt")
    id("org.jreleaser")
    id("signing")
    id("com.adarshr.test-logger")
    id("org.owasp.dependencycheck")
}

val detektGradlePluginVersion: String by project
val junitVersion: String by project
val mockkVersion: String by project
val kotestVersion: String by project

allprojects {
    group = "com.browarski.kotlinfixture"

    val ghProjectVersion = providers.environmentVariable("GITHUB_REF")
        .map { it.replaceFirst("refs/tags/", "") }
        .map { it.trimStart('v') }
    if (ghProjectVersion.isPresent) {
        version = ghProjectVersion.get()
    }
}

subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("io.gitlab.arturbosch.detekt")
        plugin("com.adarshr.test-logger")
        plugin("org.owasp.dependencycheck")
    }

    repositories {
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
    }

    dependencies {
        implementation(kotlin("stdlib"))

        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektGradlePluginVersion")

        testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
        testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
        testImplementation("io.mockk:mockk:$mockkVersion")
        testImplementation("io.kotest:kotest-assertions-core-jvm:$kotestVersion")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }

    detekt {
        allRules = true
        buildUponDefaultConfig = true
        autoCorrect = System.getProperty("autoCorrect") == "true"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    testlogger {
        theme = ThemeType.MOCHA
        showSimpleNames = true
    }

    dependencyCheck {
        nvd.apiKey = System.getenv("NVD_API_KEY")
        failBuildOnCVSS = 0f
        suppressionFile = "cve-suppressions.xml"
        autoUpdate = true
        // Disable the .NET Assembly Analyzer. Requires an external tool, and this project likely won't ever have .NET DLLs.
        analyzers.assemblyEnabled = false
    }
}

jreleaser {
    project {
        license = "APACHE-2.0"
        authors = listOf("Appmattus Limited", "detomarco", "Michal Browarski")
        copyright = "2019-2023 Appmattus Limited, 2024 detomarco, 2024 Michal Browarski"
        description = "Fixtures for Kotlin providing generated values for unit testing"
    }
    signing {
        active = Active.ALWAYS
        armored = true
    }

    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepositories.add("fixture/build/staging-deploy")
                    stagingRepositories.add("fixture-generex/build/staging-deploy")
                    stagingRepositories.add("fixture-datafaker/build/staging-deploy")
                    stagingRepositories.add("fixture-kotest/build/staging-deploy")
                    retryDelay = 60
                    maxRetries = 100
                }
            }

        }
    }
}

