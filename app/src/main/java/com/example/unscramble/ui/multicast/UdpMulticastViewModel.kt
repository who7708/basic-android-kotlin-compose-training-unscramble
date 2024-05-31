package com.example.unscramble.ui.multicast

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.hutool.core.net.NetUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.InetAddress
import java.net.MulticastSocket
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicLong

/**
 * @author Chris
 * @version 1.0
 * @since 2024/05/31
 */
class UdpMulticastViewModel : ViewModel() {

    companion object {
        private const val TAG: String = "UdpViewModel"

        // 前置机默认端口
        private const val FE_PORT = 8080

        // UDP 服务器监听的端口
        private const val MULTICAST_PORT: Int = 28006
        private const val MULTICAST_ADDRESS = "224.0.0.1"

        // 读取数据时阻塞链路的超时时间 30s
        private const val BROADCAST_TIMEOUT = 30_000
        private val counter = AtomicLong(1)

        val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    }

    // UDP UI state
    private val _uiState = MutableStateFlow(UdpMulticastUiState())
    val uiState: StateFlow<UdpMulticastUiState> = _uiState.asStateFlow()

    // 定时监听udp消息
    fun startUdpListener() {
        if (_uiState.value.isRunning) {
            return
        }
        viewModelScope.launch {
            this@UdpMulticastViewModel.startUdp()
        }
        // executorService.scheduleWithFixedDelay({
        //     this.startUdp()
        // }, 1, 3, TimeUnit.SECONDS)
    }

    // 启动监听udp
    private suspend fun startUdp() {
        if (_uiState.value.isRunning) {
            return
        }
        val localhostStr: String = NetUtil.getLocalhostStr()
        this.updateLocalIp(localhostStr)

        val group = withContext(Dispatchers.IO) {
            InetAddress.getByName(MULTICAST_ADDRESS)
        }
        // 建立Socket连接
        val datagramSocket = withContext(Dispatchers.IO) {
            val multicastSocket = MulticastSocket(MULTICAST_PORT)
            multicastSocket.joinGroup(group)
            multicastSocket
        }
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        val message = ByteArray(1024)
        val packet = DatagramPacket(message, message.size)
        try {
            this.updateRunningState(true)
            Log.i(TAG, "UDP组播开始监听")
            withContext(Dispatchers.IO) {
                // 准备接收数据
                // udpSocket.broadcast = true
                datagramSocket.soTimeout = BROADCAST_TIMEOUT
                datagramSocket.receive(packet)
                val hostAddress = "${packet.address?.hostAddress}:$FE_PORT"
                Log.d(TAG, "hostAddress:$hostAddress")
                this@UdpMulticastViewModel.updateIpFromUdp(hostAddress)
            }
        } catch (e: Exception) {
            Log.w(TAG, "UDP组播接收异常", e)
            e.printStackTrace()
        } finally {
            this.updateRunningState(false)
            // this.resetUiState()
            datagramSocket.close()
        }
    }

    // 重置ui状态
    private fun resetUiState() {
        _uiState.update { UdpMulticastUiState() }
    }

    // 更新 UDP 监听启动状态
    private fun updateRunningState(running: Boolean) {
        _uiState.update {
            it.copy(
                isRunning = running
            )
        }
    }

    // 更新 UDP 广播地址
    private fun updateIpFromUdp(hostAddress: String) {
        _uiState.update {
            it.copy(
                hostAddress = """
                    序号：${counter.getAndIncrement()}
                    服务地址：$hostAddress
                """.trimIndent()
            )
        }
    }

    // 更新 UDP 广播地址
    private fun updateLocalIp(localAddress: String) {
        _uiState.update {
            it.copy(
                localAddress = """
                    序号：${counter.get()} 
                    本机地址：$localAddress
                """.trimIndent()
            )
        }
    }

}