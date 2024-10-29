package com.intecular.invis.home.adddevice

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.home.analyzer.QrCodeAnalyzer
import com.intecular.invis.ui.view.CommonChildTopAppBar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@ExperimentalMaterial3Api
@Composable
fun AddDeviceScreen(
    navHostController: NavHostController
) {
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold(topBar = {
        CommonChildTopAppBar(stringId = R.string.add_device) {
            navHostController.popBackStack()
        }
    },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        val context = LocalContext.current
        val startCamera = remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            )
        }


        RequestCameraPermission(context, startCamera, navHostController)
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(id = R.string.scan_matter_qr_code),
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.inverseSurface
            )

            Text(
                text = stringResource(id = R.string.make_sure_your_accessory_is_powered),
                modifier = Modifier.padding(20.dp),
                color = MaterialTheme.colorScheme.inverseSurface
            )
            if (startCamera.value) {
                CameraPreviewScreen(context)
            }
        }
    }
}

@Composable
fun RequestCameraPermission(
    context: Context,
    startCamera: MutableState<Boolean>,
    navHostController: NavHostController
) {
    val deniedPermissionResult = remember {
        mutableStateOf(
            false
        )
    }
    val showReminder = remember {
        mutableStateOf(false)
    }
    val cameraPermissionLaunch =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startCamera.value = true
            } else {
                deniedPermissionResult.value = true
            }
        }
    val toAppSystemSetting = remember {
        mutableStateOf(false)
    }

    val toAppSystemLaunch =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                requestCameraPermission(cameraPermissionLaunch)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (deniedPermissionResult.value) {
        deniedPermissionResult.value = false
        handlePermissionResult(context, showReminder, cameraPermissionLaunch)
    }
    if (toAppSystemSetting.value) {
        toAppSystemSetting.value = false
        toAppSystemLaunch.launch(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )
        )
    }

    if (showReminder.value) {
        AlertDialog(
            onDismissRequest = { showReminder.value = false },
            confirmButton = {
                Text(
                    text = stringResource(id = R.string.turn_on), modifier = Modifier.clickable {
                        showReminder.value = false
                        toAppSystemSetting.value = true
                    },
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            },
            dismissButton = {
                Text(
                    text = stringResource(id = R.string.cancel), modifier = Modifier.clickable {
                        showReminder.value = false
                        navHostController.popBackStack()
                    },
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.otherwise_this_function_cannot_be_used),
                    color = MaterialTheme.colorScheme.inverseSurface
                )
            }
        )
    }
}

@Composable
fun CameraPreviewScreen(context: Context) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            bindToLifecycle(lifecycleOwner)
            cameraSelector =
                CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()
            setEnabledUseCases(CameraController.IMAGE_ANALYSIS)
            imageAnalysisBackpressureStrategy = ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
        }
    }

    val previewView = remember {
        PreviewView(context).apply {
            controller = cameraController
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    DisposableEffect(cameraController) {
        onDispose {
            cameraController.unbind()
        }
    }
    val flashState = remember { mutableStateOf(false) }
    val analyzedContent = remember {
        mutableStateOf("")
    }
    Box(modifier = Modifier.padding(40.dp, 60.dp)) {
        AndroidView(
            modifier = Modifier
                .clip(RoundedCornerShape(15.dp))
                .fillMaxSize(),
            factory = { previewView },
            onRelease = {
                cameraExecutor.shutdown()
            },
        )
        IconToggleButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            checked = flashState.value,
            onCheckedChange = { flashState.value = it }) {
            Icon(
                painter = painterResource(id = if (flashState.value) R.drawable.ic_turn_on else R.drawable.ic_turn_off),
                contentDescription = ""
            )
        }

        if (analyzedContent.value.isNotEmpty()) {
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(),
                painter = painterResource(id = R.drawable.sharp_check_24),
                contentDescription = "",
                tint = Color.White
            )
        }
    }

    handleCameraAction(cameraController, cameraExecutor, analyzedContent, flashState)
}

fun handleCameraAction(
    cameraController: CameraController,
    cameraExecutor: ExecutorService,
    analyzedString: MutableState<String>,
    flashState: MutableState<Boolean>
) {
    cameraController.setImageAnalysisAnalyzer(cameraExecutor, QrCodeAnalyzer {
        analyzedString.value = it
    })
    cameraController.enableTorch(flashState.value)
}


fun handlePermissionResult(
    context: Context,
    showReminder: MutableState<Boolean>,
    cameraPermissionLaunch: ManagedActivityResultLauncher<String, Boolean>
) {
    if (!ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.CAMERA
        )
    ) {
        showReminder.value = true
    } else {
        requestCameraPermission(cameraPermissionLaunch)
    }
}


fun requestCameraPermission(
    requestCameraPermissionLaunch: ManagedActivityResultLauncher<String, Boolean>,
) {
    requestCameraPermissionLaunch.launch(Manifest.permission.CAMERA)

}

