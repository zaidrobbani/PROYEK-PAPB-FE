package com.example.proyek_papb_fe.feature.freelancer.projectdetail.container

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyek_papb_fe.core.data.FreelancerProjectDummyData
import com.example.proyek_papb_fe.core.ui.components.DetailTopBar
import com.example.proyek_papb_fe.core.ui.components.FeaturePlaceholder
import com.example.proyek_papb_fe.feature.freelancer.projectdetail.component.ProjectDetailScreen

@Composable
fun ProjectDetailContainer(
    projectId: Int,
    onBack: () -> Unit,
    onChatClick: () -> Unit = {},
    onSubmitOfferClick: () -> Unit = {}
) {
    val project = remember(projectId) { FreelancerProjectDummyData.getById(projectId) }

    if (project == null) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(title = "Detail Proyek", onBack = onBack)
            FeaturePlaceholder(
                title = "Proyek tidak ditemukan",
                description = "Proyek dengan id $projectId tidak tersedia."
            )
        }
        return
    }

    ProjectDetailScreen(
        project = project,
        onBack = onBack,
        onChatClick = onChatClick,
        onSubmitOfferClick = onSubmitOfferClick
    )
}

@Preview(showBackground = true, widthDp = 412, heightDp = 915)
@Composable
private fun ProjectDetailContainerPreview() {
    ProjectDetailContainer(projectId = 101, onBack = {})
}
