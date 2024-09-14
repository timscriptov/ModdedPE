package com.mcal.moddedpe.navigation

import cafe.adriel.voyager.core.registry.screenModule
import com.mcal.moddedpe.ui.LauncherScreen

val launcherScreenModule = screenModule {
    register<Screens.Launcher> {
        LauncherScreen()
    }
}
