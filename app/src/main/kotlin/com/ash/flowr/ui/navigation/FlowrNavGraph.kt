package com.ash.flowr.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ash.flowr.ui.screen.dashboard.DashboardScreen
import com.ash.flowr.ui.screen.settings.SettingsScreen
import com.ash.flowr.ui.screen.stats.StatsScreen
import com.ash.flowr.ui.sheet.MenuSheet
import com.ash.flowr.ui.sheet.onboarding.OnboardingSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowrNavGraph(viewModel: MainViewModel = hiltViewModel()) {
    val onboardingComplete by viewModel.onboardingComplete.collectAsState()

    when (onboardingComplete) {
        null -> Unit  // Loading — DataStore read in-flight; brief blank
        false -> OnboardingSheet(onComplete = viewModel::onOnboardingComplete)
        true -> MainScaffold()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainScaffold() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == FlowrDestination.Dashboard.route,
                    onClick = { navController.navigate(FlowrDestination.Dashboard.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { /* quick-add sheet — Phase 2 */ },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                    label = { Text("Add") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showMenu = true },
                    icon = { Icon(Icons.Default.Apps, contentDescription = "Menu") },
                    label = { Text("Menu") }
                )
                NavigationBarItem(
                    selected = currentRoute == FlowrDestination.Settings.route,
                    onClick = { navController.navigate(FlowrDestination.Settings.route) },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FlowrDestination.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(FlowrDestination.Dashboard.route) { DashboardScreen() }
            composable(FlowrDestination.Stats.route) { StatsScreen() }
            composable(FlowrDestination.Settings.route) { SettingsScreen() }
        }
    }

    if (showMenu) {
        ModalBottomSheet(
            onDismissRequest = { showMenu = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            MenuSheet(onDismiss = { showMenu = false })
        }
    }
}
