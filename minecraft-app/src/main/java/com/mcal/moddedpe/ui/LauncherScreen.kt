package com.mcal.moddedpe.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import com.mcal.moddedpe.R
import com.mcal.moddedpe.composition.ProgressBar

class LauncherScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<LauncherViewModel>()
        val screenState by viewModel.screenState.collectAsState()

        val activity = LocalContext.current as Activity

        LaunchedEffect(screenState.isOnline) {
            viewModel.fetchData(activity)
        }

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
                    screenState.isError -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                modifier = Modifier.size(54.dp),
                                painter = painterResource(R.drawable.ic_warning),
                                contentDescription = null,
                                tint = Color(0xFFF44336)
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                text = "An error occurred. Please try again.",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                )
                            )
                            OutlinedButton(
                                onClick = {
                                    viewModel.fetchData(activity)
                                }
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    text = "Retry",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                    )
                                )
                            }
                        }
                    }

                    !screenState.isOnline -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                modifier = Modifier.size(54.dp),
                                painter = painterResource(R.drawable.ic_not_connect),
                                tint = Color(0xFFF44336),
                                contentDescription = null,
                            )
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                text = "Internet connection is unavailable. Please try again.",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center,
                                    color = Color.White,
                                )
                            )
                            OutlinedButton(
                                onClick = {
                                    viewModel.fetchData(activity)
                                }
                            ) {
                                Text(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    text = "Retry",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        textAlign = TextAlign.Center,
                                        color = Color.White,
                                    )
                                )
                            }
                        }
                    }

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
