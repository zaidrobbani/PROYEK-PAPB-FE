package com.example.proyek_papb_fe.feature.umkm.create_project.container

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.proyek_papb_fe.feature.umkm.data.ProjectAttachment
import com.example.proyek_papb_fe.feature.umkm.data.UmkmProject

internal const val DescriptionMaxLength = 500

internal const val AttachmentMaxBytes = 10L * 1024 * 1024

internal val AttachmentMimeTypes = arrayOf("image/jpeg", "image/png", "application/pdf")

/** Diganti daftar skill BE (`GET /skills`) setelah dimuat. */
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
        val Saver = listSaver<CreateProjectFormState, Any>(
            save = { state ->
                listOf(
                    state.title,
                    state.category ?: "",
                    state.description,
                    ArrayList(state.skillOptions),
                    ArrayList(state.selectedSkills),
                    state.budgetDigits,
                    state.deadlineUtcMillis ?: -1L,
                    ArrayList(state.attachments.flatMap { listOf(it.uri, it.name, it.sizeBytes.toString()) }),
                    state.submitted
                )
            },
            restore = { saved ->
                CreateProjectFormState().apply {
                    title = saved[0] as String
                    category = (saved[1] as String).ifEmpty { null }
                    description = saved[2] as String
                    skillOptions.clear()
                    @Suppress("UNCHECKED_CAST")
                    skillOptions.addAll(saved[3] as List<String>)
                    @Suppress("UNCHECKED_CAST")
                    selectedSkills.addAll(saved[4] as List<String>)
                    budgetDigits = saved[5] as String
                    deadlineUtcMillis = (saved[6] as Long).takeIf { it >= 0 }
                    @Suppress("UNCHECKED_CAST")
                    (saved[7] as List<String>).chunked(3).forEach { (uri, name, size) ->
                        attachments.add(ProjectAttachment(uri, name, size.toLong()))
                    }
                    submitted = saved[8] as Boolean
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
