/*
 * Copyright 2019 Appmattus Limited
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
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.reflect.KClass

class PrimitiveArrayResolverTest {

    @Nested
    inner class Single {
        val context = TestContext(Configuration(), CompositeResolver(PrimitiveArrayResolver(), PrimitiveResolver()))

        @Test
        fun `Unknown class returns Unresolved`() {
            val result = context.resolve(Number::class)

            assertTrue(result is Unresolved)
        }

        @Test
        fun `Length of array matches configuration value of 3`() {
            val context = context.copy(configuration = Configuration(repeatCount = { 3 }))

            val result = context.resolve(IntArray::class) as IntArray

            assertEquals(3, result.size)
        }

        @Test
        fun `Length of array matches configuration value of 7`() {
            val context = context.copy(configuration = Configuration(repeatCount = { 7 }))

            val result = context.resolve(IntArray::class) as IntArray

            assertEquals(7, result.size)
        }
    }

    val context = TestContext(
        Configuration(),
        CompositeResolver(PrimitiveArrayResolver(), PrimitiveResolver(), CharResolver())
    )

    @ParameterizedTest
    @MethodSource("data")
    fun `Returns correct type`(clazz: KClass<*>) {
        val result = context.resolve(clazz)

        assertNotNull(result)
        result.shouldNotBeNull()
        result::class shouldBe clazz
    }

    @ParameterizedTest
    @MethodSource("data")
    fun `Random values returned`(clazz: KClass<*>) {
        assertIsRandom {
            context.resolve(clazz)
        }
    }

    companion object {
        @OptIn(ExperimentalUnsignedTypes::class)
        @JvmStatic
        fun data() = arrayOf(
            BooleanArray::class,
            ByteArray::class,
            DoubleArray::class,
            FloatArray::class,
            IntArray::class,
            LongArray::class,
            ShortArray::class,
            CharArray::class,
            UByteArray::class,
            UIntArray::class,
            ULongArray::class,
            UShortArray::class
        )
    }
}
