package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.domain.model.ProjectStatusCode
import kotlin.math.roundToInt
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/** Kecocokan terbaik satu freelancer dengan salah satu project TERBUKA milik UMKM. */
data class FreelancerBestMatch(val percent: Int, val projectId: String, val projectTitle: String)

/**
 * Matching F1 PRD (skill project vs skill freelancer, ambang 60%) dihitung BE per project
 * (`GET /projects/{id}/recommendations/freelancers`, hanya yang lolos ambang). Daftar freelancer UMKM
 * tidak terikat satu project, jadi diambil skor tertinggi tiap freelancer di semua project TERBUKA.
 */
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
