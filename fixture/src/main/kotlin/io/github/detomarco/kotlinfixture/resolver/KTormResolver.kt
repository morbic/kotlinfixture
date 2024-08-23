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

package io.github.detomarco.kotlinfixture.resolver

import io.github.detomarco.kotlinfixture.Context
import io.github.detomarco.kotlinfixture.Unresolved
import org.ktorm.entity.Entity
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.isSubclassOf

internal class KTormResolver : Resolver {

    override fun resolve(context: Context, obj: Any): Any {
        if (hasKTorm && obj is KClass<*>) {
            if (obj.isSubclassOf(Entity::class) && obj != Entity::class) {
                val entity = Entity.create(obj)

                obj.declaredMembers.filterIsInstance<KProperty<*>>().forEach {
                    entity[it.name] = context.resolve(it.returnType)
                }

                return entity
            }
        }

        return Unresolved.Unhandled
    }

    companion object {
        private val hasKTorm: Boolean by lazy {
            try {
                Class.forName("org.ktorm.entity.Entity", false, KTormResolver::class.java.classLoader)
                true
            } catch (expected: ClassNotFoundException) {
                false
            }
        }
    }
}