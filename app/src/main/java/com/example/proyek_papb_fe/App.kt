package com.example.proyek_papb_fe

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyek_papb_fe.core.di.AppContainer
import com.example.proyek_papb_fe.feature.umkm.profile.navigation.logoutToLogin
import com.example.proyek_papb_fe.navigation.AppSessionViewModel
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
        val sessionViewModel: AppSessionViewModel = viewModel(
            factory = AppSessionViewModel.factory(AppContainer.authRepository, AppContainer.sessionManager)
        )
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            sessionViewModel.sessionExpired.collect {
                sessionViewModel.clearCaches()
                Toast.makeText(context, "Sesi berakhir, silakan login kembali", Toast.LENGTH_LONG).show()
                navController.logoutToLogin()
            }
        }
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = backStackEntry?.destination

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
                sessionViewModel = sessionViewModel,
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding)
            )
        }
    }
}
