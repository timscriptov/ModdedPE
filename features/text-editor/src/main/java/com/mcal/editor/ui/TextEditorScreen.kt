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
package com.mcal.editor.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Redo
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mcal.editor.composition.ErrorDialog
import com.mcal.editor.composition.SaveDialog
import java.io.File

class TextEditorScreen(
    private val file: File,
) : Screen {

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
        ExperimentalComposeUiApi::class
    )
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<TextEditorViewModel>()
        val state by viewModel.state.collectAsState()

        LaunchedEffect(file) {
            viewModel.loadFileContent(file)
        }

        LaunchedEffect(state.isSaved) {
            if (state.isSaved) {
                navigator.pop()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (state.isModified) "${file.name} *" else file.name,
                            maxLines = 1
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (state.isModified) {
                                viewModel.showSaveDialog()
                            } else {
                                navigator.pop()
                            }
                        }) {
                            Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.undo() },
                            enabled = state.canUndo && !state.isLoading
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Undo, contentDescription = "Undo")
                        }

                        IconButton(
                            onClick = { viewModel.redo() },
                            enabled = state.canRedo && !state.isLoading
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.Redo, contentDescription = "Redo")
                        }

                        IconButton(
                            onClick = {
                                viewModel.saveFile(file)
                            },
                            enabled = state.isModified && !state.isLoading
                        ) {
                            Icon(Icons.Rounded.Save, contentDescription = "Save")
                        }
                    }
                )
            },
        ) { paddingValues ->
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                ) {
                    BasicTextField(
                        value = state.content,
                        onValueChange = { newContent ->
                            viewModel.updateContent(newContent)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    )
                }
            }
        }

        if (state.showSaveDialog) {
            SaveDialog(
                fileName = file.name,
                onDismiss = { viewModel.hideSaveDialog() },
                onSave = {
                    viewModel.saveFile(file)
                    viewModel.hideSaveDialog()
                },
                onDontSave = {
                    viewModel.hideSaveDialog()
                    navigator.pop()
                }
            )
        }

        if (state.error != null) {
            ErrorDialog(
                error = state.error!!,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}
