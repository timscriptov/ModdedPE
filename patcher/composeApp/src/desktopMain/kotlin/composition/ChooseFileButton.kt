package composition

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import javax.swing.JFileChooser

@Composable
fun ChooseFileButton(
    description: String,
    onResult: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        border = BorderStroke(1.dp, Color.Gray),
        colors = ButtonDefaults.filledTonalButtonColors(),
        shape = ShapeDefaults.Small,
        onClick = {
            chooseFile(description)?.let { path ->
                onResult(path)
            }
        },
        contentPadding = PaddingValues(
            horizontal = 10.dp,
            vertical = 6.dp
        )
    ) {
        Text(
            text = "Select",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight(500),
            )
        )
    }
}

private fun chooseFile(description: String): String? {
    val fileChooser = JFileChooser().apply {
        fileSelectionMode = JFileChooser.FILES_ONLY
        dialogTitle = description
        approveButtonText = "Select"
        approveButtonToolTipText = description
    }
    fileChooser.showOpenDialog(null)
    val result = fileChooser.selectedFile
    return if (result != null && result.exists()) {
        result.absolutePath.toString()
    } else {
        null
    }
}
