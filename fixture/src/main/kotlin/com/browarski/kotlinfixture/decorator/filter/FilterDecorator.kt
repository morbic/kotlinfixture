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

package com.browarski.kotlinfixture.decorator.filter

import com.browarski.kotlinfixture.Context
import com.browarski.kotlinfixture.decorator.Decorator
import com.browarski.kotlinfixture.resolver.Resolver
import kotlin.reflect.KType

internal class FilterDecorator : Decorator {

    override fun decorate(resolver: Resolver): Resolver = FilterResolver(resolver)

    private inner class FilterResolver(
        private val resolver: Resolver
    ) : Resolver {

        override fun resolve(context: Context, obj: Any): Any? {
            if (obj is KType) {
                context.configuration.filters[obj]?.let {
                    return it.next(resolver, context)
                }
            }

            return resolver.resolve(context, obj)
        }
    }
}
