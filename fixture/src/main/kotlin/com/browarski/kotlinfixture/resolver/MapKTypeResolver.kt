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

package com.browarski.kotlinfixture.resolver

import com.browarski.kotlinfixture.Context
import com.browarski.kotlinfixture.Unresolved
import com.browarski.kotlinfixture.Unresolved.Companion.createUnresolved
import com.browarski.kotlinfixture.decorator.nullability.wrapNullability
import java.util.IdentityHashMap
import java.util.NavigableMap
import java.util.SortedMap
import java.util.TreeMap
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ConcurrentNavigableMap
import java.util.concurrent.ConcurrentSkipListMap
import kotlin.reflect.KClass
import kotlin.reflect.KType

internal class MapKTypeResolver : Resolver {

    @Suppress("ReturnCount")
    override fun resolve(context: Context, obj: Any): Any? {
        if (obj is KType && obj.classifier is KClass<*>) {
            val collection = createCollection(obj)

            if (collection != null) {
                return context.wrapNullability(obj) {
                    populateCollection(obj, collection)
                }
            }
        }

        return Unresolved.Unhandled
    }

    @Suppress("ReturnCount")
    private fun Context.populateCollection(obj: KType, collection: MutableMap<Any?, Any?>): Any {
        val keyType = obj.arguments[0].type!!
        val valueType = obj.arguments[1].type!!

        repeat(configuration.repeatCount()) {
            val key = resolve(keyType)
            if (key is Unresolved) {
                return createUnresolved("Unable to resolve ${obj.classifier} key $keyType", listOf(key))
            }

            val value = resolve(valueType)
            if (value is Unresolved) {
                return createUnresolved("Unable to resolve ${obj.classifier} value $valueType", listOf(value))
            }

            collection[key] = value
        }

        return collection
    }

    private fun createCollection(obj: KType): MutableMap<Any?, Any?>? = when (obj.classifier as KClass<*>) {
        Map::class,
        java.util.AbstractMap::class,
        HashMap::class -> HashMap()

        SortedMap::class,
        NavigableMap::class,
        TreeMap::class -> TreeMap()

        ConcurrentMap::class,
        ConcurrentHashMap::class -> ConcurrentHashMap()

        ConcurrentNavigableMap::class,
        ConcurrentSkipListMap::class -> ConcurrentSkipListMap()

        LinkedHashMap::class -> LinkedHashMap()
        IdentityHashMap::class -> IdentityHashMap()
        WeakHashMap::class -> WeakHashMap()

        else -> {
            null
        }
    }
}
