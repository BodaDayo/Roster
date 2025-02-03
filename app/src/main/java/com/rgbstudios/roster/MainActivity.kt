package com.rgbstudios.roster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.navigation.AppNavigation
import com.rgbstudios.roster.ui.CustomTopBar
import com.rgbstudios.roster.ui.components.BottomNavigationBar
import com.rgbstudios.roster.ui.theme.RosterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RosterTheme {
                val navController = rememberNavController()
                val rosterViewModel: RosterViewModel = viewModel()
                Scaffold(
                    topBar = {
                        CustomTopBar(navController)
                    },
                    bottomBar = { BottomNavigationBar(navController) }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(navController = navController, rosterViewModel = rosterViewModel)
                    }
                }
            }
        }
    }
}
