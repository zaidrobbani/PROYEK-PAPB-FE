package com.example.proyek_papb_fe.feature.umkm.create_project.container

import androidx.compose.runtime.saveable.Saver
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.feature.umkm.data.ProjectAttachment
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject

internal const val DescriptionMaxLength = 500

internal const val AttachmentMaxBytes = 10L * 1024 * 1024

internal val AttachmentMimeTypes = arrayOf("image/jpeg", "image/png", "application/pdf")

internal val SuggestedSkills = emptyList<String>()

data class CreateProjectDraft(
    val title: String,
    val category: String,
    val description: String,
    val skills: List<String>,
    val budget: Long,
    val deadlineUtcMillis: Long,
    val attachments: List<ProjectAttachment>,
    val status: String = "Terbuka"
)

internal data class CreateProjectErrors(
    val title: String? = null,
    val category: String? = null,
    val description: String? = null,
    val skills: String? = null,
    val budget: String? = null,
    val deadline: String? = null
) {
    val hasError: Boolean
        get() = listOf(title, category, description, skills, budget, deadline).any { it != null }
}

internal enum class ProjectFormField { Title, Category, Description, Skills, Budget, Deadline }

@Stable
internal class CreateProjectFormState {
    var title by mutableStateOf("")
    var category by mutableStateOf<String?>(null)
    var description by mutableStateOf("")
    val skillOptions = mutableStateListOf<String>().apply { addAll(SuggestedSkills) }
    val selectedSkills = mutableStateListOf<String>()
    var budgetDigits by mutableStateOf("")
    var deadlineUtcMillis by mutableStateOf<Long?>(null)
    val attachments = mutableStateListOf<ProjectAttachment>()
    var attachmentError by mutableStateOf<String?>(null)
    private var submitted by mutableStateOf(false)

    fun fillFrom(project: UmkmProject, keepDeadline: Boolean = true) {
        title = project.title
        category = project.category
        description = project.description
        project.skills.forEach { skill ->
            if (skill !in skillOptions) skillOptions.add(skill)
            if (skill !in selectedSkills) selectedSkills.add(skill)
        }
        budgetDigits = project.budget.toString()
        deadlineUtcMillis = if (keepDeadline) project.deadlineUtcMillis else null
        attachments.addAll(project.attachments)
    }

    fun toggleSkill(skill: String) {
        if (skill in selectedSkills) selectedSkills.remove(skill) else selectedSkills.add(skill)
    }

    fun addCustomSkill(raw: String) {
        val skill = raw.trim()
        if (skill.isEmpty()) return
        val existing = skillOptions.firstOrNull { it.equals(skill, ignoreCase = true) }
        if (existing == null) skillOptions.add(skill)
        val name = existing ?: skill
        if (name !in selectedSkills) selectedSkills.add(name)
    }

    val visibleErrors: CreateProjectErrors
        get() = if (submitted) validate() else CreateProjectErrors()

    fun submit(): CreateProjectDraft? {
        submitted = true
        if (validate().hasError) return null
        return CreateProjectDraft(
            title = title.trim(),
            category = category!!,
            description = description.trim(),
            skills = selectedSkills.toList(),
            budget = budgetDigits.toLong(),
            deadlineUtcMillis = deadlineUtcMillis!!,
            attachments = attachments.toList()
        )
    }

    fun firstErrorField(): ProjectFormField? {
        val errors = validate()
        return when {
            errors.title != null -> ProjectFormField.Title
            errors.category != null -> ProjectFormField.Category
            errors.description != null -> ProjectFormField.Description
            errors.skills != null -> ProjectFormField.Skills
            errors.budget != null -> ProjectFormField.Budget
            errors.deadline != null -> ProjectFormField.Deadline
            else -> null
        }
    }

    private fun validate() = CreateProjectErrors(
        title = "Judul project wajib diisi.".takeIf { title.isBlank() },
        category = "Pilih kategori project.".takeIf { category == null },
        description = "Deskripsi kebutuhan wajib diisi.".takeIf { description.isBlank() },
        skills = "Pilih minimal satu keahlian.".takeIf { selectedSkills.isEmpty() },
        budget = when {
            budgetDigits.isEmpty() -> "Estimasi budget wajib diisi."
            budgetDigits.toLong() == 0L -> "Estimasi budget harus lebih dari Rp 0."
            else -> null
        },
        deadline = "Pilih batas waktu pengerjaan.".takeIf { deadlineUtcMillis == null }
    )

    companion object {
        val Saver = Saver<CreateProjectFormState, Bundle>(
            save = { state ->
                Bundle().apply {
                    putString("title", state.title)
                    putString("category", state.category)
                    putString("description", state.description)
                    putStringArrayList("skillOptions", ArrayList(state.skillOptions))
                    putStringArrayList("selectedSkills", ArrayList(state.selectedSkills))
                    putString("budgetDigits", state.budgetDigits)
                    state.deadlineUtcMillis?.let { putLong("deadlineUtcMillis", it) }
                    putStringArrayList("attachmentUris", ArrayList(state.attachments.map { it.uri }))
                    putStringArrayList("attachmentNames", ArrayList(state.attachments.map { it.name }))
                    putLongArray("attachmentSizes", state.attachments.map { it.sizeBytes }.toLongArray())
                    putBoolean("submitted", state.submitted)
                }
            },
            restore = { saved ->
                CreateProjectFormState().apply {
                    title = saved.getString("title").orEmpty()
                    category = saved.getString("category")
                    description = saved.getString("description").orEmpty()
                    skillOptions.clear()
                    skillOptions.addAll(saved.getStringArrayList("skillOptions").orEmpty())
                    selectedSkills.addAll(saved.getStringArrayList("selectedSkills").orEmpty())
                    budgetDigits = saved.getString("budgetDigits").orEmpty()
                    deadlineUtcMillis = if (saved.containsKey("deadlineUtcMillis")) saved.getLong("deadlineUtcMillis") else null
                    val uris = saved.getStringArrayList("attachmentUris").orEmpty()
                    val names = saved.getStringArrayList("attachmentNames").orEmpty()
                    val sizes = saved.getLongArray("attachmentSizes") ?: LongArray(0)
                    uris.indices.forEach { i -> attachments.add(ProjectAttachment(uris[i], names[i], sizes[i])) }
                    submitted = saved.getBoolean("submitted")
                }
            }
        )
    }
}

@Composable
internal fun rememberCreateProjectFormState(
    initial: UmkmProject? = null,
    keepDeadline: Boolean = true
): CreateProjectFormState =
    rememberSaveable(saver = CreateProjectFormState.Saver) {
        CreateProjectFormState().apply { if (initial != null) fillFrom(initial, keepDeadline) }
    }
