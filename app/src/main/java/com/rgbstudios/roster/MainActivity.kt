package com.rgbstudios.roster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.rgbstudios.roster.data.repository.NetworkMonitor
import com.rgbstudios.roster.data.repository.SupabaseRepository
import com.rgbstudios.roster.data.viewmodel.RosterViewModel
import com.rgbstudios.roster.navigation.AppNavigation
import com.rgbstudios.roster.ui.components.CustomTopBar
import com.rgbstudios.roster.ui.components.BottomNavigationBar
import com.rgbstudios.roster.ui.theme.RosterTheme
import com.rgbstudios.roster.utils.showLongToast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RosterTheme {
                val navController = rememberNavController()
                val networkMonitor = NetworkMonitor(applicationContext)
                val supabaseRepository = SupabaseRepository()
                val rosterViewModel = viewModel<RosterViewModel> {
                    RosterViewModel(supabaseRepository, networkMonitor)
                }

                val errorMessage by rosterViewModel.errorMessage.collectAsState()
                val context = LocalContext.current

                LaunchedEffect(errorMessage) {
                    errorMessage?.let { message ->
                        showLongToast(context, message)
                    }
                }

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
