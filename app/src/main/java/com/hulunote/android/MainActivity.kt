package com.hulunote.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.hulunote.android.ui.navigation.HulunoteNavGraph
import com.hulunote.android.ui.navigation.Routes
import com.hulunote.android.ui.theme.HulunoteTheme
import com.hulunote.android.util.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HulunoteTheme {
                val navController = rememberNavController()
                val startDestination = if (tokenManager.isLoggedIn) {
                    Routes.DATABASES
                } else {
                    Routes.LOGIN
                }
                HulunoteNavGraph(
                    navController = navController,
                    startDestination = startDestination,
                )
            }
        }
    }
}
