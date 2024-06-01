package com.example.unscramble.ui.broadcast

import androidx.compose.foundation.background
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
        localAddress = udpUiState.localAddress,
        broadcastPort = udpUiState.broadcastPort,
        hostAddress = udpUiState.hostAddress,
        startUdpListener = {
            // thread { udpViewModel.startUdpListener() }
            udpBroadcastViewModel.startUdpListener()
        },
        onEnterBroadcastPort = { udpBroadcastViewModel.onEnterBroadcastPort(it) },
    )

}

@Composable
fun UdpTesterLayout(
    modifier: Modifier = Modifier,
    localAddress: String,
    broadcastPort: Int,
    hostAddress: String,
    startUdpListener: () -> Unit,
    onEnterBroadcastPort: (String) -> Unit,
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val scope = rememberCoroutineScope()

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
                value = "$broadcastPort",
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
                text = localAddress,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface
            )
            Text(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .align(alignment = Alignment.CenterHorizontally),
                text = hostAddress,
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
