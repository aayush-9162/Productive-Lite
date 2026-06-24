package com.example.notemoon.tools.presentation.timer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notemoon.tools.presentation.util.formatTimer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val progress by animateFloatAsState(targetValue = state.progress, label = "timer")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timer") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (state.isActive) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(240.dp),
                            strokeWidth = 8.dp
                        )
                        Text(
                            text = formatTimer(state.remainingMillis / 1000),
                            style = MaterialTheme.typography.displaySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Light,
                            color = if (state.isFinished) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                } else {
                    TimeInputRow(
                        hours = state.inputHours,
                        minutes = state.inputMinutes,
                        seconds = state.inputSeconds,
                        onHours = viewModel::onHoursChange,
                        onMinutes = viewModel::onMinutesChange,
                        onSeconds = viewModel::onSecondsChange
                    )
                }
            }

            if (state.isFinished) {
                Text(
                    text = "Time's up!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = viewModel::reset,
                    modifier = Modifier.weight(1f),
                    enabled = state.isActive
                ) {
                    Text("Reset")
                }
                Button(
                    onClick = viewModel::startOrPause,
                    modifier = Modifier.weight(1f),
                    enabled = !state.isFinished
                ) {
                    Text(
                        when {
                            !state.isActive -> "Start"
                            state.isRunning -> "Pause"
                            else -> "Resume"
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeInputRow(
    hours: String,
    minutes: String,
    seconds: String,
    onHours: (String) -> Unit,
    onMinutes: (String) -> Unit,
    onSeconds: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        UnitField("Hrs", hours, onHours)
        Separator()
        UnitField("Min", minutes, onMinutes)
        Separator()
        UnitField("Sec", seconds, onSeconds)
    }
}

@Composable
private fun UnitField(label: String, value: String, onChange: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(84.dp)
        )
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun Separator() {
    Text(
        ":",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp)
    )
    Spacer(Modifier.width(2.dp))
}
