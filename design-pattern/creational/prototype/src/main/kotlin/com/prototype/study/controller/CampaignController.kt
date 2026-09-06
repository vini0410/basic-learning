package com.prototype.study.controller

import com.prototype.study.models.Campaign
import com.prototype.study.service.CampaignPrototypeFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/campaigns")
class CampaignController(private val factory: CampaignPrototypeFactory) {

    @GetMapping("/clone/{prototypeName}")
    fun cloneCampaign(@PathVariable prototypeName: String): Campaign? {
        return factory.createCampaign(prototypeName)
    }
}