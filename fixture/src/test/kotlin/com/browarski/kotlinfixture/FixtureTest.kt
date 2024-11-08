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

package com.browarski.kotlinfixture

import com.browarski.kotlinfixture.config.Configuration
import com.browarski.kotlinfixture.resolver.Resolver
import io.kotest.matchers.collections.shouldBeEmpty
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random

class FixtureTest {

    @Test
    fun containsAllResolvers() {
        val actualResolvers = Configuration().resolvers
            .map { it::class.java.simpleName }
            .sorted()

        val missingResolvers =
            Classes.classGraph.getClassInfo(Resolver::class.java.name).classesImplementing
                .map { it.simpleName }
                .filterNot { it.endsWith("TestResolver") || it == "CompositeResolver" }
                .sorted()
                .toMutableList().apply {
                    removeAll(actualResolvers)
                }

        missingResolvers.shouldBeEmpty()
    }

    @Test
    fun `providing empty list has no effect`() {
        val fixture = kotlinFixture()
        assertIsRandom {
            fixture(emptyList<Int>())
        }
    }

    @Test
    fun `providing list of one returns its value`() {
        val fixture = kotlinFixture()
        repeat(100) {
            assertEquals(10, fixture(listOf(10)))
        }
    }

    @Test
    fun `providing multi-value list returns one its values`() {
        val fixture = kotlinFixture()
        repeat(100) {
            val result = fixture(listOf(10, 20, 30))

            if (result != 10 && result != 20 && result != 30) {
                fail<String>()
            }
        }
    }

    @Test
    fun `providing multi-value list returns a random value`() {
        val fixture = kotlinFixture()
        assertIsRandom {
            fixture(listOf(10, 20, 30))
        }
    }

    abstract class Superclass
    class SubclassA : Superclass()
    class SubclassB : Superclass()

    @Test
    fun `subclass is random by default for superclass class`() {
        val fixture = kotlinFixture()

        assertIsRandom {
            fixture<Superclass>()::class
        }
    }

    @Test
    fun `subclass can be overridden in initialisation`() {
        val fixture = kotlinFixture {
            subType<Superclass, SubclassA>()
        }

        repeat(100) {
            assertEquals(SubclassA::class, fixture<Superclass>()::class)
        }
    }

    @Test
    fun `subclass can be overridden in creation`() {
        val fixture = kotlinFixture()

        repeat(100) {
            val result = fixture<Superclass> {
                subType<Superclass, SubclassB>()
            }
            assertEquals(SubclassB::class, result::class)
        }
    }

    @Test
    fun `subclass can be overridden in creation when already overridden in initialisation`() {
        val fixture = kotlinFixture {
            subType<Superclass, SubclassA>()
        }

        repeat(100) {
            val result = fixture<Superclass> {
                subType<Superclass, SubclassB>()
            }
            assertEquals(SubclassB::class, result::class)
        }
    }

    @Test
    fun `subclass can be overridden in new when already overridden in initialisation`() {
        val baseFixture = kotlinFixture {
            subType<Superclass, SubclassA>()
        }

        repeat(100) {
            val fixture = baseFixture.new {
                subType<Superclass, SubclassB>()
            }
            val result = fixture<Superclass>()
            assertEquals(SubclassB::class, result::class)
        }
    }

    @Test
    fun `can override instance in initialisation`() {
        val fixture = kotlinFixture {
            factory<Number> { 10 }
        }

        repeat(100) {
            assertEquals(10, fixture<Number>())
        }
    }

    @Test
    fun `overridden instance in initialisation is random`() {
        val fixture = kotlinFixture {
            factory<Number> { Random.nextInt(1, 5) }
        }

        repeat(100) {
            assertTrue { fixture<Number>() in 1..5 }
        }
    }

    @Test
    fun `can override instance in creation`() {
        val fixture = kotlinFixture()

        val result = fixture<Number> {
            factory<Number> { 20 }
        }
        assertEquals(20, result)
    }

