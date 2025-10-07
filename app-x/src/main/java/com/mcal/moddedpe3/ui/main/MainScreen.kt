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
package com.mcal.moddedpe3.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.mcal.moddedpe3.data.model.MainTab
import com.mcal.moddedpe3.ui.home.HomeContent
import com.mcal.moddedpe3.ui.mods.ModsContent
import com.mcal.moddedpe3.ui.settings.SettingsContent

class MainScreen : Screen {
    @Composable
    override fun Content() {
        Main()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Main() {
        val viewModel = koinScreenModel<MainViewModel>()
        val screenState by viewModel.screenState.collectAsState()

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Основной контент
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    when (screenState.selectedTab) {
                        MainTab.HOME -> HomeContent()
                        MainTab.MODS -> ModsContent()
                        MainTab.SETTINGS -> SettingsContent()
                    }
                }

                // Панель навигации
                NavigationPanel(
                    selectedTab = screenState.selectedTab,
                    onTabSelected = { tab ->
                        viewModel.selectTab(tab)
                    }
                )
            }
        }
    }

    @Composable
    fun NavigationPanel(
        selectedTab: MainTab,
        onTabSelected: (MainTab) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NavigationButton(
                icon = Icons.Rounded.Home,
                label = "Главная",
                isSelected = selectedTab == MainTab.HOME,
                onClick = { onTabSelected(MainTab.HOME) }
            )
            NavigationButton(
                icon = Icons.Rounded.Extension,
                label = "Моды",
                isSelected = selectedTab == MainTab.MODS,
                onClick = { onTabSelected(MainTab.MODS) }
            )
            NavigationButton(
                icon = Icons.Rounded.Settings,
                label = "Настройки",
                isSelected = selectedTab == MainTab.SETTINGS,
                onClick = { onTabSelected(MainTab.SETTINGS) }
            )
        }
    }

    @Composable
    fun NavigationButton(
        icon: ImageVector,
        label: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        val backgroundColor = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primaryContainer
        }

        val iconColor = if (isSelected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = backgroundColor,
                        shape = CircleShape
                    ),
                onClick = onClick,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}