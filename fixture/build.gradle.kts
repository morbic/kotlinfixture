/*
 * Copyright 2021-2023 Appmattus Limited
 *           2024 Detomarco
 *           2024 Michal Browarski
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

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("maven-publish")
}

val classgraphVersion: String by project
val jodaTimeVersion: String by project
val mockitoKotlinVersion: String by project
val serializationVersion: String by project
val marcellogalhardoVersion: String by project
val flextradeVersion: String by project
val easyrandomVersion: String by project
val kotlinxSerializatioVersion: String by project

dependencies {

    implementation("io.github.classgraph:classgraph:${classgraphVersion}")
    implementation(kotlin("reflect"))

    compileOnly("joda-time:joda-time:${jodaTimeVersion}")
    testImplementation("joda-time:joda-time:${jodaTimeVersion}")

    testImplementation("org.mockito.kotlin:mockito-kotlin:${mockitoKotlinVersion}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxSerializatioVersion}")
    // Used for ComparisonTest
    testImplementation("com.github.marcellogalhardo:kotlin-fixture:${marcellogalhardoVersion}")
    testImplementation("com.flextrade.jfixture:kfixture:${flextradeVersion}")
    testImplementation("org.jeasy:easy-random-core:${easyrandomVersion}")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            from(components["java"])
            artifactId = "fixture"
            description = "Fixtures for Kotlin providing generated values for unit testing"
        }
        withType<MavenPublication> {
            pom {
                packaging = "jar"
                name = "kotlinfixture"
                description = "Kotlin Fixture"
                url = "https://github.com/morbic/kotlinfixture"
                inceptionYear = "2024"
                licenses {
                    license {
                        name = "Apache-2.0"
                        url = "https://spdx.org/licenses/Apache-2.0.html"
                    }
                }
                developers {
                    developer {
                        developer {
                            id = "morbic"
                            name = "Michal Browarski"
                        }
                        id = "detomarco"
                        name = "Marco De Toma"
                    }
                    developer {
                        id = "Appmattus Limited"
                        name = "Matthew Dolan"
                    }
                }
                scm {
                    connection = "scm:git:https://github.com/morbic/kotlinfixture.git"
                    developerConnection = "scm:git:ssh://github.com/morbic/kotlinfixture.git"
                    url = "https://github.com/morbic/kotlinfixture"
                }
            }
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}
