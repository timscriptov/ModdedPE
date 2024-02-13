import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import composition.ChooseFileButton
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val snackBarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        var inputPath by remember { mutableStateOf("") }
        var originalImgPath by remember { mutableStateOf("") }
        var newImgPath by remember { mutableStateOf("") }

        var showProgressDialog by remember { mutableStateOf(true) }
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            modifier = Modifier,
                            text = "Patcher",
                            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        )
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            if (
                                inputPath.isNotEmpty() &&
                                originalImgPath.isNotEmpty() &&
                                newImgPath.isNotEmpty() &&
                                File(inputPath).exists()
                            ) {
                                showProgressDialog = true
                                val originalBytes = File(originalImgPath).readBytes()
                                val newBytes = File(newImgPath).readBytes()
                                if (originalBytes.size >= newBytes.size) {
                                    patchingMinecraftLib(
                                        inputPath = inputPath,
                                        originalBytes = originalBytes,
                                        newBytes = newBytes,
                                    )
                                } else {
                                    scope.launch {
                                        snackBarHostState.showSnackbar("The size of the new logo should not exceed the size of the original!")
                                    }
                                }
                                showProgressDialog = false
                            }
                        }
                    },
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ) {
                    Icon(painter = painterResource("ic_run.xml"), null)
                }
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState) { data ->
                    Snackbar(
                        snackbarData = data,
                        actionOnNewLine = true,
                    )
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (showProgressDialog) {
                        Dialog(
                            onDismissRequest = {
                                showProgressDialog = false
                            }, content = {
                                Scaffold {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        CircularProgressIndicator()
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(text = "Please wait...")
                                    }
                                }
                            }
                        )
                    }
                    Text(text = "Select libminecraftpe.so")
                    if (newImgPath.isNotEmpty()) {
                        Text(text = newImgPath)
                    }
                    ChooseFileButton(
                        description = "Select libminecraftpe.so",
                        onResult = {
                            inputPath = it
                        }
                    )
                    Text(text = "Select original logo")
                    IconPreview(originalImgPath)
                    ChooseFileButton(
                        description = "Select original logo",
                        onResult = {
                            originalImgPath = it
                        }
                    )
                    Text(text = "Select new logo")
                    IconPreview(newImgPath)
                    ChooseFileButton(
                        description = "Select new logo",
                        onResult = {
                            newImgPath = it
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun IconPreview(originalImgPath: String) {
    val file = File(originalImgPath)
    if (file.exists()) {
        val painterResource = asyncPainterResource(data = file)
        KamelImage(
            resource = painterResource,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
        )
    }
}

private fun patchingMinecraftLib(inputPath: String, originalBytes: ByteArray, newBytes: ByteArray) {
    val inputFile = File(inputPath)
    val libraryBytes = inputFile.readBytes()

    val indexOfOrigImg = hexIndexOf(libraryBytes, originalBytes)
    if (indexOfOrigImg == -1) {
        println("bytes not found in libminecraftpe.so")
        return
    }

    val libraryPatchedBytes = libraryBytes.copyOf()
    System.arraycopy(newBytes, 0, libraryPatchedBytes, indexOfOrigImg, newBytes.size)

    writeToFile(inputFile, libraryPatchedBytes)
}

private fun hexIndexOf(source: ByteArray, target: ByteArray): Int {
    outer@ for (i in 0..source.size - target.size) {
        for (j in target.indices) {
            if (source[i + j] != target[j]) {
                continue@outer
            }
        }
        return i
    }
    return -1
}

fun writeToFile(file: File, content: ByteArray) = file.writeBytes(content)

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
