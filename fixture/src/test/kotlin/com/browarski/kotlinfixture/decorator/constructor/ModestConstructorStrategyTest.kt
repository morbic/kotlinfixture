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

package com.browarski.kotlinfixture.decorator.constructor

import com.browarski.kotlinfixture.ContextImpl
import com.browarski.kotlinfixture.config.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.reflect.KClass

class ModestConstructorStrategyTest {

    @Test
    fun `Order constructors with lowest parameter count first`() {
        val context = ContextImpl(Configuration())

        val shuffledConstructors = mock<KClass<MultipleConstructors>> {
            on { constructors } doReturn MultipleConstructors::class.constructors.shuffled()
        }

        val result = ModestConstructorStrategy.constructors(context, shuffledConstructors).map {
            val emptyParameters = List<Any?>(it.parameters.size) { null }
            (it.call(*emptyParameters.toTypedArray()) as MultipleConstructors).constructorCalled
        }

        assertEquals(listOf("0", "1", "2", "3"), result)
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    class MultipleConstructors {
        val constructorCalled: String

        constructor() {
            constructorCalled = "0"
        }

        constructor(array: Array<String>?) {
            constructorCalled = "1"
        }

        constructor(array1: Array<String>?, array2: BooleanArray?, list3: List<String>?) {
            constructorCalled = "3"
        }

        constructor(array1: Array<String>?, array2: BooleanArray?) {
            constructorCalled = "2"
        }
    }
}
