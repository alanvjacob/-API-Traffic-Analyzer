package com.security.apianalyzer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.security.apianalyzer.models.NetworkRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    requests: List<NetworkRequest>,
    onRequestClick: (NetworkRequest) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Traffic Feed", color = MaterialTheme.colorScheme.primary) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(requests) { request ->
                RequestItem(request = request, onClick = { onRequestClick(request) })
            }
        }
    }
}

@Composable
fun RequestItem(request: NetworkRequest, onClick: () -> Unit) {
    val methodColor = when(request.method.uppercase()) {
        "GET" -> MaterialTheme.colorScheme.tertiary
        "POST" -> MaterialTheme.colorScheme.primary
        "DELETE" -> MaterialTheme.colorScheme.error
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = request.method.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = methodColor,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = request.responseCode?.toString() ?: "---",
                        color = if (request.responseCode in 200..299) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val time = sdf.format(Date(request.timestamp))
                Text(
                    text = time,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = request.url,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                fontSize = 14.sp
            )
            
            // Badges for alerts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!request.isEncrypted) {
                    AlertBadge("HTTP (Insecure)", MaterialTheme.colorScheme.error)
                }
                if (request.isTracker) {
                    AlertBadge("Tracker", Color(0xFFFFA500)) // Orange
                }
                if (request.hasPii) {
                    AlertBadge("PII Detected", MaterialTheme.colorScheme.error)
                }
                if (request.hasApiKey) {
                    AlertBadge("API Key Leak", MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AlertBadge(text: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
