package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import kotlin.math.roundToInt
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

data class FreelancerBestMatch(val percent: Int, val projectId: String, val projectTitle: String)

class FreelancerMatchRepository(
    private val workflowRepository: WorkflowRepository,
    private val projectRepository: ProjectRepository
) {
    suspend fun bestMatches(): Map<String, FreelancerBestMatch> = coroutineScope {
        val openProjects = runCatching { workflowRepository.myProjects(ProjectStatusCode.TERBUKA).items }
            .getOrDefault(emptyList())
            .map { it.project }
            .filter { it.status == ProjectStatusCode.TERBUKA }
        openProjects.map { project ->
            async {
                runCatching { projectRepository.recommendedFreelancers(project.id) }.getOrDefault(emptyList()).map {
                    it.freelancerId to FreelancerBestMatch(it.match.scorePercent.roundToInt(), project.id, project.title)
                }
            }
        }.awaitAll()
            .flatten()
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, matches) -> matches.maxBy { it.percent } }
    }
}
