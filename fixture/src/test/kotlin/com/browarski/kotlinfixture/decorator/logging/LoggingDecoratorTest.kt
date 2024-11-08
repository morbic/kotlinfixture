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

package com.browarski.kotlinfixture.decorator.logging

import com.browarski.kotlinfixture.Context
import com.browarski.kotlinfixture.TestContext
import com.browarski.kotlinfixture.config.ConfigurationBuilder
import com.browarski.kotlinfixture.resolver.Resolver
import com.browarski.kotlinfixture.typeOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.internal.verification.Times
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.reflect.KType

class LoggingDecoratorTest {

    private val mockLoggingStrategy = mock<LoggingStrategy>()

    private val config = ConfigurationBuilder().apply {
        loggingStrategy(mockLoggingStrategy)
    }.build()

    @Test
    fun `logs each request`() {
        val resolver = TestResolver(listOf(typeOf<Int>(), typeOf<Short>()))

        val decoratedResolver = LoggingDecorator().decorate(resolver)
        decoratedResolver.resolve(TestContext(config, decoratedResolver), typeOf<Float>())

        argumentCaptor<KType> {
            verify(mockLoggingStrategy, Times(3)).request(capture())

            assertEquals(listOf(typeOf<Float>(), typeOf<Int>(), typeOf<Short>()), allValues)
        }
    }

    @Test
    fun `logs each response`() {
        val resolver = TestResolver(listOf(typeOf<Int>(), typeOf<Short>()))

        val decoratedResolver = LoggingDecorator().decorate(resolver)
        decoratedResolver.resolve(TestContext(config, decoratedResolver), typeOf<Float>())

        verify(mockLoggingStrategy).response(typeOf<Short>(), Result.success(typeOf<Short>()))
        verify(mockLoggingStrategy).response(typeOf<Int>(), Result.success(typeOf<Short>()))
        verify(mockLoggingStrategy).response(typeOf<Float>(), Result.success(typeOf<Short>()))
    }

    class TestResolver(list: List<KType>) : Resolver {

        private val objects = list.iterator()

        override fun resolve(context: Context, obj: Any): Any? {
            return if (objects.hasNext()) {
                context.resolve(objects.next())
            } else {
                obj
            }
        }
    }
}
