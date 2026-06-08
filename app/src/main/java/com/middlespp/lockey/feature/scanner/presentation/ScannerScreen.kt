package com.middlespp.lockey.feature.scanner.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun AddPassScreen(
    state: ScannerUiState,
    onLinkChange: (String) -> Unit,
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
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                "Добавить пропуск",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Вставь ссылку пропуска",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = state.code,
                        onValueChange = onLinkChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Ссылка пропуска") },
                        placeholder = { Text("lockey://open?lock_id=...") },
                        minLines = 4
                    )
                    Button(
                        onClick = onSubmitClick,
                        enabled = !state.isBusy,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (state.isBusy) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Добавить пропуск")
                    }
                }
            }
        }
    }
}

@Composable
fun OpenLockScreen(
    state: ScannerUiState,
    onCodeChange: (String) -> Unit,
    onScannedQr: (String) -> Unit,
    onSubmitCodeClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var lastScannedCode by remember { mutableStateOf<String?>(null) }

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
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            Text(
                "Открыть замок",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            QrScanCard(
                title = "Сканировать QR замка",
                description = "Наведи камеру на QR-код на замке. После распознавания отправим запрос на открытие.",
                enabled = !state.isBusy,
                onQrCodeScanned = { code ->
                    if (code != lastScannedCode) {
                        lastScannedCode = code
                        onScannedQr(code)
                    }
                }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Ввести код вручную",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = state.code,
                        onValueChange = onCodeChange,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Код с замка") },
                        singleLine = true
                    )
                    Button(
                        onClick = onSubmitCodeClick,
                        enabled = !state.isBusy,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        if (state.isBusy) CircularProgressIndicator(strokeWidth = 2.dp) else Text("Открыть замок")
                    }
                }
            }
        }
    }
}

@Composable
private fun QrScanCard(
    title: String,
    description: String,
    enabled: Boolean,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            hasCameraPermission = isGranted
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (hasCameraPermission) {
                QrCameraPreview(
                    enabled = enabled,
                    onQrCodeScanned = onQrCodeScanned
                )
            } else {
                CameraPermissionCard(onRequestPermission = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                })
            }
        }
    }
}

@Composable
private fun CameraPermissionCard(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Нужен доступ к камере",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Разреши камеру, чтобы сканировать QR-коды внутри приложения.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onRequestPermission, shape = MaterialTheme.shapes.large) {
                Text("Разрешить камеру")
            }
        }
    }
}

@Composable
private fun QrCameraPreview(
    enabled: Boolean,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val isAnalyzing = remember { AtomicBoolean(false) }
    var boundCameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    val barcodeScanner = remember {
        BarcodeScanning.getClient(
            com.google.mlkit.vision.barcode.BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    LaunchedEffect(previewView, lifecycleOwner, enabled) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(
            {
                val cameraProvider = cameraProviderFuture.get()
                boundCameraProvider = cameraProvider
                val preview = Preview.Builder().build().also { preview ->
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { analysis ->
                        analysis.setAnalyzer(analysisExecutor) { imageProxy ->
                            if (enabled && isAnalyzing.compareAndSet(false, true)) {
                                imageProxy.scanQrCode(
                                    scanner = barcodeScanner,
                                    onQrCodeScanned = onQrCodeScanned,
                                    onComplete = { isAnalyzing.set(false) }
                                )
                            } else {
                                imageProxy.close()
                            }
                        }
                    }

                runCatching {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageAnalysis
                    )
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            boundCameraProvider?.unbindAll()
            barcodeScanner.close()
            analysisExecutor.shutdown()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.72f)
                .height(220.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White.copy(alpha = 0.08f))
        )
    }
}

@OptIn(ExperimentalGetImage::class)
private fun ImageProxy.scanQrCode(
    scanner: BarcodeScanner,
    onQrCodeScanned: (String) -> Unit,
    onComplete: () -> Unit
) {
    val mediaImage = image
    if (mediaImage == null) {
        onComplete()
        close()
        return
    }

    val inputImage = InputImage.fromMediaImage(mediaImage, imageInfo.rotationDegrees)
    scanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            barcodes.firstNotNullOfOrNull { barcode -> barcode.rawValue }?.let(onQrCodeScanned)
        }
        .addOnCompleteListener {
            onComplete()
            close()
        }
}

private val ScannerBackgroundBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFF7FAFF), Color(0xFFEAF1FB), Color(0xFFF8FAFF))
)
