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

plugins {
    kotlin("jvm")
    id("maven-publish")
}

val dataFakerVersion: String by project

dependencies {
    implementation(project(":fixture"))
    api("net.datafaker:datafaker:$dataFakerVersion")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            from(components["java"])
            artifactId = "fixture-datafaker"
            description = "kotlinfixture module to generate values with a closer match to real data using"
        }
        withType<MavenPublication> {
            pom {
                packaging = "jar"
                name = "kotlinfixture-datafaker"
                description = "Kotlin Fixture - DataFaker"
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
                        id = "morbic"
                        name = "Michal Browarski"
                    }
                    developer {
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
