/*
 * Copyright 2021 Appmattus Limited
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

import com.browarski.kotlinfixture.decorator.Decorator
import com.browarski.kotlinfixture.decorator.exception.ExceptionDecorator
import com.browarski.kotlinfixture.decorator.filter.Filter
import com.browarski.kotlinfixture.decorator.filter.FilterDecorator
import com.browarski.kotlinfixture.decorator.logging.LoggingDecorator
import com.browarski.kotlinfixture.decorator.recursion.RecursionDecorator
import com.browarski.kotlinfixture.resolver.AbstractClassResolver
import com.browarski.kotlinfixture.resolver.ArrayKTypeResolver
import com.browarski.kotlinfixture.resolver.AtomicKTypeResolver
import com.browarski.kotlinfixture.resolver.BigDecimalResolver
import com.browarski.kotlinfixture.resolver.BigIntegerResolver
import com.browarski.kotlinfixture.resolver.CalendarResolver
import com.browarski.kotlinfixture.resolver.CharResolver
import com.browarski.kotlinfixture.resolver.ClassResolver
import com.browarski.kotlinfixture.resolver.CurrencyResolver
import com.browarski.kotlinfixture.resolver.DateResolver
import com.browarski.kotlinfixture.resolver.EnumMapResolver
import com.browarski.kotlinfixture.resolver.EnumResolver
import com.browarski.kotlinfixture.resolver.EnumSetResolver
import com.browarski.kotlinfixture.resolver.FactoryMethodResolver
import com.browarski.kotlinfixture.resolver.FactoryResolver
import com.browarski.kotlinfixture.resolver.FakeResolver
import com.browarski.kotlinfixture.resolver.FileResolver
import com.browarski.kotlinfixture.resolver.FormatResolver
import com.browarski.kotlinfixture.resolver.HashtableKTypeResolver
import com.browarski.kotlinfixture.resolver.IterableKTypeResolver
import com.browarski.kotlinfixture.resolver.JodaTimeResolver
import com.browarski.kotlinfixture.resolver.KFunctionResolver
import com.browarski.kotlinfixture.resolver.KNamedPropertyResolver
import com.browarski.kotlinfixture.resolver.KTypeResolver
import com.browarski.kotlinfixture.resolver.LocaleResolver
import com.browarski.kotlinfixture.resolver.MapKTypeResolver
import com.browarski.kotlinfixture.resolver.ObjectResolver
import com.browarski.kotlinfixture.resolver.PrimitiveArrayResolver
import com.browarski.kotlinfixture.resolver.PrimitiveResolver
import com.browarski.kotlinfixture.resolver.Resolver
import com.browarski.kotlinfixture.resolver.SealedClassResolver
import com.browarski.kotlinfixture.resolver.StringResolver
import com.browarski.kotlinfixture.resolver.SubTypeResolver
import com.browarski.kotlinfixture.resolver.TimeResolver
import com.browarski.kotlinfixture.resolver.TupleKTypeResolver
import com.browarski.kotlinfixture.resolver.UriResolver
import com.browarski.kotlinfixture.resolver.UrlResolver
import com.browarski.kotlinfixture.resolver.UuidResolver
import com.browarski.kotlinfixture.toUnmodifiableList
import com.browarski.kotlinfixture.toUnmodifiableMap
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * The [Configuration] for generating the current fixture. This is a combination of all previous configurations.
 * @property repeatCount The length used for lists and maps.
 * @property propertiesRepeatCount Overrides the length used for lists and maps
 * on constructor parameters and mutable properties when generating instances of generic classes.
 * @property properties Overrides for constructor parameters and mutable properties
 *  when generating instances of generic classes.
 * @property factories Given instances for a particular class using a factory method.
 * @property subTypes Superclass to subclass mapping for subtypes.
 * @property random Random to use for generating random values. This may be a seeded random.
 * @property decorators Each [Decorator] wraps the resolver chain.
 * @property resolvers The resolver chain, each [Resolver] is called in order until one handles the input object.
 * @property strategies Strategy settings for altering the behaviour of [resolvers] and [decorators].
 * @property filters Sequence filters for generated values.
 */
data class Configuration internal constructor(
    val repeatCount: () -> Int = defaultRepeatCount,
    val propertiesRepeatCount: Map<KClass<*>, Map<String, () -> Int>> =
        emptyMap<KClass<*>, Map<String, () -> Int>>().toUnmodifiableMap(),
    val properties: Map<KClass<*>, Map<String, GeneratorFun>> =
        emptyMap<KClass<*>, Map<String, GeneratorFun>>().toUnmodifiableMap(),
    val factories: Map<KType, GeneratorFun> =
        emptyMap<KType, GeneratorFun>().toUnmodifiableMap(),
    val subTypes: Map<KClass<*>, KClass<*>> = emptyMap<KClass<*>, KClass<*>>().toUnmodifiableMap(),
    val random: Random = defaultRandom,
    val decorators: List<Decorator> = defaultDecorators.toUnmodifiableList(),
    val resolvers: List<Resolver> = defaultResolvers.toUnmodifiableList(),
    val strategies: Map<KClass<*>, Any> = emptyMap<KClass<*>, Any>().toUnmodifiableMap(),
    internal val filters: Map<KType, Filter> = emptyMap<KType, Filter>().toUnmodifiableMap()
) {

    private companion object {
        private val defaultRepeatCount: () -> Int = { 5 }

        private val defaultRandom = Random

        private val defaultDecorators = listOf(
            FilterDecorator(),
            ExceptionDecorator(),
            RecursionDecorator(),
            LoggingDecorator()
        )

        private val defaultResolvers = listOf(
            FactoryResolver(),
            SubTypeResolver(),

            CharResolver(),
            StringResolver(),
            PrimitiveResolver(),
            UrlResolver(),
            UriResolver(),
            BigDecimalResolver(),
            BigIntegerResolver(),
            UuidResolver(),
            EnumResolver(),
            CalendarResolver(),
            DateResolver(),
            TimeResolver(),
            JodaTimeResolver(),
            FileResolver(),
            FormatResolver(),
            CurrencyResolver(),
            LocaleResolver(),

            ObjectResolver(),
            SealedClassResolver(),

            AtomicKTypeResolver(),
            TupleKTypeResolver(),

            ArrayKTypeResolver(),
            PrimitiveArrayResolver(),
            HashtableKTypeResolver(),
            IterableKTypeResolver(),
            EnumSetResolver(),
            EnumMapResolver(),
            MapKTypeResolver(),

            KTypeResolver(),
            FakeResolver(),
            KNamedPropertyResolver(),
            KFunctionResolver(),

            AbstractClassResolver(),

            ClassResolver(),
            FactoryMethodResolver()
        )
    }
}
