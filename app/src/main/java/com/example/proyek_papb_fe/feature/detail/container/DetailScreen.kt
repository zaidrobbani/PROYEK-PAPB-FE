package com.example.proyek_papb_fe.feature.detail.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.proyek_papb_fe.core.data.ItemDummyData
import com.example.proyek_papb_fe.core.ui.components.AppTopBar
import com.example.proyek_papb_fe.core.ui.theme.PROYEKPAPBFETheme

@Composable
fun DetailScreen(
    itemId: Int,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val item = ItemDummyData.getById(itemId)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { AppTopBar(title = "Detail", onBack = onBack) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (item == null) {
                Text("Item dengan id $itemId tidak ditemukan.")
            } else {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    PROYEKPAPBFETheme {
        DetailScreen(itemId = 1, onBack = {})
    }
}
