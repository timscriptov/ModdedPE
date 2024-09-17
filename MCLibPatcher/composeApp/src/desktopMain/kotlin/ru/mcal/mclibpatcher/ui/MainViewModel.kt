package ru.mcal.mclibpatcher.ui

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.mcal.mclibpatcher.data.model.MainScreenState
import ru.mcal.mclibpatcher.data.repositories.MainRepository
import java.io.File

class MainViewModel(
    private val mainRepository: MainRepository
) : ScreenModel {
    private val _exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _screenState.update {
            it.copy(
                isPatching = false,
                errorMessage = throwable.toString()
            )
        }
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

    fun setOriginalLogoPath(path: String) {
        _screenState.update {
            it.copy(
                originalLogoPath = path
            )
        }
    }

    fun setLogoPath(path: String) {
        _screenState.update {
            it.copy(
                newLogoPath = path
            )
        }
    }

    fun chooseFile(
        buttonText: String,
        description: String,
        baseDirectory: String = "/",
        onResult: (path: String) -> Unit,
    ) {
        mainRepository.chooseFile(buttonText, description, baseDirectory, onResult)
    }

    fun chooseDirectory(
        buttonText: String,
        description: String,
        baseDirectory: String = "/",
        onResult: (path: String) -> Unit,
    ) {
        mainRepository.chooseDirectory(buttonText, description, baseDirectory, onResult)
    }

    fun patch(libraryPath: String, originalLogo: String, newLogo: String) = screenModelScope.launch(_exceptionHandler) {
        _screenState.update {
            it.copy(
                isPatching = true
            )
        }
        mainRepository.patchingMinecraftLib(libraryPath, File(originalLogo).readBytes(), File(newLogo).readBytes())
        _screenState.update {
            it.copy(
                isPatching = false
            )
        }
    }
}
