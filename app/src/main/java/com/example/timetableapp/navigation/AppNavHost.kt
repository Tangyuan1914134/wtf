package com.example.timetableapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.timetableapp.ui.edit.EditCourseScreen
import com.example.timetableapp.ui.edit.EditCourseViewModel
import com.example.timetableapp.ui.screens.settings.PeriodScheduleScreen
import com.example.timetableapp.ui.screens.settings.SettingsScreen
import com.example.timetableapp.ui.weekly.WeeklyScreen

object Routes {
    const val WEEKLY = "weekly"
    const val EDIT = "edit"
    const val SETTINGS = "settings"
    const val SETTINGS_PERIODS = "settings/periods"
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.WEEKLY) {
        composable(Routes.WEEKLY) {
            WeeklyScreen(
                onAddCourse = { day, startMin, startPeriod ->
                    val args = buildString {
                        append(Routes.EDIT)
                        append("?")
                        append("day=$day")
                        if (startMin != null) append("&startMin=$startMin")
                        if (startPeriod != null) append("&startPeriod=$startPeriod")
                    }
                    navController.navigate(args)
                },
                onEditCourse = { id -> navController.navigate("${Routes.EDIT}?courseId=$id") },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(
            route = "${Routes.EDIT}?courseId={courseId}&day={day}&startMin={startMin}&startPeriod={startPeriod}",
            arguments = listOf(
                navArgument("courseId") { type = NavType.LongType; defaultValue = -1L },
                navArgument("day") { type = NavType.IntType; defaultValue = -1 },
                navArgument("startMin") { type = NavType.IntType; defaultValue = -1 },
                navArgument("startPeriod") { type = NavType.IntType; defaultValue = -1 }
            )
        ) {
            val vm: EditCourseViewModel = hiltViewModel()
            EditCourseScreen(
                viewModel = vm,
                onClose = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenPeriods = { navController.navigate(Routes.SETTINGS_PERIODS) }
            )
        }
        composable(Routes.SETTINGS_PERIODS) {
            PeriodScheduleScreen(onBack = { navController.popBackStack() })
        }
    }
}
