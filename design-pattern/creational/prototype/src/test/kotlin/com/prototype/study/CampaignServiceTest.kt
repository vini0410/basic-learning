package com.prototype.study

import com.prototype.study.models.Campaign
import com.prototype.study.service.CampaignPrototypeFactory
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CampaignServiceTest {
    private val factory = CampaignPrototypeFactory().apply { loadPrototype() }

    @Test
    fun testCreateCampaignWithValidPrototype() {
        val cloned = factory.createCampaign("base")
        assertNotNull(cloned)
        assertEquals("Base Campaign", cloned?.name)
        assertEquals(listOf("all"), cloned.audience)
    }

    @Test
    fun testCreateCampaignWithInvalidPrototype() {
        val cloned = factory.createCampaign("invalid")
        assertNull(cloned)
    }

    @Test
    fun testCloneIndependence() {
        val campaign1 = factory.createCampaign("base")
        val campaign2 = factory.createCampaign("base")
        
        // Modificar o primeiro clone não afeta o segundo
        campaign1?.audience = listOf("new_audience")
        assertEquals(listOf("all"), campaign2?.audience)
    }
}