package com.example.notemoon.tools.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.notemoon.tools.presentation.age.AgeCalculatorScreen
import com.example.notemoon.tools.presentation.cointoss.CoinTossScreen
import com.example.notemoon.tools.presentation.datediff.DateDifferenceScreen
import com.example.notemoon.tools.presentation.emi.EmiCalculatorScreen
import com.example.notemoon.tools.presentation.qr.QrGeneratorScreen
import com.example.notemoon.tools.presentation.scanner.ScannerScreen
import com.example.notemoon.tools.presentation.sip.SipCalculatorScreen
import com.example.notemoon.tools.presentation.standard.StandardCalculatorScreen
import com.example.notemoon.tools.presentation.stopwatch.StopwatchScreen
import com.example.notemoon.tools.presentation.timer.TimerScreen
import com.example.notemoon.tools.presentation.tools.ToolsScreen

/** Registers the Tools module: the hub plus every calculator/utility screen. */
fun NavGraphBuilder.toolsGraph(navController: NavController) {
    composable(route = ToolsDestinations.TOOLS) {
        ToolsScreen(onOpenTool = { route -> navController.navigate(route) })
    }

    val back: () -> Unit = { navController.popBackStack() }

    composable(ToolsDestinations.STANDARD_CALCULATOR) { StandardCalculatorScreen(onNavigateBack = back) }
    composable(ToolsDestinations.AGE_CALCULATOR) { AgeCalculatorScreen(onNavigateBack = back) }
    composable(ToolsDestinations.EMI_CALCULATOR) { EmiCalculatorScreen(onNavigateBack = back) }
    composable(ToolsDestinations.SIP_CALCULATOR) { SipCalculatorScreen(onNavigateBack = back) }
    composable(ToolsDestinations.DATE_DIFFERENCE) { DateDifferenceScreen(onNavigateBack = back) }
    composable(ToolsDestinations.STOPWATCH) { StopwatchScreen(onNavigateBack = back) }
    composable(ToolsDestinations.TIMER) { TimerScreen(onNavigateBack = back) }
    composable(ToolsDestinations.QR_GENERATOR) { QrGeneratorScreen(onNavigateBack = back) }
    composable(ToolsDestinations.SCANNER) { ScannerScreen(onNavigateBack = back) }
    composable(ToolsDestinations.COIN_TOSS) { CoinTossScreen(onNavigateBack = back) }
}
