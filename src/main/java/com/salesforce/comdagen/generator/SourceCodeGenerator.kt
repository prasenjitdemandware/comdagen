/*
 *  Copyright (c) 2018, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.comdagen.generator

import com.salesforce.comdagen.attributeDefinitions
import com.salesforce.comdagen.config.SourceCodeConfiguration
import com.salesforce.comdagen.model.AttributeDefinition
import com.salesforce.comdagen.model.SourceCodeGroup
import java.util.*

data class SourceCodeGenerator(override val configuration: SourceCodeConfiguration) :
    Generator<SourceCodeConfiguration, SourceCodeGroup> {

    //override val creatorFunc = { _: Int, seed: Long -> SourceCodeGroup(seed, configuration) }

    override val objects: Sequence<SourceCodeGroup>
        get() {
            val sourceCodeGroup: MutableList<SourceCodeGroup> = mutableListOf()

            val rng = Random(configuration.initialSeed)

            for (i in (1..configuration.elementCount)) {

                val seed = rng.nextLong()
                val pricebooks = listOf<String>("$i-NewPricebook-SC")

//                val pricebooks = listOf<String>("$i-1-NewPricebook-SC", "$i-2-NewPricebook-SC",
//                        "$i-3-NewPricebook-SC","$i-4-NewPricebook-SC","$i-5-NewPricebook-SC")

                val obj = SourceCodeGroup(seed, configuration, pricebooks)
                sourceCodeGroup.add(obj)
            }

            return sourceCodeGroup.asSequence()
        }
    override val metadata: Map<String, Set<AttributeDefinition>>
        get() = mapOf("SourceCodeGroup" to configuration.attributeDefinitions())
}
