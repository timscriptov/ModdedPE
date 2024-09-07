package ru.mcal.mclibpatcher.navigation

import cafe.adriel.voyager.core.registry.screenModule
import ru.mcal.mclibpatcher.ui.MainScreen

val mainScreenModule = screenModule {
    register<Screens.Main> {
        MainScreen()
    }
}
