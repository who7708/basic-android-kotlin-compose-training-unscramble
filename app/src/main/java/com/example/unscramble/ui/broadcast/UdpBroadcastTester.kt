package com.example.unscramble.ui.broadcast

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unscramble.R
import com.example.unscramble.ui.theme.UnscrambleTheme
import java.util.regex.Pattern

/**
 * @author Chris
 * @version 1.0
 * @since 2024/05/30
 */
@Composable
fun UdpBroadcastTester(
    udpBroadcastViewModel: UdpBroadcastViewModel = viewModel()
) {
    val udpUiState by udpBroadcastViewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    UdpTesterLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(mediumPadding),
        udpUiState,
        // localAddress = udpUiState.localAddress,
        // broadcastPort = udpUiState.broadcastPort,
        // hostAddress = udpUiState.hostAddress,
        // urlToOpen = udpUiState.urlToOpen,
        startUdpListener = {
            // thread { udpViewModel.startUdpListener() }
            udpBroadcastViewModel.startUdpListener()
        },
        onEnterBroadcastPort = { udpBroadcastViewModel.onEnterBroadcastPort(it) },
    )

}

fun extractAndConvertToUrl(input: String): String {
    val pattern = Pattern.compile("(d{1,3}.d{1,3}.d{1,3}.d{1,3}):(d+)")
    val matcher = pattern.matcher(input)

    return if (matcher.find()) {
        val ip = matcher.group(1)
        val port = matcher.group(2)
        "http://$ip:$port"
    } else {
        ""
    }
}


@Composable
fun UdpTesterLayout(
    modifier: Modifier = Modifier,
    udpUiState: UdpBroadcastUiState,
    // localAddress: String,
    // broadcastPort: Int,
    // hostAddress: String,
    // urlToOpen: String,
    startUdpListener: () -> Unit,
    onEnterBroadcastPort: (String) -> Unit,
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val openBrowserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // 这里可以处理打开浏览器后的结果，不过一般打开浏览器不需要处理返回结果
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ) {
            OutlinedTextField(
                value = "${udpUiState.broadcastPort}",
                singleLine = true,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                ),
                onValueChange = onEnterBroadcastPort,
                label = { Text(stringResource(R.string.enter_udp_port)) },
                isError = false,
                // keyboardType = KeyboardType.Number,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done,
                    // 限制输入类型
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = { startUdpListener() }
                )
            )
            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                text = udpUiState.localAddress,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface
            )
            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.CenterHorizontally)
                    .clickable {
                        if (udpUiState.urlToOpen.isBlank()) {
                            Log.i("UdpBroadcastTester", "UdpTesterLayout: url is blank")
                            return@clickable
                        }
                        val intent = android.content.Intent(
                            android.content.Intent.ACTION_VIEW,
                            android.net.Uri.parse(udpUiState.urlToOpen)
                        )
                        // 打开浏览器
                        if (context is ComponentActivity) {
                            openBrowserLauncher.launch(intent)
                        }
                    },
                text = udpUiState.hostAddress,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { startUdpListener() }
            ) {
                Text(
                    text = stringResource(R.string.start_udp_broadcast),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun UdpTesterPreview() {
    UnscrambleTheme {
        UdpBroadcastTester()
    }
}
