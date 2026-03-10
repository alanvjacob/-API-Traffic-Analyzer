# On-Device Privacy & API Traffic Analyzer (No-Root Interceptor)

An advanced Android application built with Kotlin and Jetpack Compose that leverages the `VpnService` API to create a local, on-device loopback VPN. This app intercepts, inspects, and logs outbound network traffic from other applications on the phone—**without requiring root access**.

## 🛡️ Executive Summary

Analyzing what mobile apps send over the network typically requires a complex setup involving a rooted device, a computer proxy (like Burp Suite or Charles), and tedious certificate pinning bypasses. This project simplifies that process by moving the interception layer directly onto the device itself.

**Key Features:**
- **Local VPN Interception**: Captures outbound TCP/UDP traffic at the IP/TCP layer using Android's `VpnService`.
- **Insecure Traffic Detection**: Automatically flags HTTP (plaintext) traffic.
- **Tracker Identification**: Cross-references request hosts against a list of known tracking and telemetry domains.
- **Sensitive Data Leaks**: Scans headers and payloads for Personally Identifiable Information (PII) and hardcoded API keys.
- **Hacker Aesthetic UI**: A modern, responsive dashboard built in Jetpack Compose featuring a sleek dark mode.

## ⚠️ Ethical & Legal Disclaimer

**This tool is designed strictly for educational purposes, personal privacy auditing, and authorized security research.** 
- Do not use this tool to intercept traffic on networks or devices you do not own or do not have explicit permission to audit.
- Intercepting and decrypting TLS traffic (even on your own device) may violate the Terms of Service of third-party applications.
- The author(s) assume no liability and are not responsible for any misuse or damage caused by this program.

## 🏗️ Architecture & Tech Stack

- **Language:** Kotlin 1.9+
- **UI Framework:** Jetpack Compose (Material 3)
- **Core APIs:** `android.net.VpnService`
- **Asynchrony:** Kotlin Coroutines & Flow
- **Minimum SDK:** API 26 (Android 8.0)
- **Target SDK:** API 34 (Android 14)

### Core Components
1. **`LocalVpnService.kt`**: Configures the TUN interface to route all device traffic internally.
2. **`ProxyServer.kt`**: Reads raw IP packets from the TUN interface descriptor. (Note: Implementing a full userspace TCP/IP stack from scratch is highly complex; this module currently demonstrates the architecture and scaffolding).
3. **`TlsInterceptor.kt` & `CertUtils.kt`**: Utilities for dynamic X.509 certificate generation to perform on-device Man-in-the-Middle (MITM) for TLS decryption.
4. **`TrafficAnalyzer.kt`**: The inspection engine that runs regex and heuristics to find anomalies and leaks.

## 🚀 Getting Started

### Prerequisites
- Android Studio Iguana (or newer)
- An Android Emulator or Physical Device running Android 8.0+

### Installation & Build
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/ApiTrafficAnalyzer.git
   ```
2. Open the project in Android Studio.
3. Sync Gradle dependencies.
4. Click **Run** (`Shift + F10`) to explicitly install the application on your test device.

### Usage
1. Open the **API Traffic Analyzer** app.
2. Click **Start VPN Interception**.
3. Android will display a security prompt asking for permission to set up a VPN connection. Accept the prompt.
4. A persistent notification will appear indicating the VPN is active.
5. You will see simulated network traffic populate the dashboard (as an architectural demonstration). Tap on any request to view detailed headers, body payloads, and security alerts.



## 🔮 Future Enhancements
- Integration of a lightweight C/C++ or existing Java TCP/IP parsing library to fully reconstruct complex TCP streams.
- Full BouncyCastle integration for dynamic cert signing.
- Capillary integration for deeper packet inspection.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
