package com.example.proyek_papb_fe.core.data.remote

import com.example.proyek_papb_fe.core.data.remote.dto.ProjectCategoryDto
import com.example.proyek_papb_fe.core.data.remote.dto.SkillDto
import retrofit2.http.GET

interface MasterDataApiService {
    @GET("skills")
    suspend fun skills(): ApiResponse<List<SkillDto>>

    @GET("project-categories")
    suspend fun projectCategories(): ApiResponse<List<ProjectCategoryDto>>
}
