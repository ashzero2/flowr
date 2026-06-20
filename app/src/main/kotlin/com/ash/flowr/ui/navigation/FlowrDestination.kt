package com.ash.flowr.ui.navigation

sealed class FlowrDestination(val route: String) {
    data object Dashboard : FlowrDestination("dashboard")
    data object Stats : FlowrDestination("stats")
    data object Review : FlowrDestination("review")
    data object Settings : FlowrDestination("settings")
}
