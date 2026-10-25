package com.example.proyek_papb_fe.feature.home.container

import androidx.compose.runtime.Composable

import androidx.compose.ui.tooling.preview.Preview

import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme
import com.example.proyek_papb_fe.feature.home.component.HomeScreen



@Preview(showBackground = false)
@Composable
private fun HomeScreenPreview() {
    PROYEKPAPBFETheme {
        HomeScreen(onItemClick = {})
    }
}
