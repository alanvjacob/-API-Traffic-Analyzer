package com.security.apianalyzer.core.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.security.apianalyzer.MainActivity
import com.security.apianalyzer.core.proxy.ProxyServer
import kotlinx.coroutines.*
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class LocalVpnService : VpnService() {

    private val TAG = "LocalVpnService"
    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var proxyServer: ProxyServer? = null

    companion object {
        const val ACTION_STOP_VPN = "STOP_VPN"
        private const val NOTIFICATION_CHANNEL_ID = "vpn_status"
        private const val NOTIFICATION_ID = 1
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_VPN) {
            stopVpn()
            return START_NOT_STICKY
        }
        
        startForeground(NOTIFICATION_ID, createNotification("Intercepting Traffic"))
        startVpn()
        
        return START_STICKY
    }

    private fun startVpn() {
        if (vpnInterface != null) return // Already running

        try {
            val builder = Builder()
                .setSession("API Analyzer VPN")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0) // Route all traffic
                .setMtu(1500)
                
            // Optional: allow passing traffic of this app directly
            builder.addDisallowedApplication(packageName)

            vpnInterface = builder.establish()

            if (vpnInterface != null) {
                Log.i(TAG, "VPN Interface established")
                proxyServer = ProxyServer(vpnInterface!!.fileDescriptor)
                proxyServer?.start()

                // Start simple IO loop to read packets (for debugging if proxy isn't fully set up)
                // In a real implementation the ProxyServer handles this
                /*
                serviceScope.launch {
                    val vpnInput = FileInputStream(vpnInterface!!.fileDescriptor)
                    val vpnOutput = FileOutputStream(vpnInterface!!.fileDescriptor)
                    val buffer = ByteBuffer.allocate(32767)

                    while (isActive) {
                        try {
                            val length = vpnInput.read(buffer.array())
                            if (length > 0) {
                                // Packet intercepted!
                                // Here we would pass the raw IP packet to the TCP/IP parser
                                // For now, we just drop it (blackhole)
                            }
                            buffer.clear()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error reading from VPN interface", e)
                            break
                        }
                    }
                }
                */
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to establish VPN", e)
            stopVpn()
        }
    }

    private fun stopVpn() {
        try {
            proxyServer?.stop()
            vpnInterface?.close()
            vpnInterface = null
            serviceJob.cancelChildren()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing VPN interface", e)
        }
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopVpn()
        serviceJob.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "VPN Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows if the API traffic analyzer is active"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("API Traffic Analyzer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_secure) // Using standard icon
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
