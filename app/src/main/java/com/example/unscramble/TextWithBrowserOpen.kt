package com.example.unscramble

/**
 * @author Chris
 * @version 1.0.0
 * @since 2024/11/19
 */
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun TextWithBrowserOpen(ip: String) {
    val context = LocalContext.current
    val openBrowserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // 这里可以处理打开浏览器后的结果，不过一般打开浏览器不需要处理返回结果
    }
    Text(
        text = "打开浏览器",
        modifier = Modifier.clickable {
            val intent = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                android.net.Uri.parse(ip)
            )
            if (context is ComponentActivity) {
                openBrowserLauncher.launch(intent)
            }
        }
    )
}

// @Preview
@Composable
fun PreviewTextWithBrowserOpen() {
    TextWithBrowserOpen("192.168.67.71")
}