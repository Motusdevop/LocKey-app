package com.middlespp.lockey.core.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.GetPassesUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.ImportPassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.OpenLockUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.DeletePassUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.SetPassPinnedUseCase
import com.middlespp.lockey.feature.passes.domain.usecase.UpdatePassOrderUseCase
import com.middlespp.lockey.feature.passes.presentation.PassesScreen
import com.middlespp.lockey.feature.passes.presentation.PassesViewModel
import com.middlespp.lockey.feature.passes.presentation.details.PassDetailsScreen
import com.middlespp.lockey.feature.passes.presentation.details.PassDetailsViewModel
import com.middlespp.lockey.feature.scanner.domain.parse.LockQrParser
import com.middlespp.lockey.feature.scanner.presentation.AddPassScreen
import com.middlespp.lockey.feature.scanner.presentation.AddPassUiEvent
import com.middlespp.lockey.feature.scanner.presentation.AddPassViewModel
import com.middlespp.lockey.feature.scanner.presentation.OpenLockScreen
import com.middlespp.lockey.feature.scanner.presentation.OpenLockViewModel

@Composable
fun NavHost(
    navigator: Navigator,
    getPasses: GetPassesUseCase,
    getPass: GetPassUseCase,
    deletePass: DeletePassUseCase,
    setPassPinned: SetPassPinnedUseCase,
    updatePassOrder: UpdatePassOrderUseCase,
    importPass: ImportPassUseCase,
    openLock: OpenLockUseCase,
    lockQrParser: LockQrParser,
    modifier: Modifier = Modifier
) {
    val backStack by navigator.backStack.collectAsState()

    BackHandler(enabled = backStack.size > 1) {
        navigator.pop()
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier.fillMaxSize(),
        onBack = { navigator.pop() },
        entryProvider = { screen ->
            NavEntry(screen) {
                when (screen) {
                    is Screen.Passes -> {
                        val viewModel = viewModel<PassesViewModel>(
                            factory = PassesViewModel.factory(
                                getPasses = getPasses,
                                deletePass = deletePass,
                                setPassPinned = setPassPinned,
                                updatePassOrder = updatePassOrder,
                                importPass = importPass
                            )
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        PassesScreen(
                            state = state,
                            events = viewModel.uiEvents,
                            onPassClick = { lockId -> navigator.navigate(Screen.PassDetails(lockId)) },
                            onScanClick = { navigator.navigate(Screen.AddPass) },
                            onDeletePass = viewModel::delete,
                            onUndoDelete = viewModel::undoDelete,
                            onTogglePinned = viewModel::togglePinned,
                            onMovePass = viewModel::move
                        )
                    }

                    is Screen.PassDetails -> {
                        val viewModel = viewModel<PassDetailsViewModel>(
                            key = screen.lockId,
                            factory = PassDetailsViewModel.factory(
                                lockId = screen.lockId,
                                getPass = getPass
                            )
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        PassDetailsScreen(
                            state = state,
                            onScanClick = { lockId -> navigator.navigate(Screen.OpenLock(lockId)) },
                            onBackClick = { navigator.pop() }
                        )
                    }

                    Screen.AddPass -> {
                        val viewModel = viewModel<AddPassViewModel>(
                            factory = AddPassViewModel.factory(
                                importPass = importPass
                            )
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        LaunchedEffect(viewModel) {
                            viewModel.events.collect { event ->
                                when (event) {
                                    AddPassUiEvent.PassAdded -> navigator.replaceAll(Screen.Passes)
                                }
                            }
                        }

                        AddPassScreen(
                            state = state,
                            onLinkChange = viewModel::onLinkChange,
                            onSubmitClick = viewModel::submit,
                            onBackClick = { navigator.pop() }
                        )
                    }

                    is Screen.OpenLock -> {
                        val viewModel = viewModel<OpenLockViewModel>(
                            key = "open-lock:${screen.lockId}",
                            factory = OpenLockViewModel.factory(
                                lockId = screen.lockId,
                                getPass = getPass,
                                openLock = openLock,
                                qrParser = lockQrParser
                            )
                        )
                        val state by viewModel.state.collectAsStateWithLifecycle()

                        OpenLockScreen(
                            state = state,
                            onCodeChange = viewModel::onCodeChange,
                            onScannedQr = viewModel::onScannedQr,
                            onSubmitCodeClick = viewModel::submitManualCode,
                            onBackClick = { navigator.pop() }
                        )
                    }
                }
            }
        }
    )
}
