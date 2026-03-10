package com.security.apianalyzer.analyzer

import com.security.apianalyzer.models.NetworkRequest

object TrafficAnalyzer {
    
    // Simple mock domains and regex for demonstration
    private val knownTrackers = listOf(
        "google-analytics.com",
        "mixpanel.com",
        "amplitude.com",
        "segment.com",
        "appsflyer.com"
    )

    private val piiPatterns = listOf(
        Regex("""\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Z|a-z]{2,7}\b"""), // Email
        Regex("""(?:\b|\+)[1-9]\d{9,14}\b""") // Phone number simple
    )
    
    private val apiKeyPatterns = listOf(
        Regex("""(?i)(?:api_key|apikey|token|auth_token|access_token)["'\s:=]+(["'][a-zA-Z0-9_\-]{16,}["']|[a-zA-Z0-9_\-]{16,})""")
    )

    fun analyze(request: NetworkRequest): NetworkRequest {
        // 1. Check for insecure traffic
        if (request.url.startsWith("http://")) {
            request.isEncrypted = false
        }

        // 2. Check for known trackers
        if (knownTrackers.any { request.host.contains(it, ignoreCase = true) }) {
            request.isTracker = true
        }

        // 3. Check for API keys or PII in headers/body (if decrypted)
        val combinedData = "${request.url} ${request.headers} ${request.requestBody ?: ""} ${request.responseBody ?: ""}"
        
        if (piiPatterns.any { it.containsMatchIn(combinedData) }) {
            request.hasPii = true
        }
        
        if (apiKeyPatterns.any { it.containsMatchIn(combinedData) }) {
            request.hasApiKey = true
        }

        return request
    }
}
