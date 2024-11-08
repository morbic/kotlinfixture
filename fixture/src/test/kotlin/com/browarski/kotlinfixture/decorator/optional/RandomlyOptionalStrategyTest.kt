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

package com.browarski.kotlinfixture.decorator.optional

import com.browarski.kotlinfixture.TestContext
import com.browarski.kotlinfixture.assertIsRandom
import com.browarski.kotlinfixture.config.ConfigurationBuilder
import com.browarski.kotlinfixture.resolver.TestResolver
import org.junit.jupiter.api.Test

class RandomlyOptionalStrategyTest {

    private val testContext = TestContext(ConfigurationBuilder().build(), TestResolver())

    data class DataClass(val optionalValue: String = "hello")

    @Test
    fun `Strategy RandomlyOptionalStrategy returns random value`(): Unit = with(testContext) {
        RandomlyOptionalStrategy.apply {
            assertIsRandom {
                generateAsOptional(DataClass::class, "optionalValue")
            }
        }
    }
}
