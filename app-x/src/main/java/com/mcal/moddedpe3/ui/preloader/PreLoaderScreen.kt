/*
 * Copyright (C) 2018-2025 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.moddedpe3.ui.preloader

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mcal.moddedpe3.data.model.FailedNMod
import com.mcal.moddedpe3.data.model.PreLoaderContentType
import com.mcal.moddedpe3.data.model.PreLoaderScreenState
import kotlinx.coroutines.launch

class PreLoaderScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<PreLoaderViewModel>()
        val state by viewModel.state.collectAsState()
        val scope = rememberCoroutineScope()
        val listState = rememberLazyListState()
        val activity = LocalActivity.current

        LaunchedEffect(Unit) {
            if (activity != null) {
                viewModel.initializePreLoader(activity)
            }
        }

        LaunchedEffect(state.logs.size) {
            if (state.contentType == PreLoaderContentType.LOADING) {
                scope.launch {
                    listState.animateScrollToItem(state.logs.size)
                }
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (state.contentType) {
                    PreLoaderContentType.LOADING -> LoadingContent(
                        state = state,
                        listState = listState,
                    )

                    PreLoaderContentType.APP_ERROR -> AppErrorContent(
                        errorMessage = state.errorMessage,
                        onHomeClicked = { navigator.pop() }
                    )

                    PreLoaderContentType.NMOD_ERROR -> NModErrorContent(
                        failedNMods = state.failedNMods,
                        onHomeClicked = { navigator.pop() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent(
    state: PreLoaderScreenState,
    listState: LazyListState,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(state.logs) { log ->
            TerminalLine(
                text = log,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AppErrorContent(
    errorMessage: String,
    onHomeClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ошибка приложения",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(onClick = onHomeClicked) {
            Text("Вернуться на главный экран")
        }
    }
}

@Composable
private fun NModErrorContent(
    failedNMods: List<FailedNMod>,
    onHomeClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ошибка загрузки модов",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            items(failedNMods) { nMod ->
                NModErrorItem(nmod = nMod)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onHomeClicked) {
            Text("Вернуться на главный экран")
        }
    }
}

@Composable
private fun NModErrorItem(
    nmod: FailedNMod,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val iconBitmap = remember(nmod.packageName) {
            runCatching {
                val iconPath = nmod.icon
                if (iconPath?.exists() == true) {
                    android.graphics.BitmapFactory.decodeFile(iconPath.absolutePath)
                } else {
                    null
                }
            }.getOrNull()
        }

        if (iconBitmap != null) {
            Image(
                bitmap = iconBitmap.asImageBitmap(),
                contentDescription = "Иконка мода ${nmod.name}",
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 8.dp)
            )
        }

        Text(
            text = nmod.name,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Пакет: ${nmod.packageName}",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "Ошибка: ${nmod.loadException?.message ?: "Неизвестная ошибка"}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TerminalLine(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "> $text",
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier.padding(vertical = 2.dp)
    )
}