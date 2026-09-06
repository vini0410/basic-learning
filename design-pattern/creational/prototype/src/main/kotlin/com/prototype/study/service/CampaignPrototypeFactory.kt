package com.prototype.study.service

import com.prototype.study.models.Campaign
import java.util.*

class CampaignPrototypeFactory {
    private val prototypes: MutableMap<String, Campaign> = HashMap()

    fun loadPrototype() {
        val baseCampaign = Campaign(
            name = "Base Campaign",
            audience = listOf("all"),
            startDate = Date()
        )
        prototypes["base"] = baseCampaign
    }

    fun createCampaign(prototypeName: String): Campaign? {
        val campaign = prototypes[prototypeName]
        return campaign?.clone()
    }
}