package com.prototype.study

import com.prototype.study.models.Campaign
import com.prototype.study.service.CampaignPrototypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CampaignControllerTest {
    private val factory = CampaignPrototypeFactory().apply { loadPrototype() }

    @Test
    fun testCloneCampaign() {
        val cloned = factory.createCampaign("base")
        assertNotNull(cloned)
        assertEquals("Base Campaign", cloned?.name)
    }
}