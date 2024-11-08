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

package com.browarski.kotlinfixture.decorator.recursion

import kotlin.reflect.KType

/**
 * A [RecursionStrategy] that throws an [UnsupportedOperationException] when recursion is detected.
 */
object ThrowingRecursionStrategy : RecursionStrategy {

    override fun handleRecursion(type: KType, stack: Collection<KType>): Any? {
        check(stack.isNotEmpty()) { "Stack must be populated" }

        val errorMessage = "Unable to create ${stack.first()} with circular reference: ${stack.toStackString(type)}"
        throw UnsupportedOperationException(errorMessage)
    }

    private fun Collection<KType>.toStackString(type: KType) = (this + type).joinToString(separator = " → ")
}
