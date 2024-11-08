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

import com.browarski.kotlinfixture.Context
import com.browarski.kotlinfixture.Unresolved
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Date
import java.util.concurrent.TimeUnit

internal class DateResolver : Resolver {

    override fun resolve(context: Context, obj: Any): Any {
        return when (obj) {
            Date::class -> context.generateJavaUtilDate()
            java.sql.Date::class -> java.sql.Date(context.generateJavaUtilDate().time)
            java.sql.Time::class -> java.sql.Time(context.generateJavaUtilDate().time)
            java.sql.Timestamp::class -> java.sql.Timestamp(context.generateJavaUtilDate().time)
            else -> Unresolved.Unhandled
        }
    }

    private fun Context.generateJavaUtilDate(): Date {
        return Date(
            @Suppress("MagicNumber")
            random.nextLong(
                referenceTime - TimeUnit.DAYS.toMillis(3650),
                referenceTime + TimeUnit.DAYS.toMillis(3650)
            )
        )
    }

    companion object {
        private val referenceTime = ZonedDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault()).toEpochSecond()
    }
}
