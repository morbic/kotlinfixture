/*
 * Copyright 2020 Appmattus Limited
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

package com.browarski.kotlinfixture.resolver

import com.browarski.kotlinfixture.TestContext
import com.browarski.kotlinfixture.Unresolved
import com.browarski.kotlinfixture.assertIsRandom
import com.browarski.kotlinfixture.config.Configuration
import com.browarski.kotlinfixture.config.ConfigurationBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FactoryMethodResolverTest {
    private val context = TestContext(
        Configuration(),
        CompositeResolver(
            PrimitiveResolver(),
            StringResolver(),
            KTypeResolver(),
            KNamedPropertyResolver(),
            FactoryMethodResolver(),
            KFunctionResolver()
        )
    )

    @Test
    fun `Unknown class returns Unresolved`() {
        val result = context.resolve(Number::class)

        assertTrue(result is Unresolved)
    }

    @Test
    fun `Class returns Unresolved when not able to resolve factory method`() {
        val context = TestContext(
            Configuration(),
            FactoryMethodResolver()
        )

        val result = context.resolve(SingleFactoryMethod::class)

        assertTrue(result is Unresolved)
    }

    @Test
    fun `Class with single factory method creates instance`() {
        repeat(100) {
            val result = context.resolve(SingleFactoryMethod::class)

            assertNotNull(result)
            assertEquals(SingleFactoryMethod::class, result!!::class)
        }
    }

    @Test
    fun `Class with single factory method generates random content`() {
        repeat(100) {
            assertIsRandom {
                (context.resolve(SingleFactoryMethod::class) as SingleFactoryMethod).value
            }
        }
    }

    @Test
    fun `Factory method parameter can be overridden`() {
        repeat(100) {
            val context = context.copy(
                configuration = ConfigurationBuilder().apply {
                    property(SingleFactoryMethod::value) { "custom" }
                }.build()
            )

            val result = context.resolve(SingleFactoryMethod::class) as SingleFactoryMethod
            assertEquals("custom", result.value)
        }
    }

    @Test
    fun `Class with multiple constructors picks one at random`() {
        repeat(100) {
            assertIsRandom {
                val result = context.resolve(MultipleFactoryMethods::class) as MultipleFactoryMethods
                result.factoryMethodCalled
            }
        }
    }

    @Test
    fun `Class with mutable parameter is set at random`() {
        repeat(100) {
            assertIsRandom {
                val result =
                    context.resolve(MutableParameter::class) as MutableParameter
                result.parameter
            }
        }
    }

    @Test
    fun `Mutable parameter can be overridden`() {
        repeat(100) {
            val context = context.copy(
                configuration = ConfigurationBuilder().apply {
                    property(MutableParameter::parameter) { "custom" }
                }.build()
            )

            val result = context.resolve(MutableParameter::class) as MutableParameter
            assertEquals("custom", result.parameter)
        }
    }

    @Test
    fun `Constructs Java class with random constructor value`() {
        repeat(100) {
            assertIsRandom {
                (
                    context.resolve(
                        com.browarski.kotlinfixture.FactoryMethodJavaClass::class
                    ) as com.browarski.kotlinfixture.FactoryMethodJavaClass
                    ).constructor
            }
        }
    }

    @Test
    fun `Constructs Java class with random setter value`() {
        repeat(100) {
            assertIsRandom {
                (
                    context.resolve(
                        com.browarski.kotlinfixture.FactoryMethodJavaClass::class
                    ) as com.browarski.kotlinfixture.FactoryMethodJavaClass
                    ).mutable
            }
        }
    }

    @Test
    fun `Can override Java constructor arg`() {
        repeat(100) {
            val context = context.copy(
                configuration = ConfigurationBuilder().apply {
                    property<com.browarski.kotlinfixture.FactoryMethodJavaClass, String>("arg0") { "custom" }
                }.build()
            )

            val result = context.resolve(
                com.browarski.kotlinfixture.FactoryMethodJavaClass::class
            ) as com.browarski.kotlinfixture.FactoryMethodJavaClass
            assertEquals("custom", result.constructor)
        }
    }

    @Test
    fun `Can override Java setter`() {
        repeat(100) {
            val context = context.copy(
                configuration = ConfigurationBuilder().apply {
                    property<String>(com.browarski.kotlinfixture.FactoryMethodJavaClass::setMutable) { "custom" }
                }.build()
            )

            val result = context.resolve(
                com.browarski.kotlinfixture.FactoryMethodJavaClass::class
            ) as com.browarski.kotlinfixture.FactoryMethodJavaClass
            assertEquals("custom", result.mutable)
        }
    }

    @Suppress("UNUSED_PARAMETER", "unused")
    class MatchingNames(number: Int) {
        lateinit var number: String

        val isInitialised: Boolean
            get() = ::number.isInitialized

        companion object {
            fun create(value: Int) = MatchingNames(value)
        }
    }

    class SingleFactoryMethod private constructor(val value: String) {
        companion object {
            fun create(value: String) = SingleFactoryMethod(value)
        }
    }

    class MultipleFactoryMethods private constructor(val value: String, val factoryMethodCalled: String) {
        companion object {
            fun create() = MultipleFactoryMethods("default", "noParams")

            fun create(value: String) = MultipleFactoryMethods(value, "oneParam")
        }
    }

    class MutableParameter private constructor() {
        lateinit var parameter: String

        companion object {
            fun create() = MutableParameter()
        }
    }
}
