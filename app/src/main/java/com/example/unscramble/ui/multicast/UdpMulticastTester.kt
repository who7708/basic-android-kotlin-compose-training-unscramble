package com.example.unscramble.ui.multicast

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
fun UdpMulticastTester(
    udpMulticastViewModel: UdpMulticastViewModel = viewModel()
) {
    val udpUiState by udpMulticastViewModel.uiState.collectAsState()
    val mediumPadding = dimensionResource(R.dimen.padding_medium)

    UdpTesterLayout(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(mediumPadding),
        startUdpListener = {
            // thread { udpViewModel.startUdpListener() }
            udpMulticastViewModel.startUdpListener()
        },
        localAddress = udpUiState.localAddress,
        hostAddress = udpUiState.hostAddress
    )

}

@Composable
fun UdpTesterLayout(
    modifier: Modifier = Modifier,
    startUdpListener: () -> Unit,
    localAddress: String,
    hostAddress: String
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
                    text = stringResource(R.string.start_udp_multicast),
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
        UdpMulticastTester()
    }
}
