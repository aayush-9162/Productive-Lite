package com.example.notemoon.tools.presentation.tools

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.notemoon.tools.presentation.navigation.ToolsDestinations

private data class Tool(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(onOpenTool: (String) -> Unit) {
    val tools = listOf(
        Tool("Standard calculator", "Everyday arithmetic", Icons.Filled.Calculate, ToolsDestinations.STANDARD_CALCULATOR),
        Tool("SIP calculator", "Project investment growth", Icons.Filled.Savings, ToolsDestinations.SIP_CALCULATOR),
        Tool("EMI calculator", "Loan instalments & interest", Icons.Filled.Payments, ToolsDestinations.EMI_CALCULATOR),
        Tool("Age calculator", "Age from a date of birth", Icons.Filled.Cake, ToolsDestinations.AGE_CALCULATOR),
        Tool("Date difference", "Days between two dates", Icons.Filled.DateRange, ToolsDestinations.DATE_DIFFERENCE),
        Tool("Stopwatch", "Time with laps", Icons.Filled.Timer, ToolsDestinations.STOPWATCH),
        Tool("Timer", "Countdown with alert", Icons.Filled.HourglassEmpty, ToolsDestinations.TIMER),
        Tool("QR generator", "Make a QR code", Icons.Filled.QrCode2, ToolsDestinations.QR_GENERATOR),
        Tool("QR & barcode scanner", "Scan codes with the camera", Icons.Filled.QrCodeScanner, ToolsDestinations.SCANNER),
        Tool("Coin toss", "Heads or tails", Icons.Filled.Casino, ToolsDestinations.COIN_TOSS)
    )

    androidx.compose.material3.Scaffold(
        topBar = { TopAppBar(title = { Text("Tools", fontWeight = FontWeight.Bold) }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
        ) {
            items(tools) { tool ->
                ToolCard(tool, onClick = { onOpenTool(tool.route) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ToolCard(tool: Tool, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(tool.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(
                    tool.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
