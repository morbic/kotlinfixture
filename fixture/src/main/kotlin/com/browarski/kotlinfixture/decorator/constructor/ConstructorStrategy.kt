/*
 * Copyright 2020-2023 Appmattus Limited
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

import com.browarski.kotlinfixture.Context
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Strategy used to determine order to try class constructors when generating an instance.
 */
interface ConstructorStrategy {
    /**
     * Returns [obj] constructors in the order to try when generating an instance.
     */
    fun constructors(context: Context, obj: KClass<*>): Collection<KFunction<*>>

    /**
     * Constructors of the class with Serializable constructors filtered out
     */
    val KClass<*>.filteredConstructors: Collection<KFunction<*>>
        get() = constructors.filterNot { it.isSerializationConstructor() }

    private fun KFunction<Any>.isSerializationConstructor(): Boolean {
        val lastParameterType = (parameters.lastOrNull()?.type?.classifier as? KClass<*>)?.qualifiedName
        return lastParameterType == "kotlinx.serialization.internal.SerializationConstructorMarker"
    }
}
