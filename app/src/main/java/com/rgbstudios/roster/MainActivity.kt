package com.rgbstudios.roster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.rgbstudios.roster.data.model.StaffMember
import com.rgbstudios.roster.ui.MainScreen
import com.rgbstudios.roster.ui.theme.RosterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // enableEdgeToEdge()
        setContent {
            RosterTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                      MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}