    @Test
    fun `overridden instance in creation is random`() {
        val fixture = kotlinFixture()

        repeat(100) {
            val result = fixture<Number> {
                factory<Number> { Random.nextInt(6, 10) }
            }

            assertTrue { result in 6..10 }
        }
    }

    @Test
    fun `can override instance in creation when overridden in initialisation`() {
        val fixture = kotlinFixture {
            factory<Number> { 10 }
        }

        val result = fixture<Number> {
            factory<Number> { 30 }
        }
        assertEquals(30, result)
    }

    @Test
    fun `can override instance in new when overridden in initialisation`() {
        val baseFixture = kotlinFixture {
            factory<Number> { 10 }
        }

        val fixture = baseFixture.new {
            factory<Number> { 30 }
        }

        val result = fixture<Number>()
        assertEquals(30, result)
    }

    data class KotlinClass(val readOnly: String, private var private: String) {
        var member: String? = null
        val alsoReadOnly: String? = null
        fun getPrivate(): String = private
    }

    @Test
    fun `constructor property can be set in fixture initialisation`() {
        val fixture = kotlinFixture {
            property(KotlinClass::readOnly) { "a" }
        }

        val instance = fixture<KotlinClass>()
        assertEquals("a", instance.readOnly)
    }

    @Test
    fun `constructor property can be overridden in fixture creation`() {
        val fixture = kotlinFixture()

        val instance = fixture<KotlinClass> {
            property(KotlinClass::readOnly) { "b" }
        }
        assertEquals("b", instance.readOnly)
    }

    @Test
    fun `constructor property can be overridden in fixture creation when already overridden in initialisation`() {
        val fixture = kotlinFixture {
            property(KotlinClass::readOnly) { "a" }
        }

        val instance = fixture<KotlinClass> {
            property(KotlinClass::readOnly) { "b" }
        }
        assertEquals("b", instance.readOnly)
    }

    @Test
    fun `constructor property can be overridden in new when already overridden in initialisation`() {
        val baseFixture = kotlinFixture {
            property(KotlinClass::readOnly) { "a" }
        }
        val fixture = baseFixture.new {
            property(KotlinClass::readOnly) { "b" }
        }

        val instance = fixture<KotlinClass>()
        assertEquals("b", instance.readOnly)
    }

    @Test
    fun `private constructor property can be set in fixture initialisation`() {
        val fixture = kotlinFixture {
            property<KotlinClass, String>("private") { "a" }
        }

        val instance = fixture<KotlinClass>()
        assertEquals("a", instance.getPrivate())
    }

    @Test
    fun `private constructor property can be overridden in fixture creation`() {
        val fixture = kotlinFixture()

        val instance = fixture<KotlinClass> {
            property<KotlinClass, String>("private") { "b" }
        }
        assertEquals("b", instance.getPrivate())
    }

    @Test
    fun `private constructor property can be overridden in fixture creation when already overridden`() {
        val fixture = kotlinFixture {
            property<KotlinClass, String>("private") { "a" }
        }

        val instance = fixture<KotlinClass> {
            property<KotlinClass, String>("private") { "b" }
        }
        assertEquals("b", instance.getPrivate())
    }

    @Test
    fun `private constructor property can be overridden in new when already overridden`() {
        val baseFixture = kotlinFixture {
            property<KotlinClass, String>("private") { "a" }
        }
        val fixture = baseFixture.new {
            property<KotlinClass, String>("private") { "b" }
        }

        val instance = fixture<KotlinClass>()
        assertEquals("b", instance.getPrivate())
    }

    @Test
    fun `member property can be set in fixture initialisation`() {
        val fixture = kotlinFixture {
            property(KotlinClass::member) { "a" }
        }

        val instance = fixture<KotlinClass>()
        assertEquals("a", instance.member)
    }

    @Test
    fun `member property can be overridden in fixture creation`() {
        val fixture = kotlinFixture()

        val instance = fixture<KotlinClass> {
            property(KotlinClass::member) { "b" }
        }
        assertEquals("b", instance.member)
    }

