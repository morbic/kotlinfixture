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
import io.kotest.matchers.types.shouldBeTypeOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID

class UuidResolverTest {
    private val context = TestContext(Configuration(), UuidResolver())

    @Test
    fun `Unknown class returns Unresolved`() {
        val result = context.resolve(Number::class)

        assertTrue(result is Unresolved)
    }

    @Test
    fun `UUID class returns uuid`() {
        val result = context.resolve(UUID::class)

        assertNotNull(result)
        result.shouldNotBeNull()
        result.shouldBeTypeOf<UUID>()
    }

    @Test
    fun `Random values returned`() {
        assertIsRandom {
            context.resolve(UUID::class)
        }
    }

    @Test
    fun `Uses seeded random`() {
        val value1 = (context.seedRandom().resolve(UUID::class) as UUID).toString()
        val value2 = (context.seedRandom().resolve(UUID::class) as UUID).toString()

        assertEquals(value1, value2)
    }
}
