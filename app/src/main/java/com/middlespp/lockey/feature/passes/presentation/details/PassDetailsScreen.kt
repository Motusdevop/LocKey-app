package com.middlespp.lockey.feature.passes.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.time.Instant

@Composable
fun PassDetailsScreen(
    state: PassDetailsUiState,
    onScanClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(DetailsBackgroundBrush)
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBackClick) { Text("Back") }
                Text(
                    text = "Pass details",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            when {
                state.isLoading -> CircularProgressIndicator()
                state.details == null -> Text("Pass not found", style = MaterialTheme.typography.bodyLarge)
                else -> {
                    val pass = state.details.pass
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(pass.lockId, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            DetailRow("Access code", pass.accessCode)
                            DetailRow("Starts", pass.bookingStartsAt.shortLabel())
                            DetailRow("Ends", pass.bookingEndsAt.shortLabel())
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = { onScanClick(pass.lockId) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.large
                            ) {
                                Text("Scan lock QR")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}

private fun Instant.shortLabel(): String = toString()
    .replace('T', ' ')
    .removeSuffix("Z")
    .take(16)

private val DetailsBackgroundBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFF7FAFF), Color(0xFFEAF1FB), Color(0xFFF8FAFF))
)
