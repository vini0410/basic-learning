package com.prototype.study.models

import java.util.*

class Campaign : Cloneable {
    var name: String = ""
    var audience: List<String> = emptyList()
    var startDate: Date = Date()

    constructor(name: String, audience: List<String>, startDate: Date) {
        this.name = name
        this.audience = audience
        this.startDate = startDate
    }

    public override fun clone(): Campaign {
        return Campaign(
            name = this.name,
            audience = ArrayList(this.audience),
            startDate = Date(this.startDate.time)
        )
    }
}