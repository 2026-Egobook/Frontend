package com.egobook.app.data.local

import java.util.UUID

object UUIDProvider {

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

}