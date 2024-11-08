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

package com.browarski.kotlinfixture.decorator.filter

import com.browarski.kotlinfixture.TestContext
import com.browarski.kotlinfixture.config.ConfigurationBuilder
import com.browarski.kotlinfixture.resolver.Resolver
import com.browarski.kotlinfixture.typeOf
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class FilterDecoratorTest {

    private val mockIterator = mock<Iterator<Float>> {
        onGeneric { next() } doReturn 0.5f
    }

    private val mockSequence = mock<Sequence<Float>> {
        on { iterator() } doReturn mockIterator
    }

    private val config = ConfigurationBuilder().apply {
        filter<Float> { mockSequence }
    }.build()

    private val resolver = mock<Resolver>()

    private val context = TestContext(config, resolver)

    @Test
    fun `not KType calls original resolver`() {
        val decoratedResolver = FilterDecorator().decorate(resolver)
        decoratedResolver.resolve(context, Float::class)

        verify(resolver).resolve(eq(context), eq(Float::class))
        verifyNoMoreInteractions(resolver)
    }

    @Test
    fun `not in filter list calls original resolver`() {
        val decoratedResolver = FilterDecorator().decorate(resolver)
        decoratedResolver.resolve(context, typeOf<String>())

        verify(resolver).resolve(eq(context), eq(typeOf<String>()))
        verifyNoMoreInteractions(resolver)
    }

    @Test
    fun `in filter list calls filter next`() {
        val decoratedResolver = FilterDecorator().decorate(resolver)
        val result = decoratedResolver.resolve(context, typeOf<Float>())

        result shouldBe 0.5f
        verify(mockIterator).next()
        verifyNoMoreInteractions(resolver)
    }
}
