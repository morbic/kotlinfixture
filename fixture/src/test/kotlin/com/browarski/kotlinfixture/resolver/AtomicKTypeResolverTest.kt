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
import com.browarski.kotlinfixture.typeOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass
import kotlin.reflect.KType

class AtomicKTypeResolverTest {

    val context = TestContext(
        Configuration(),
        CompositeResolver(AtomicKTypeResolver(), StringResolver(), KTypeResolver())
    )

    @Test
    fun `Unknown class returns Unresolved`() {
        val result = context.resolve(Number::class)

        assertTrue(result is Unresolved)
    }

    @Test
    fun `Unknown type parameter returns Unresolved`() {
        val result = context.resolve(typeOf<AtomicReference<Number>>())

        assertTrue(result is Unresolved)
    }

    @Test
    fun `Random nullability returned`() {
        assertIsRandom {
            context.resolve(typeOf<AtomicReference<String>?>()) == null
        }
    }

    private val contextParam = TestContext(
        Configuration(),
        CompositeResolver(
            AtomicKTypeResolver(),
            StringResolver(),
            PrimitiveResolver(),
            PrimitiveArrayResolver(),
            KTypeResolver()
        )
    )

    @ParameterizedTest
    @MethodSource("data")
    fun `creates instance`(type: KType, resultClass: KClass<*>) {
        val result = contextParam.resolve(type)

        assertTrue {
            resultClass.isInstance(result)
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    fun `Random values returned`(type: KType) {
        assertIsRandom {
            contextParam.resolve(type).toString()
        }
    }

    @ParameterizedTest
    @MethodSource("data")
    fun `Uses seeded random`(type: KType) {
        val value1 = contextParam.seedRandom().resolve(type)
        val value2 = contextParam.seedRandom().resolve(type)

        assertEquals(value1.toString(), value2.toString())
    }

    companion object {
        @JvmStatic
        fun data() = arrayOf(
            Arguments.of(typeOf<AtomicBoolean>(), AtomicBoolean::class),
            Arguments.of(typeOf<AtomicInteger>(), AtomicInteger::class),
            Arguments.of(typeOf<AtomicLong>(), AtomicLong::class),
            Arguments.of(typeOf<AtomicIntegerArray>(), AtomicIntegerArray::class),
            Arguments.of(typeOf<AtomicLongArray>(), AtomicLongArray::class),
            Arguments.of(typeOf<AtomicReference<String>>(), AtomicReference::class)
        )
    }
}
