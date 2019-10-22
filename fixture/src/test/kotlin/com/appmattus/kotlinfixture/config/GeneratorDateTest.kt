package com.appmattus.kotlinfixture.config

import com.appmattus.kotlinfixture.assertIsRandom
import com.appmattus.kotlinfixture.kotlinFixture
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GeneratorDateTest {

    private val now = Date()

    @Test
    fun `Date class returns date`() {
        val fixture = kotlinFixture()

        val result = fixture<Date>()

        assertNotNull(result)
        assertEquals(Date::class, result::class)
    }

    @Test
    fun `After specification gives date in the future`() {

        val fixture = kotlinFixture {
            instance<Date> { after(now) }
        }

        repeat(100) {
            val result = fixture<Date>()

            assertTrue {
                result.time >= now.time
            }
        }
    }

    @Test
    fun `After specification is random`() {

        val fixture = kotlinFixture {
            instance<Date> { after(now) }
        }

        assertIsRandom {
            fixture<Date>()
        }
    }

    @Test
    fun `After specification uses seeded random`() {
        val fixture = kotlinFixture {
            instance<Date> { after(now) }
        }

        val value1 = fixture<Date> {
            random = Random(0)
        }
        val value2 = fixture<Date> {
            random = Random(0)
        }

        assertEquals(value1, value2)
    }

    @Test
    fun `Before specification gives date in the past`() {
        val fixture = kotlinFixture {
            instance<Date> { before(now) }
        }

        repeat(100) {
            val result = fixture<Date>()

            assertTrue {
                result.time <= now.time
            }
        }
    }

    @Test
    fun `Before specification is random`() {

        val fixture = kotlinFixture {
            instance<Date> { before(now) }
        }

        assertIsRandom {
            fixture<Date>()
        }
    }

    @Test
    fun `Before specification uses seeded random`() {
        val fixture = kotlinFixture {
            instance<Date> { before(now) }
        }

        val value1 = fixture<Date> {
            random = Random(0)
        }
        val value2 = fixture<Date> {
            random = Random(0)
        }

        assertEquals(value1, value2)
    }

    @Test
    fun `Between specification gives date between two dates`() {
        val minTime = now.time - TimeUnit.HOURS.toMillis(1)

        val fixture = kotlinFixture {
            instance<Date> { between(Date(minTime), now) }
        }

        repeat(100) {
            val result = fixture<Date>()

            assertTrue {
                result.time >= minTime
                result.time <= now.time
            }
        }
    }

    @Test
    fun `Between specification is random`() {
        val minTime = now.time - TimeUnit.HOURS.toMillis(1)

        val fixture = kotlinFixture {
            instance<Date> { between(Date(minTime), now) }
        }

        assertIsRandom {
            fixture<Date>()
        }
    }

    @Test
    fun `Between specification uses seeded random`() {
        val minTime = now.time - TimeUnit.HOURS.toMillis(1)

        val fixture = kotlinFixture {
            instance<Date> { between(Date(minTime), now) }
        }

        val value1 = fixture<Date> {
            random = Random(0)
        }
        val value2 = fixture<Date> {
            random = Random(0)
        }

        assertEquals(value1, value2)
    }
}
