package ru.mcal.mclibpatcher.ui

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.mcal.mclibpatcher.data.model.MainScreenState
import ru.mcal.mclibpatcher.data.repositories.MainRepository
import java.io.File
import java.io.InputStream

class MainViewModel(
    private val mainRepository: MainRepository
) : ScreenModel {
    private val _exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private val _screenState = MutableStateFlow(MainScreenState())
    val screenState = _screenState.asStateFlow()

    fun setLibPath(path: String) {
        _screenState.update {
            it.copy(
                libPath = path
            )
        }
    }

    fun setLogoPath(path: String) {
        _screenState.update {
            it.copy(
                logoPath = path
            )
        }
    }

    fun isValidLogoSize(file: File): Boolean {
        val fileSize = file.length()
        val inputStreamSize = mainRepository.originalLogoInputStream().use { it.readBytes().size.toLong() }
        return fileSize <= inputStreamSize
    }

    fun patch(libraryPath: String, newLogo: String) {
        _screenState.update {
            it.copy(
                isPatching = true
            )
        }
        mainRepository.patchingMinecraftLib(libraryPath, File(newLogo).readBytes())
        _screenState.update {
            it.copy(
                isPatching = false
            )
        }
    }
}
