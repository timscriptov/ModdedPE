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
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import ru.mcal.mclibpatcher.data.model.MainScreenState
import java.io.File

class MainScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<MainViewModel>()
        val screenState by viewModel.screenState.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            modifier = Modifier.statusBarsPadding(),
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
                            PatchingContent()
                        }

                        else -> {
                            if (screenState.errorMessage.isNotEmpty()) {
                                ErrorContent(screenState.errorMessage)
                            }
                            SelectorContent(
                                screenState = screenState,
                                onClickOriginal = {
                                    viewModel.chooseFile(
                                        buttonText = "Select",
                                        description = "Select original logo(PNG file 87129 byte)",
                                        onResult = { path ->
                                            path.takeIf { it.isNotEmpty() && File(it).exists() }?.let {
                                                viewModel.setOriginalLogoPath(it)
                                            }
                                        }
                                    )
                                },
                                onClickNew = {
                                    viewModel.chooseFile(
                                        buttonText = "Select",
                                        description = "Select new logo(PNG file 87129 byte)",
                                        onResult = { path ->
                                            path.takeIf { it.isNotEmpty() && File(it).exists() }?.let {
                                                viewModel.setLogoPath(it)
                                            }
                                        }
                                    )
                                },
                                onClickLib = {
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
                            )
                            if (screenState.libPath.isNotEmpty() && screenState.newLogoPath.isNotEmpty()) {
                                PatchButtonContent(
                                    text = "Patch",
                                    onClick = {
                                        val validation = screenState.validate()
                                        if (validation.isNotEmpty()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(validation)
                                            }
                                        } else {
                                            viewModel.patch(
                                                screenState.libPath,
                                                screenState.originalLogoPath,
                                                screenState.newLogoPath
                                            )
                                        }
                                    }
                                )
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

    @Composable
    private fun PatchButtonContent(
        text: String,
        onClick: () -> Unit
    ) {
        OutlinedButton(
            modifier = Modifier.padding(vertical = 16.dp),
            onClick = { onClick() }
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = text,
                style = TextStyle(
                    fontSize = 16.sp
                )
            )
        }
    }

    @Composable
    private fun SelectorContent(
        screenState: MainScreenState,
        onClickOriginal: () -> Unit,
        onClickNew: () -> Unit,
        onClickLib: () -> Unit,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            CardContent(
                path = screenState.originalLogoPath,
                description = "Select original logo\nPNG file 87129 byte",
                isImage = true,
                onClick = onClickOriginal,
            )
            Spacer(modifier = Modifier.width(16.dp))
            CardContent(
                path = screenState.newLogoPath,
                description = "Select new logo\nPNG file 87129 byte",
                isImage = true,
                onClick = onClickNew,
            )
            Spacer(modifier = Modifier.width(16.dp))
            CardContent(
                path = screenState.libPath,
                description = "Select libminecraftpe.so",
                isImage = false,
                onClick = onClickLib,
            )
        }
    }

    @Composable
    private fun CardContent(
        path: String,
        description: String,
        isImage: Boolean,
        onClick: () -> Unit,
    ) {
        Card(
            modifier = Modifier
                .size(200.dp)
                .clickable { onClick() }
        ) {
            if (path.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    if (isImage) {
                        val painter = rememberAsyncImagePainter(path)
                        Image(
                            modifier = Modifier.align(Alignment.Center),
                            painter = painter,
                            contentDescription = null,
                            contentScale = ContentScale.Inside,
                        )
                    }
                    Text(
                        modifier = Modifier.align(Alignment.BottomCenter),
                        text = path,
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
                        text = description,
                        style = TextStyle(
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun ErrorContent(errorMessage: String) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            text = errorMessage,
            style = TextStyle(
                color = Color.Red,
                textAlign = TextAlign.Center,
            )
        )
    }

    @Composable
    private fun PatchingContent() {
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
}
