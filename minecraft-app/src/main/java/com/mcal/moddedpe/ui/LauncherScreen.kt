package com.mcal.moddedpe.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mcal.moddedpe.composition.ProgressBar

class LauncherScreen : Screen {
    override val key: ScreenKey
        get() = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<LauncherViewModel>()
        val screenState by viewModel.screenState.collectAsState()

        val activity = LocalContext.current as Activity

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Black,
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when {
                    screenState.isLoading -> {
                        ProgressBar(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(54.dp)
                        )
                    }

                    else -> {
                        LaunchedEffect(Unit) {
                            viewModel.startGame(activity)
                        }
                    }
                }
            }
        }
    }
}
