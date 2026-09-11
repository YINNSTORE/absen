@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.albasroh.absensi.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

@Composable
fun MenuBlock(
    title: String,
    desc: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = desc,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Composable
fun RowScope.Stat(
    label: String,
    value: Int
) {
    Card(
        modifier = Modifier.weight(1f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall
            )
            Text(text = label)
        }
    }
}

@Composable
fun Field(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    secret: Boolean = false,
    modifier: Modifier = Modifier,
    minLines: Int = 1
) {
    var visible by remember(secret) { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        singleLine = minLines == 1,
        minLines = minLines,
        keyboardOptions = if (secret) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        visualTransformation = if (secret && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (secret) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = if (visible) "Sembunyikan password" else "Tampilkan password")
                }
            }
        } else null
    )
}

@Composable
fun Badge(text: String) {
    AssistChip(
        onClick = {},
        label = { Text(text) },
        leadingIcon = {
            Icon(
                imageVector = if (text == "HADIR") {
                    Icons.Default.CheckCircle
                } else {
                    Icons.Default.Info
                },
                contentDescription = null
            )
        }
    )
}

@Composable
fun ScaffoldBack(
    title: String,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        },
        content = content
    )
}
