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
package com.mcal.moddedpe3.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.mcal.moddedpe3.composition.PreferencesSection
import com.mcal.moddedpe3.composition.PreferencesSwitch
import com.mcal.moddedpe3.composition.PreferencesTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen.SettingsContent() {
    val viewModel = koinScreenModel<SettingsViewModel>()
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )

            PreferencesSection(
                title = "Game Settings",
                icon = Icons.Default.Games
            ) {
                PreferencesSwitch(
                    title = "Safe Mode",
                    description = "Disable all mods and custom content",
                    icon = Icons.Default.Security,
                    iconColor = Color(0xFF4CAF50),
                    isChecked = state.isSafeMode,
                    onCheckedChange = { viewModel.setSafeMode(it) }
                )

                PreferencesTextField(
                    title = "Minecraft Package",
                    description = "Target Minecraft package name",
                    icon = Icons.Default.Storage,
                    iconColor = Color(0xFF607D8B),
                    value = state.minecraftPackageName,
                    onValueChange = { viewModel.setMinecraftPackageName(it) },
                    placeholder = "com.mojang.minecraftpe",
                    keyboardType = KeyboardType.Text
                )

//                PreferencesSlider(
//                    title = "Render Distance",
//                    description = "Adjust view distance",
//                    icon = Icons.Default.Visibility,
//                    iconColor = Color(0xFF2196F3),
//                    value = state.renderDistance,
//                    minValue = 2,
//                    maxValue = 32,
//                    onValueChange = { viewModel.setRenderDistance(it) },
//                    valueSuffix = " chunks"
//                )
            }

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
            )
        }
    }
}
