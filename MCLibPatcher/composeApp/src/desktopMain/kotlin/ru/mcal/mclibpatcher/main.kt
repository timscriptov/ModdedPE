package ru.mcal.mclibpatcher

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.core.context.startKoin
import ru.mcal.mclibpatcher.di.AppModules
import ru.mcal.mclibpatcher.navigation.mainScreenModule
import ru.mcal.mclibpatcher.ui.MainScreen
import java.util.*

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
    val iconPath = when {
        System.getProperty("os.name").lowercase(Locale.getDefault()).contains("win") -> "drawable/windows.ico"
        System.getProperty("os.name").lowercase(Locale.getDefault()).contains("mac") -> "drawable/macos.icns"
        System.getProperty("os.name").lowercase(Locale.getDefault()).contains("nix") ||
                System.getProperty("os.name").lowercase(Locale.getDefault()).contains("nux") -> "drawable/linux.png"

        else -> "drawable/linux.png"
    }
    Window(
        onCloseRequest = ::exitApplication,
        title = "MCLibPatcher",
        icon = painterResource(iconPath)
    ) {
        MaterialTheme {
            val screen = MainScreen()
            Navigator(screen) { nav ->
                SlideTransition(nav)
            }
        }
    }
}
