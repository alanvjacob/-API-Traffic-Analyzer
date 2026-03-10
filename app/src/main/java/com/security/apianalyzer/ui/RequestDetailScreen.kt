package com.security.apianalyzer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.apianalyzer.models.NetworkRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    request: NetworkRequest,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Request Details", color = MaterialTheme.colorScheme.primary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Overview Section
            DetailSection("Overview") {
                DetailRow("URL", request.url)
                DetailRow("Method", request.method)
                DetailRow("Status Code", request.responseCode?.toString() ?: "N/A")
                DetailRow("Host", request.host)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Request Headers Section
            if (request.headers.isNotEmpty()) {
                DetailSection("Request Headers") {
                    request.headers.forEach { (key, value) ->
                        DetailRow(key, value)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Request Body Section
            if (!request.requestBody.isNullOrEmpty()) {
                DetailSection("Request Body") {
                    CodeBlock(request.requestBody!!)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Response Headers Section
            if (!request.responseHeaders.isNullOrEmpty()) {
                DetailSection("Response Headers") {
                    request.responseHeaders!!.forEach { (key, value) ->
                        DetailRow(key, value)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Response Body Section
            if (!request.responseBody.isNullOrEmpty()) {
                DetailSection("Response Body") {
                    CodeBlock(request.responseBody!!)
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                content()
            }
        }
    }
}

@Composable
fun DetailRow(key: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        Text(
            text = key,
            fontSize = 12.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun CodeBlock(code: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0F0F0F))
            .padding(8.dp)
    ) {
        Text(
            text = code,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.tertiary,
            fontFamily = FontFamily.Monospace
        )
    }
}
