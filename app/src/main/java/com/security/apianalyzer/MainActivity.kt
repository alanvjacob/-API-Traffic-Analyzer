package com.security.apianalyzer

import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.security.apianalyzer.core.vpn.LocalVpnService
import com.security.apianalyzer.ui.theme.APITrafficAnalyzerTheme
import com.security.apianalyzer.ui.DashboardScreen
import com.security.apianalyzer.ui.RequestDetailScreen
import com.security.apianalyzer.models.NetworkRequest

class MainActivity : ComponentActivity() {

    private val vpnPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = Intent(this, LocalVpnService::class.java)
            startService(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APITrafficAnalyzerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isVpnActive by remember { mutableStateOf(false) }
                    var selectedRequest by remember { mutableStateOf<NetworkRequest?>(null) }
                    
                    // Mock data to demonstrate the UI since full interception is complex
                    val mockRequests = remember {
                        listOf(
                            NetworkRequest(
                                method = "GET", url = "https://api.github.com/users/octocat", host = "api.github.com",
                                responseCode = 200, isEncrypted = true, hasPii = false, isTracker = false,
                                responseBody = "{\n  \"login\": \"octocat\",\n  \"id\": 1\n}"
                            ),
                            NetworkRequest(
                                method = "POST", url = "http://api.weather.com/v1/update", host = "api.weather.com",
                                responseCode = 200, isEncrypted = false, hasPii = false, isTracker = false,
                                requestBody = "{\"location\":\"New York\"}"
                            ),
                            NetworkRequest(
                                method = "POST", url = "https://google-analytics.com/collect", host = "google-analytics.com",
                                responseCode = 204, isEncrypted = true, hasPii = true, isTracker = true,
                                responseHeaders = mapOf("Server" to "Google Frontend"), requestBody = "v=1&tid=UA-XXXXX-Y&cid=555&t=pageview&uid=testuser@email.com"
                            ),
                            NetworkRequest(
                                method = "GET", url = "https://api.myapp.com/v1/profile", host = "api.myapp.com",
                                responseCode = 401, isEncrypted = true, hasPii = false, isTracker = false, hasApiKey = true,
                                headers = mapOf("Authorization" to "Bearer my_super_secret_token_123456789")
                            )
                        )
                    }

                    if (selectedRequest != null) {
                        RequestDetailScreen(
                            request = selectedRequest!!,
                            onBack = { selectedRequest = null }
                        )
                    } else if (isVpnActive) {
                        Column {
                            Button(
                                onClick = { 
                                    stopVpn()
                                    isVpnActive = false
                                },
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Stop Interception")
                            }
                            DashboardScreen(
                                requests = mockRequests,
                                onRequestClick = { selectedRequest = it }
                            )
                        }
                    } else {
                        MainScreen(
                            onStartVpn = { 
                                startVpn()
                                isVpnActive = true
                            }
                        )
                    }
                }
            }
        }
    }

    private fun startVpn() {
        val vpnIntent = VpnService.prepare(this)
        if (vpnIntent != null) {
            vpnPermissionLauncher.launch(vpnIntent)
        } else {
            // Already have permission
            val intent = Intent(this, LocalVpnService::class.java)
            startService(intent)
        }
    }

    private fun stopVpn() {
        // We will send an action to the service to stop
        val intent = Intent(this, LocalVpnService::class.java)
        intent.action = "STOP_VPN"
        startService(intent)
    }
}

@Composable
fun MainScreen(onStartVpn: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "API Traffic Analyzer",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onStartVpn,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Start VPN Interception", color = MaterialTheme.colorScheme.background)
        }
    }
}
