package com.example.unscramble.ui.broadcast

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.net.DatagramSocket
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicLong

/**
 * @author Chris
 * @version 1.0
 * @since 2024/05/31
 */
class UdpBroadcastViewModel : ViewModel() {

    companion object {
        private const val TAG: String = "UdpViewModel"

        // 前置机默认端口
        private const val FE_PORT = 8080
        // private const val FE_PORT = 30000

        // UDP 服务器监听的端口
        private const val BROADCAST_PORT = 18006

        // 读取数据时阻塞链路的超时时间 30s
        private const val BROADCAST_TIMEOUT = 60_000
        private val counter = AtomicLong(1)

        val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    }

    // UDP UI state
    private val _uiState = MutableStateFlow(UdpBroadcastUiState())
    val uiState: StateFlow<UdpBroadcastUiState> = _uiState.asStateFlow()

    // 弹窗提示使用
    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage
    fun showSnackbarMessage(message: String) {
        _snackbarMessage.value = message
    }

    // 定时监听udp消息
    fun startUdpListener(ctx: Context) {
        if (_uiState.value.isRunning) {
            Toast.makeText(ctx, "正常监听udp广播，请稍后再试", Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch {
            this@UdpBroadcastViewModel.startUdp(ctx)
        }
        // executorService.scheduleWithFixedDelay({
        //     this.startUdp()
        // }, 1, 3, TimeUnit.SECONDS)
    }

    // 启动监听udp
    private suspend fun startUdp(ctx: Context) {
        if (_uiState.value.isRunning) {
            Toast.makeText(ctx, "正常监听udp广播，请稍后再试", Toast.LENGTH_SHORT).show()
            return
        }
        // 接收的字节大小，客户端发送的数据不能超过这个大小
        val message = ByteArray(1024)
        val localhostStr: String = NetUtil.getLocalhostStr()
        this.updateLocalIp(localhostStr)
        // 建立Socket连接
        val datagramSocket = withContext(Dispatchers.IO) {
            // DatagramSocket(BROADCAST_PORT)
            DatagramSocket(_uiState.value.broadcastPort)
        }
        val packet = DatagramPacket(message, message.size)
        try {
            this.updateRunningState(true)
            Log.i(TAG, "UDP广播开始监听")
            withContext(Dispatchers.IO) {
                // 准备接收数据
                // datagramSocket.broadcast = true
                datagramSocket.soTimeout = BROADCAST_TIMEOUT
                datagramSocket.receive(packet)
                val hostAddress = "${packet.address?.hostAddress}:$FE_PORT"
                Log.d(TAG, "hostAddress:$hostAddress")
                this@UdpBroadcastViewModel.updateIpFromUdp(hostAddress)
            }
        } catch (e: Exception) {
            Log.w(TAG, "UDP广播接收异常", e)
            this.updateRunningState(false)
            Toast.makeText(ctx, "UDP广播接收异常" + e.message, Toast.LENGTH_SHORT).show()
        } finally {
            this.updateRunningState(false)
            // this.resetUiState()
            datagramSocket.close()
        }
    }

    // 重置ui状态
    private fun resetUiState() {
        _uiState.update { UdpBroadcastUiState() }
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
                """.trimIndent(),
                urlToOpen = "http://$hostAddress"
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

    fun onEnterBroadcastPort(broadcastPort: String) {
        _uiState.update {
            it.copy(
                broadcastPort = broadcastPort.toInt()
            )
        }
    }

}