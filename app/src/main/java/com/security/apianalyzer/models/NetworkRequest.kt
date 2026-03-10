package com.security.apianalyzer.models

import java.util.UUID

data class NetworkRequest(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val method: String,
    val url: String,
    val host: String,
    val headers: Map<String, String> = emptyMap(),
    val requestBody: String? = null,
    var responseCode: Int? = null,
    var responseHeaders: Map<String, String>? = null,
    var responseBody: String? = null,
    var isEncrypted: Boolean = true,
    var hasPii: Boolean = false,
    var hasApiKey: Boolean = false,
    var isTracker: Boolean = false
)
