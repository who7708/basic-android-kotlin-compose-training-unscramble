package com.example.unscramble.ui.broadcast

/**
 * @author Chris
 * @version 1.0
 * @since 2024/05/31
 */
data class UdpBroadcastUiState(
    val localAddress: String = "正在获取中...",
    val broadcastPort: Int = 18006,
    val hostAddress: String = "正在获取中...",
    var isRunning: Boolean = false,
    val urlToOpen: String = "",
)
