/*
 *  Copyright (c) 2018, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.comdagen.model

import com.salesforce.comdagen.RandomData
import com.salesforce.comdagen.XMLOutputProducer
import com.salesforce.comdagen.attributeDefinitions
import com.salesforce.comdagen.config.SourceCodeConfiguration
import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.LoggerFactory
import java.util.*

data class SourceCodeGroup(private val seed: Long, private val config: SourceCodeConfiguration,
                           private val pbs: List<String>) {
    val id: String
        get() = "comdagen-${Math.abs(seed + "newSourceCodeId".hashCode())}"

    val description: String
        get() = RandomData.getRandomSentence(seed + "sourceCodeDescription".hashCode())

    val sourceCodes: List<String>
        get() {
            val rng = Random(seed + "sourceCodes".hashCode())

            val n = if (config.maxCodes > config.minCodes)
                rng.nextInt(config.maxCodes - config.minCodes) + config.minCodes
            else config.minCodes

            // generate n strings with length 12
            return (1..n).map {
                var code = RandomStringUtils.random(12, 0, 0, true, true, null, Random(rng.nextLong()))
                LOGGER.info("Source Code : " + code)
                code

            }
        }

    val customAttributes: List<CustomAttribute>
        get() = config.attributeDefinitions().map { CustomAttribute(it, seed) }

    val pricebooks: List<String> = pbs

    companion object {
        private val LOGGER = LoggerFactory.getLogger(XMLOutputProducer::class.java)

    }
}
