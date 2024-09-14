package com.mcal.moddedpe.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class Screens : ScreenProvider {
    data object Launcher : Screens()
}
