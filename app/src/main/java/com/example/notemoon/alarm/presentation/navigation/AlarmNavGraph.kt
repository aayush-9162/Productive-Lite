package com.example.notemoon.alarm.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notemoon.alarm.presentation.addedit.AddEditAlarmScreen
import com.example.notemoon.alarm.presentation.list.AlarmListScreen

/** Registers the Alarm feature's list and add/edit screens. */
fun NavGraphBuilder.alarmGraph(navController: NavController) {
    composable(AlarmDestinations.ALARM_LIST) {
        AlarmListScreen(
            onAddAlarm = { navController.navigate(AlarmDestinations.addEditAlarm()) },
            onEditAlarm = { id -> navController.navigate(AlarmDestinations.addEditAlarm(id)) },
            onNavigateBack = { navController.popBackStack() }
        )
    }

    composable(
        route = AlarmDestinations.ADD_EDIT_ALARM,
        arguments = listOf(
            navArgument(AlarmDestinations.ALARM_ID_ARG) {
                type = NavType.LongType
                defaultValue = AlarmDestinations.NO_ALARM_ID
            }
        )
    ) {
        AddEditAlarmScreen(onNavigateBack = { navController.popBackStack() })
    }
}
