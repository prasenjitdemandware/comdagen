/*
 *  Copyright (c) 2018, salesforce.com, inc.
 *  All rights reserved.
 *  SPDX-License-Identifier: BSD-3-Clause
 *  For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.comdagen.generator

import com.salesforce.comdagen.GeneratorHelper
import com.salesforce.comdagen.GeneratorHelper.getPartialProductSequence
import com.salesforce.comdagen.SupportedCurrency
import com.salesforce.comdagen.attributeDefinitions
import com.salesforce.comdagen.config.CatalogListConfiguration
import com.salesforce.comdagen.config.PricebookConfiguration
import com.salesforce.comdagen.model.AttributeDefinition
import com.salesforce.comdagen.model.ChildPricebook
import com.salesforce.comdagen.model.ParentPriceBook
import com.salesforce.comdagen.model.Pricebook
import java.util.*


/**
 * Generate sequence of Pricebook objects
 *
 * @property configuration configure pricebook generation
 *
 * @property catalogConfiguration configuration of catalogs associated to this pricebooks
 *
 * @property currencies generate pricebooks for different currencies
 *
 * @property objects sequence of generated pricebooks
 */
data class PricebookGenerator(
    override val configuration: PricebookConfiguration,
    private val catalogConfiguration: CatalogListConfiguration,
    private val currencies: List<SupportedCurrency>
) : Generator<PricebookConfiguration, Pricebook> {

    override val objects: Sequence<Pricebook>
        get() {
            val pricebooks: MutableList<Pricebook> = mutableListOf()

            //for (currency in currencies) {
                //val rng = Random(configuration.initialSeed)

                //for (i in (1..configuration.elementCount)) {

                    //val seed = rng.nextLong()

                    // generate ParentPriceBook
                    var allProductIds = GeneratorHelper.getProductIds(catalogConfiguration)

                    var totalProductCount = catalogConfiguration.totalProductCount()

                    //System.out.println("Total product count = " + totalProductCount)

                    // Use productIds for all pricebooks when we need same product price entries all across
//                    var productIds =
//                            getPartialProductSequence(configuration.seed, totalProductCount, configuration.coverage, allProductIds)

                    var productIds = allProductIds

                    // All price books not starting with name "1-" will have 4000 random price entries
                    // selected from the complete product list
                    if (!configuration.pbName.startsWith("1-")) {
                        var allProductIdsMutable = allProductIds.toMutableList()
                        allProductIdsMutable.shuffle()
                        productIds = allProductIdsMutable.subList(0, 4000).asSequence()
                    }

                    val parent = ParentPriceBook(
                        productIds,
                            configuration.seed,
                        metadata["PriceBook"].orEmpty(),
                        configuration,
                            configuration.currency,
                            configuration.index,
                        catalogConfiguration.hashCode(),
                            configuration.pbName
                    )
                    pricebooks.add(parent)

                    // generate child pricebooks for parent if any
                    configuration.children?.forEach { childConfig ->
                        pricebooks.add(
                            ChildPricebook(
                                parent,
                                getPartialProductSequence(
                                        configuration.seed,
                                    totalProductCount,
                                    configuration.coverage,
                                    allProductIds
                                ),
                                    configuration.seed,
                                metadata["PriceBook"].orEmpty(),
                                childConfig,
                                    configuration.currency,
                                    configuration.index,
                                catalogConfiguration.hashCode(),
                                    configuration.pbName
                            )
                        )
                    }
                //}
            //}

            return pricebooks.asSequence()
        }

    override val metadata: Map<String, Set<AttributeDefinition>> = mapOf(
        "PriceBook" to configuration.attributeDefinitions()
    )
}