    @Test
    fun `member property can be overridden in fixture creation when already overridden in initialisation`() {
        val fixture = kotlinFixture {
            property(KotlinClass::member) { "a" }
        }

        val instance = fixture<KotlinClass> {
            property(KotlinClass::member) { "b" }
        }
        assertEquals("b", instance.member)
    }

    @Test
    fun `member property can be overridden in new when already overridden in initialisation`() {
        val baseFixture = kotlinFixture {
            property(KotlinClass::member) { "a" }
        }
        val fixture = baseFixture.new {
            property(KotlinClass::member) { "b" }
        }

        val instance = fixture<KotlinClass>()
        assertEquals("b", instance.member)
    }

    @Test
    fun `read only property cannot be set in fixture initialisation`() {
        assertThrows<IllegalStateException> {
            kotlinFixture {
                property(KotlinClass::alsoReadOnly) { "a" }
            }
        }
    }

    @Test
    fun `read only property cannot be overridden in fixture creation`() {
        assertThrows<IllegalStateException> {
            val fixture = kotlinFixture()

            fixture<KotlinClass> {
                property(KotlinClass::alsoReadOnly) { "b" }
            }
        }
    }

    @Test
    fun `java constructor property can be set in fixture initialisation`() {
        val fixture = kotlinFixture {
            property<com.browarski.kotlinfixture.FixtureTestJavaClass, String>("arg0") { "a" }
        }

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass>()
        assertEquals("a", instance.constructor)
    }

    @Test
    fun `java constructor property can be overridden in fixture creation`() {
        val fixture = kotlinFixture()

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass> {
            property<com.browarski.kotlinfixture.FixtureTestJavaClass, String>("arg0") { "b" }
        }
        assertEquals("b", instance.constructor)
    }

    @Test
    fun `java constructor property can be overridden in fixture creation when already overridden in initialisation`() {
        val fixture = kotlinFixture {
            property<com.browarski.kotlinfixture.FixtureTestJavaClass, String>("arg0") { "a" }
        }

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass> {
            property<com.browarski.kotlinfixture.FixtureTestJavaClass, String>("arg0") { "b" }
        }
        assertEquals("b", instance.constructor)
    }

    @Test
    fun `java constructor property can be overridden in new when already overridden in initialisation`() {
        val baseFixture = kotlinFixture {
            property<com.browarski.kotlinfixture.FixtureTestJavaClass, String>("arg0") { "a" }
        }
        val fixture = baseFixture.new {
            property<com.browarski.kotlinfixture.FixtureTestJavaClass, String>("arg0") { "b" }
        }

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass>()
        assertEquals("b", instance.constructor)
    }

    @Test
    fun `java member property can be set in fixture initialisation`() {
        val fixture = kotlinFixture {
            property<String>(com.browarski.kotlinfixture.FixtureTestJavaClass::setMutable) { "a" }
        }

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass>()
        assertEquals("a", instance.mutable)
    }

    @Test
    fun `java member property can be overridden in fixture creation`() {
        val fixture = kotlinFixture()

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass> {
            property<String>(com.browarski.kotlinfixture.FixtureTestJavaClass::setMutable) { "b" }
        }
        assertEquals("b", instance.mutable)
    }

    @Test
    fun `java member property can be overridden in fixture creation when already overridden in initialisation`() {
        val fixture = kotlinFixture {
            property<String>(com.browarski.kotlinfixture.FixtureTestJavaClass::setMutable) { "a" }
        }

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass> {
            property<String>(com.browarski.kotlinfixture.FixtureTestJavaClass::setMutable) { "b" }
        }
        assertEquals("b", instance.mutable)
    }

    @Test
    fun `java member property can be overridden in new when already overridden in initialisation`() {
        val baseFixture = kotlinFixture {
            property<String>(com.browarski.kotlinfixture.FixtureTestJavaClass::setMutable) { "a" }
        }
        val fixture = baseFixture.new {
            property<String>(com.browarski.kotlinfixture.FixtureTestJavaClass::setMutable) { "b" }
        }

        val instance = fixture<com.browarski.kotlinfixture.FixtureTestJavaClass>()
        assertEquals("b", instance.mutable)
    }
}
