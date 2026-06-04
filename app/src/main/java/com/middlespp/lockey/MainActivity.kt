package com.middlespp.lockey

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.middlespp.lockey.core.navigation.NavHost
import com.middlespp.lockey.core.navigation.Screen
import com.middlespp.lockey.core.ui.theme.LockeyTheme
import com.middlespp.lockey.feature.passes.domain.model.ImportPassResult
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appGraph = (application as LockeyApp).appGraph

        enableEdgeToEdge()
        setContent {
            LockeyTheme {
                NavHost(
                    navigator = appGraph.navigator,
                    getPasses = appGraph.getPassesUseCase,
                    getPass = appGraph.getPassUseCase,
                    deletePass = appGraph.deletePassUseCase,
                    setPassPinned = appGraph.setPassPinnedUseCase,
                    updatePassOrder = appGraph.updatePassOrderUseCase,
                    importPass = appGraph.importPassUseCase,
                    openLock = appGraph.openLockUseCase,
                    lockQrParser = appGraph.lockQrParser
                )
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val link = intent.dataString ?: return
        val appGraph = (application as LockeyApp).appGraph

        lifecycleScope.launch {
            when (val result = appGraph.importPassUseCase(link)) {
                is ImportPassResult.Saved -> appGraph.navigator.navigate(Screen.PassDetails(result.pass.lockId))
                ImportPassResult.InvalidLink -> Unit
            }
        }
    }
}
