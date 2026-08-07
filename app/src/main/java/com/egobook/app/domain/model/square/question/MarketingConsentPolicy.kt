package com.egobook.app.domain.model.square.question

object MarketingConsentPolicy {
    fun requiresConfirmation(currentlyEnabled: Boolean, requestedEnabled: Boolean): Boolean =
        requestedEnabled && !currentlyEnabled
}
