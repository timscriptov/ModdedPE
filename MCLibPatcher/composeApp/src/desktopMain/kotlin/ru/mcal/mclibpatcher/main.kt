package ru.mcal.mclibpatcher

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.core.context.startKoin
import ru.mcal.mclibpatcher.di.AppModules
import ru.mcal.mclibpatcher.navigation.mainScreenModule
import ru.mcal.mclibpatcher.ui.MainScreen

fun main() = application {
    startKoin {
        val featureModules = listOf(
            AppModules.modules,
        ).flatten()
        modules(featureModules)
    }
    ScreenRegistry {
        mainScreenModule()
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "MCLibPatcher",
    ) {
        MaterialTheme {
            val screen = MainScreen()
            Navigator(screen) { nav ->
                SlideTransition(nav)
            }
        }
    }
}
