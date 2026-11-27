package com.example.proyek_papb_fe.core.data.repository

import com.example.proyek_papb_fe.core.data.remote.dto.toDomain
import com.example.proyek_papb_fe.core.data.source.MasterDataRemoteDataSource
import com.example.proyek_papb_fe.core.domain.model.Category
import com.example.proyek_papb_fe.core.domain.model.Skill
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

interface MasterDataRepository {
    suspend fun skills(): List<Skill>
    suspend fun categories(): List<Category>
    suspend fun skillNames(ids: List<String>): List<String>
    suspend fun categoryName(id: String): String
}

class DefaultMasterDataRepository(
    private val remote: MasterDataRemoteDataSource
) : MasterDataRepository {
    private val mutex = Mutex()
    private var cachedSkills: List<Skill>? = null
    private var cachedCategories: List<Category>? = null

    override suspend fun skills(): List<Skill> = mutex.withLock {
        cachedSkills ?: remote.skills().map { it.toDomain() }.also { cachedSkills = it }
    }

    override suspend fun categories(): List<Category> = mutex.withLock {
        cachedCategories ?: remote.categories().map { it.toDomain() }.also { cachedCategories = it }
    }

    override suspend fun skillNames(ids: List<String>): List<String> {
        val byId = runCatching { skills() }.getOrDefault(emptyList()).associateBy { it.id }
        return ids.map { byId[it]?.name ?: it }
    }

    override suspend fun categoryName(id: String): String =
        runCatching { categories() }.getOrDefault(emptyList()).firstOrNull { it.id == id }?.name ?: id
}
