package com.example.saveapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.saveapp.ui.theme.Navigation.AppNavigation
import com.example.saveapp.ui.theme.SaveAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaveAppTheme {
                AppNavigation()
            }
        }
    }
}
