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

package com.browarski.kotlinfixture.generex.config

import com.browarski.kotlinfixture.config.Generator
import com.mifmif.common.regex.Generex

/**
 * Generate a random string from a regular expression
 *
 * ```
 * data class DataClass(val index: String, val value: String)
 *
 * val indexRegex = "[a-z][0-9]".toRegex()
 * val valueRegex = "[A-Z]{3}".toRegex()
 *
 * val fixture = kotlinFixture {
 *     factory<String> { regexToRandom(indexRegex) }
 *
 *     property(DataClass::value) { regexToRandom(valueRegex) }
 * }
 *
 * println(fixture<DataClass>()) // DataClass(index=m3, value=CGJ)
 * ```
 *
 * IMPORTANT: Be careful with object creation inside the generation function
 *             as it will be called for every instance of the object you create.
 */
fun Generator<String>.regexToRandom(regex: String, minLength: Int = 1, maxLength: Int = Int.MAX_VALUE): String {
    return Generex(regex).apply {
        setSeed(random.nextLong())
    }.random(minLength, maxLength)
}

/**
 * Generate a random string from a regular expression
 *
 * ```
 * data class DataClass(val index: String, val value: String)
 *
 * val indexRegex = "[a-z][0-9]".toRegex()
 * val valueRegex = "[A-Z]{3}".toRegex()
 *
 * val fixture = kotlinFixture {
 *     factory<String> { regexToRandom(indexRegex) }
 *
 *     property(DataClass::value) { regexToRandom(valueRegex) }
 * }
 *
 * println(fixture<DataClass>()) // DataClass(index=m3, value=CGJ)
 * ```
 *
 * IMPORTANT: Be careful with object creation inside the generation function as it will be called
 *              for every instance of the object you create.
 */
fun Generator<String>.regexToRandom(regex: Regex, minLength: Int = 1, maxLength: Int = Int.MAX_VALUE): String {
    return regexToRandom(regex.pattern, minLength, maxLength)
}
