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
import java.util.Locale
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.valueParameters

internal interface PopulateInstance {

    data class CallContext(
        val context: Context,
        val obj: KClass<*>,
        val constructorParameterNames: Set<String?>,
        val callingClass: KClass<*>
    )

    fun populatePropertiesAndSetters(
        callContext: CallContext,
        result: Any?
    ): Boolean {
        return try {
            populateKotlinProperties(callContext, result) && populateJavaSetters(callContext, result)
        } catch (expected: Error) {
            // If retrieving setters fails we fail regardless of constructor used
            false
        }
    }

    private fun populateJavaSetters(
        callContext: CallContext,
        result: Any?
    ): Boolean {
        callContext.obj.memberFunctions.filter {
            it.name.startsWith("set") && it.valueParameters.size == 1
        }.filterNot {
            val name = it.name.removePrefix("set")
                .replaceFirstChar { s -> s.lowercase(Locale.getDefault()) }
            callContext.constructorParameterNames.contains(name)
        }.forEach {
            val propertyResult = callContext.context.resolve(
                KNamedPropertyRequest(it.name, callContext.callingClass, it.valueParameters[0].type)
            )

            if (propertyResult is Unresolved) {
                return false
            }

            it.safeCall(result, propertyResult)
        }

        return true
    }

    private fun populateKotlinProperties(
        callContext: CallContext,
        result: Any?
    ): Boolean {
        callContext.obj.settableMutableProperties()
            .filterNot { callContext.constructorParameterNames.contains(it.name) }
            .forEach { property ->
                val propertyResult = callContext.context.resolve(
                    KNamedPropertyRequest(property.name, callContext.callingClass, property.returnType)
                )

                if (propertyResult is Unresolved) {
                    return false
                }

                // Property might not be visible
                property.setter.safeCall(result, propertyResult)
            }

        return true
    }

    private fun KClass<*>.settableMutableProperties(): List<KMutableProperty<*>> {
        return memberProperties
            .filterIsInstance<KMutableProperty<*>>()
            .filter { it.setter.visibility != KVisibility.PRIVATE }
    }

    fun KClass<*>.constructorParameterNames(): Set<String?> {
        return constructors.flatMap { constructor ->
            constructor.parameters.map { it.name }
        }.toSet()
    }

    private fun KMutableProperty.Setter<*>.safeCall(obj: Any?, value: Any?) {
        try {
            call(obj, value)
        } catch (expected: Exception) {
            // ignored
        }
    }

    private fun KFunction<*>.safeCall(obj: Any?, value: Any?) {
        try {
            call(obj, value)
        } catch (expected: Exception) {
            // ignored
        }
    }
}
