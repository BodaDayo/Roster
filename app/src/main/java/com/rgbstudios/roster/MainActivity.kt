package com.rgbstudios.roster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.ramcosta.composedestinations.DestinationsNavHost
import com.rgbstudios.roster.ui.NavGraphs
import com.rgbstudios.roster.ui.theme.RosterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent {
            RosterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}