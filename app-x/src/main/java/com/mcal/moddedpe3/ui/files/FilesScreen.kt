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
package com.mcal.moddedpe3.ui.files

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mcal.moddedpe3.composition.DeleteDialog
import com.mcal.moddedpe3.data.model.FileItem
import com.mcal.moddedpe3.ui.editor.TextEditorScreen
import okio.IOException

class FilesScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val filesDir = context.filesDir.parentFile ?: throw IOException("filesDir not found")
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<FilesViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(Unit) {
            if (state.currentPath.isEmpty()) {
                viewModel.loadFiles(filesDir.absolutePath)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("File Manager") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CurrentPathCard(
                    currentPath = state.currentPath,
                    rootPath = filesDir.absolutePath,
                    onBackClick = {
                        viewModel.navigateToParent(state.currentPath, filesDir.absolutePath)
                    }
                )

                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                    ) {
                        items(state.files) { file ->
                            FileItemView(
                                file = file,
                                onFileClick = {
                                    if (file.isDirectory) {
                                        viewModel.loadFiles(file.file.absolutePath)
                                    } else if (file.isTextFile()) {
                                        navigator.push(TextEditorScreen(file = file.file))
                                    }
                                },
                                onShareClick = {
                                    viewModel.shareFile(file)
                                },
                                onDeleteClick = {
                                    viewModel.showDeleteDialog(file)
                                },
                            )
                        }
                    }
                }
            }
        }

        if (state.showDeleteDialog && state.fileToDelete != null) {
            DeleteDialog(
                title = "Delete File",
                text = "Are you sure you want to delete \"${state.fileToDelete!!.name}\"? This action cannot be undone.",
                onDismiss = { viewModel.hideDeleteDialog() },
                onDelete = {
                    viewModel.deleteFile(state.fileToDelete!!)
                }
            )
        }
    }
}

@Composable
fun CurrentPathCard(
    currentPath: String,
    rootPath: String,
    onBackClick: () -> Unit
) {
    val canGoBack = currentPath != rootPath

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currentPath,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (canGoBack) {
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowUpward,
                        contentDescription = "Go to parent directory",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun FileItemView(
    file: FileItem,
    onFileClick: () -> Unit,
    onShareClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onFileClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                file.icon()
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = file.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (file.size.isNotEmpty()) {
                        Text(
                            text = file.size,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Row {
                if (!file.isDirectory) {
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
