package com.example.proyek_papb_fe

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyek_papb_fe.core.navigation.navigateToTopLevel
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.navigation.AppBottomBar
import com.example.proyek_papb_fe.navigation.AppNavHost
import com.example.proyek_papb_fe.navigation.TopLevelDestination
import com.example.proyek_papb_fe.navigation.isTopLevelDestination

@Composable
fun App() {
    PROYEKPAPBFETheme {
        val navController = rememberNavController()
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

        // Bottom bar hanya muncul di halaman tab utama (bukan di halaman detail)
        val showBottomBar = TopLevelDestination.entries.any {
            currentDestination.isTopLevelDestination(it)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    AppBottomBar(
                        currentDestination = currentDestination,
                        onNavigate = { navController.navigateToTopLevel(it.route) }
                    )
                }
            }
        ) { innerPadding ->
            AppNavHost(
                navController = navController,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}
