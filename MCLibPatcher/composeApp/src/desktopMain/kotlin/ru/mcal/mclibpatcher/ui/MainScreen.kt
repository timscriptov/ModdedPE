package ru.mcal.mclibpatcher.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import java.io.File

class MainScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<MainViewModel>()
        val screenState by viewModel.screenState.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier,
                            text = "MCLibPatcher",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    when {
                        screenState.isPatching -> {
                            CircularProgressIndicator()
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                text = "Patching. Please wait...",
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }

                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Card(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clickable {
                                            viewModel.chooseFile(
                                                buttonText = "Select",
                                                description = "Select original logo(PNG file 87129 byte)",
                                                onResult = { path ->
                                                    path.takeIf { it.isNotEmpty() && File(it).exists() }?.let {
                                                        viewModel.setOriginalLogoPath(it)
                                                    }
                                                }
                                            )
                                        }
                                ) {
                                    if (screenState.originalLogoPath.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                        ) {
                                            val painter = rememberAsyncImagePainter(screenState.originalLogoPath)
                                            Image(
                                                modifier = Modifier.align(Alignment.Center),
                                                painter = painter,
                                                contentDescription = null,
                                                contentScale = ContentScale.Inside,
                                            )
                                            Text(
                                                modifier = Modifier.align(Alignment.BottomCenter),
                                                text = screenState.originalLogoPath,
                                                style = TextStyle(
                                                    color = Color.DarkGray,
                                                    textAlign = TextAlign.Center,
                                                )
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                        ) {
                                            Icon(
                                                modifier = Modifier.align(Alignment.Center),
                                                painter = painterResource("drawable/ic_select_file.png"),
                                                contentDescription = null,
                                            )
                                            Text(
                                                modifier = Modifier.align(Alignment.BottomCenter),
                                                text = "Select original logo\nPNG file 87129 byte",
                                                style = TextStyle(
                                                    color = Color.DarkGray,
                                                    textAlign = TextAlign.Center,
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Card(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clickable {
                                            viewModel.chooseFile(
                                                buttonText = "Select",
                                                description = "Select new logo(PNG file 87129 byte)",
                                                onResult = { path ->
                                                    path.takeIf { it.isNotEmpty() && File(it).exists() }?.let {
                                                        viewModel.setLogoPath(it)
                                                    }
                                                }
                                            )
                                        }
                                ) {
                                    if (screenState.logoPath.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                        ) {
                                            val painter = rememberAsyncImagePainter(screenState.logoPath)
                                            Image(
                                                modifier = Modifier.align(Alignment.Center),
                                                painter = painter,
                                                contentDescription = null,
                                                contentScale = ContentScale.Inside,
                                            )
                                            Text(
                                                modifier = Modifier.align(Alignment.BottomCenter),
                                                text = screenState.logoPath,
                                                style = TextStyle(
                                                    color = Color.DarkGray,
                                                    textAlign = TextAlign.Center,
                                                )
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                        ) {
                                            Icon(
                                                modifier = Modifier.align(Alignment.Center),
                                                painter = painterResource("drawable/ic_select_file.png"),
                                                contentDescription = null,
                                            )
                                            Text(
                                                modifier = Modifier.align(Alignment.BottomCenter),
                                                text = "Select new logo\nPNG file 87129 byte",
                                                style = TextStyle(
                                                    color = Color.DarkGray,
                                                    textAlign = TextAlign.Center,
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Card(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clickable {
                                            viewModel.chooseFile(
                                                buttonText = "Select",
                                                description = "Select new logo(PNG file 87129 byte)",
                                                onResult = { path ->
                                                    path.takeIf { it.isNotEmpty() && File(it).exists() }?.let {
                                                        viewModel.setLibPath(it)
                                                    }
                                                }
                                            )
                                        }
                                ) {
                                    if (screenState.libPath.isNotEmpty()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                        ) {
                                            Icon(
                                                modifier = Modifier.align(Alignment.Center),
                                                painter = painterResource("drawable/ic_select_file.png"),
                                                contentDescription = null,
                                            )
                                            Text(
                                                modifier = Modifier.align(Alignment.BottomCenter),
                                                text = screenState.libPath,
                                                style = TextStyle(
                                                    color = Color.DarkGray,
                                                    textAlign = TextAlign.Center,
                                                )
                                            )
                                        }
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                        ) {
                                            Icon(
                                                modifier = Modifier.align(Alignment.Center),
                                                painter = painterResource("drawable/ic_select_file.png"),
                                                contentDescription = null,
                                            )
                                            Text(
                                                modifier = Modifier.align(Alignment.BottomCenter),
                                                text = "Select libminecraftpe.so",
                                                style = TextStyle(
                                                    color = Color.DarkGray,
                                                    textAlign = TextAlign.Center,
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            if (screenState.libPath.isNotEmpty() && screenState.logoPath.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = {
                                        val libPath = screenState.libPath
                                        val originalLogoPath = screenState.originalLogoPath
                                        val logoPath = screenState.logoPath
                                        if (libPath.isEmpty()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Please enter path to libminecraftpe.so")
                                            }
                                        } else if (!File(libPath).exists()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("$libPath not exists")
                                            }
                                        } else if (logoPath.isEmpty()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Please enter path to Logo")
                                            }
                                        } else if (!File(logoPath).exists()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("$logoPath not exists")
                                            }
                                        } else if (originalLogoPath.isEmpty()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Please enter path to original Logo")
                                            }
                                        } else if (!File(originalLogoPath).exists()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("$logoPath not exists")
                                            }
                                        } else if (!viewModel.isValidLogoSize(originalLogoPath, logoPath)) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("The logo size must be less than or equal to the weight of the original logo 87129 bytes")
                                            }
                                        } else {
                                            viewModel.patch(
                                                screenState.libPath,
                                                screenState.originalLogoPath,
                                                screenState.logoPath
                                            )
                                        }
                                    }
                                ) {
                                    Text(text = "Patch")
                                }
                            }
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                text = "Copyright 2024 timscriptov",
                                style = TextStyle(
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    }
                }
            }
        )
    }
}
