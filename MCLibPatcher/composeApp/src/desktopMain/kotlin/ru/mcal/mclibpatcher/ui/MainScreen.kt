package ru.mcal.mclibpatcher.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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

                        }

                        else -> {
                            OutlinedTextField(
                                value = screenState.libPath,
                                label = {
                                    Text(text = "Enter path to libminecraftpe.so")
                                },
                                onValueChange = viewModel::setLibPath
                            )
                            OutlinedTextField(
                                value = screenState.logoPath,
                                label = {
                                    Text(text = "Enter path to new logo")
                                },
                                onValueChange = viewModel::setLogoPath
                            )
                            OutlinedButton(
                                onClick = {
                                    val libPath = screenState.libPath
                                    val logoPath = screenState.logoPath
                                    val logoFile = File(logoPath)
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
                                    } else if (!logoFile.exists()) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("$logoPath not exists")
                                        }
                                    } else if (!viewModel.isValidLogoSize(logoFile)) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Reduce the quality of the logo")
                                        }
                                    } else {
                                        viewModel.patch(screenState.libPath, screenState.logoPath)
                                    }
                                }
                            ) {
                                Text(text = "Patch")
                            }
                        }
                    }
                }
            }
        )
    }
}
