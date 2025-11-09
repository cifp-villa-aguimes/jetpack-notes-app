package edu.dam.notesapptyped.ui.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditNoteSheet(
    sheetState: SheetState,
    title: String,
    body: String,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        Column(
            Modifier.Companion
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Editar nota", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Título") },
                singleLine = true,
                supportingText = { Text("${title.trim().length} / 80") }
            )

            OutlinedTextField(
                value = body,
                onValueChange = onBodyChange,
                label = { Text("Contenido (opcional)") },
                minLines = 3
            )

            Row(
                Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.Companion.weight(1f)
                ) { Text("Cancelar") }

                Button(
                    onClick = onSave,
                    enabled = title.isNotBlank(),
                    modifier = Modifier.Companion.weight(1f)
                ) { Text("Guardar cambios") }
            }

            Spacer(Modifier.Companion.height(8.dp))
        }
    }
}