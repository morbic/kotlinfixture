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

package com.browarski.kotlinfixture.config

import com.browarski.kotlinfixture.Fixture
import com.browarski.kotlinfixture.assertIsRandom
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class GeneratorTest {

    private class IntGenerator(override val random: Random = Random) : Generator<Int> {
        override val fixture = Fixture(Configuration())
    }

    @Test
    fun `Empty range throws exception`() {
        assertThrows<NoSuchElementException> {
            IntGenerator().range(emptyList())
        }
    }

    @Test
    fun `Random values returned from range`() {
        assertIsRandom {
            IntGenerator().range(1..100)
        }
    }

    @Test
    fun `Range uses seeded random`() {
        val value1 = IntGenerator(random = Random(0)).range(1..100)
        val value2 = IntGenerator(random = Random(0)).range(1..100)

        assertEquals(value1, value2)
    }
}
