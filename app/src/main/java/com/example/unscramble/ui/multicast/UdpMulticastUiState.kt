package com.example.unscramble.ui.multicast

/**
 * @author Chris
 * @version 1.0
 * @since 2024/05/31
 */
data class UdpMulticastUiState(
    val localAddress: String = "正在获取中...",
    val hostAddress: String = "正在获取中...",
    var isRunning: Boolean = false
)