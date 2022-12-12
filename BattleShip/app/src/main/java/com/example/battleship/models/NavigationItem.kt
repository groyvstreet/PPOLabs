package com.example.battleship.models

import com.example.battleship.R

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Profile : NavigationItem("profile", R.drawable.ic_baseline_person_24, "Profile")
    object Home : NavigationItem("home", R.drawable.ic_baseline_sports_esports_24, "Play")
    object Statistic : NavigationItem("statistic", R.drawable.ic_baseline_view_list_24, "Statistic")
}
