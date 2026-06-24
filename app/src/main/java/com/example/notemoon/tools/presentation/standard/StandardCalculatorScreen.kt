package com.example.notemoon.tools.presentation.standard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private enum class KeyType { NUMBER, OPERATOR, ACTION, EQUALS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: StandardCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val rows = listOf(
        listOf("AC", "⌫", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("+/−", "0", ".", "=")
    )

    fun handle(label: String) = when (label) {
        "AC" -> viewModel.onClear()
        "⌫" -> viewModel.onBackspace()
        "%" -> viewModel.onPercent()
        "+/−" -> viewModel.onToggleSign()
        "÷", "×", "−", "+" -> viewModel.onOperator(label)
        "=" -> viewModel.onEquals()
        "." -> viewModel.onDecimal()
        else -> viewModel.onDigit(label)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculator") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Display.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = state.expression.ifEmpty { "0" },
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Light,
                    maxLines = 2,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                if (state.preview.isNotBlank() && state.preview != state.expression) {
                    Text(
                        text = "= ${state.preview}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
            }

            // Keypad.
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rows.forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { label ->
                            CalcButton(
                                label = label,
                                type = keyType(label),
                                onClick = { handle(label) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun keyType(label: String): KeyType = when (label) {
    "÷", "×", "−", "+" -> KeyType.OPERATOR
    "=" -> KeyType.EQUALS
    "AC", "⌫", "%", "+/−" -> KeyType.ACTION
    else -> KeyType.NUMBER
}

@Composable
private fun CalcButton(
    label: String,
    type: KeyType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val container: Color = when (type) {
        KeyType.NUMBER -> MaterialTheme.colorScheme.surfaceVariant
        KeyType.OPERATOR -> MaterialTheme.colorScheme.primaryContainer
        KeyType.ACTION -> MaterialTheme.colorScheme.secondaryContainer
        KeyType.EQUALS -> MaterialTheme.colorScheme.primary
    }
    val content: Color = when (type) {
        KeyType.NUMBER -> MaterialTheme.colorScheme.onSurface
        KeyType.OPERATOR -> MaterialTheme.colorScheme.onPrimaryContainer
        KeyType.ACTION -> MaterialTheme.colorScheme.onSecondaryContainer
        KeyType.EQUALS -> MaterialTheme.colorScheme.onPrimary
    }

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(container)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 24.sp,
            color = content
        )
    }
}
