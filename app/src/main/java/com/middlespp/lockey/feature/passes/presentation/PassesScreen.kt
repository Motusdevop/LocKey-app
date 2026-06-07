package com.middlespp.lockey.feature.passes.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.middlespp.lockey.feature.passes.domain.model.AccessPass
import kotlinx.coroutines.flow.SharedFlow
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PassesScreen(
    state: PassesUiState,
    events: SharedFlow<PassesUiEvent>,
    onPassClick: (String) -> Unit,
    onScanClick: () -> Unit,
    onDeletePass: (String) -> Unit,
    onUndoDelete: () -> Unit,
    onTogglePinned: (String) -> Unit,
    onMovePass: (fromIndex: Int, toIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(events) {
        events.collect { event ->
            when (event) {
                is PassesUiEvent.PassDeleted -> {
                    val result = snackbarHostState.showSnackbar(
                        message = "Пропуск ${event.lockId} удален",
                        actionLabel = "Отменить",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) onUndoDelete()
                }

                PassesUiEvent.ImportFailed -> snackbarHostState.showSnackbar("Не удалось импортировать пропуск")
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onScanClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 10.dp)
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Light
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PremiumBackgroundBrush)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 112.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    LocKeyHero(
                        passCount = state.passes.size,
                        pinnedCount = state.pinnedCount,
                        onScanClick = onScanClick,
                        modifier = Modifier.statusBarsPadding()
                    )
                }

                when {
                    state.isLoading -> item { LoadingCard() }
                    !state.hasPasses -> item { EmptyPassesCard(onScanClick = onScanClick) }
                    else -> {
                        item {
                            SectionHeader(
                                title = "Пропуска доступа",
                                subtitle = "Сначала закрепленные и ближайшие бронирования"
                            )
                        }

                        items(
                            items = state.passes,
                            key = { it.lockId }
                        ) { pass ->
                            PassCard(
                                pass = pass,
                                onClick = { onPassClick(pass.lockId) },
                                onDeleteClick = { onDeletePass(pass.lockId) },
                                onTogglePinned = { onTogglePinned(pass.lockId) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Keeps the callback part of the screen contract while drag reorder UI is introduced later.
    remember(onMovePass) { onMovePass }
}

@Composable
private fun LocKeyHero(
    passCount: Int,
    pinnedCount: Int,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(HeroBrush)
                .padding(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            color = Color.White.copy(alpha = 0.18f),
                            contentColor = Color.White,
                            shape = CircleShape
                        ) {
                            Text(
                                text = "ЧАСТНЫЙ ДОСТУП",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = "LocKey",
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Управляй доступом к доверенным замкам.",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFFD9E6FF)
                        )
                    }

                    Text(
                        text = "LocKey",
                        modifier = Modifier
                            .padding(start = 12.dp),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFFBFD3FF),
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatPill(label = "Пропусков", value = passCount.toString())
                    StatPill(label = "Закреплено", value = pinnedCount.toString())
                }

                Button(
                    onClick = onScanClick,
                    shape = MaterialTheme.shapes.large
                ) {
                    Text("Добавить пропуск")
                }
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String) {
    Surface(
        color = Color.White.copy(alpha = 0.16f),
        contentColor = Color.White,
        shape = MaterialTheme.shapes.large,
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = Color(0xFFD9E6FF))
        }
    }
}

@Composable
private fun SectionHeader(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF172033))
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF607086))
    }
}

@Composable
private fun LoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp), strokeWidth = 3.dp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "Готовим пропуска доступа", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun EmptyPassesCard(onScanClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "LocKey",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Пропусков пока нет",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Открой ссылку LocKey или вставь ее вручную, чтобы добавить первый пропуск. Здесь появятся бронирования и доверенные замки.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onScanClick, shape = MaterialTheme.shapes.large) {
                Text("Добавить пропуск")
            }
        }
    }
}

@Composable
private fun PassCard(
    pass: AccessPass,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onTogglePinned: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFFFFF)),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = Color(0xFFD8E2F0),
                    shape = MaterialTheme.shapes.extraLarge
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(PassIconBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "K", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pass.lockId,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Действует до ${pass.validUntil.shortLabel()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF607086),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = if (pass.isPinned) Color(0xFFDCE7FF) else Color(0xFFE9EEF7),
                    contentColor = if (pass.isPinned) Color(0xFF0D3475) else Color(0xFF4C5968),
                    shape = CircleShape,
                    modifier = Modifier.clickable(onClick = onTogglePinned)
                ) {
                    Text(
                        text = if (pass.isPinned) "Закреплен" else "Закрепить",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Окно бронирования", style = MaterialTheme.typography.labelMedium, color = Color(0xFF607086))
                    Text(
                        text = "${pass.bookingStartsAt.shortLabel()} - ${pass.bookingEndsAt.shortLabel()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                TextButton(onClick = onDeleteClick) {
                    Text("Удалить", color = Color(0xFF2E6BFF))
                }
            }
        }
    }
}

private fun Instant.shortLabel(): String = toString()
    .replace('T', ' ')
    .removeSuffix("Z")
    .take(16)

private val PremiumBackgroundBrush = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF4F8FF),
        Color(0xFFEAF2FF),
        Color(0xFFF9FBFF)
    )
)

private val HeroBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF1D4ED8),
        Color(0xFF2563EB),
        Color(0xFF5E8CFF)
    )
)

private val PassIconBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFF4F7DFF),
        Color(0xFF173B8F)
    )
)
