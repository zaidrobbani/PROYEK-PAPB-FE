package com.example.proyek_papb_fe.core.data.remote.dto

data class SkillDto(val id: String, val name: String)

data class ProjectCategoryDto(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean?
)
