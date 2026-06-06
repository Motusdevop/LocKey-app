package com.middlespp.lockey.feature.scanner.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ScannerScreen(
    state: ScannerUiState,
    onCodeChange: (String) -> Unit,
    onScannedCode: (String) -> Unit,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(ScannerBackgroundBrush)
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            TextButton(onClick = onBackClick) { Text("Back") }
            Text("LocKey scanner", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(state.message, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = state.code,
                        onValueChange = onCodeChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("QR payload") },
                        minLines = 4
                    )
                    Button(
                        onClick = onSubmitClick,
                        enabled = !state.isBusy,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (state.isBusy) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Submit")
                    }
                    TextButton(onClick = { onScannedCode(state.code) }) {
                        Text("Use pasted payload as scanned")
                    }
                }
            }
        }
    }
}

private val ScannerBackgroundBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFF7FAFF), Color(0xFFEAF1FB), Color(0xFFF8FAFF))
)
