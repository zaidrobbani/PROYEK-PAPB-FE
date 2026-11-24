package com.example.proyek_papb_fe.core.data.remote.dto

import com.example.proyek_papb_fe.core.domain.model.Agreement
import com.example.proyek_papb_fe.core.domain.model.AppNotification
import com.example.proyek_papb_fe.core.domain.model.Application
import com.example.proyek_papb_fe.core.domain.model.Cancellation
import com.example.proyek_papb_fe.core.domain.model.Category
import com.example.proyek_papb_fe.core.domain.model.Completion
import com.example.proyek_papb_fe.core.domain.model.Conversation
import com.example.proyek_papb_fe.core.domain.model.DelayRisk
import com.example.proyek_papb_fe.core.domain.model.Experience
import com.example.proyek_papb_fe.core.domain.model.FreelancerDetail
import com.example.proyek_papb_fe.core.domain.model.FreelancerProfessionalProfile
import com.example.proyek_papb_fe.core.domain.model.FreelancerRecommendation
import com.example.proyek_papb_fe.core.domain.model.FreelancerReviews
import com.example.proyek_papb_fe.core.domain.model.FreelancerSummary
import com.example.proyek_papb_fe.core.domain.model.LatestSubmission
import com.example.proyek_papb_fe.core.domain.model.Message
import com.example.proyek_papb_fe.core.domain.model.MessageAttachment
import com.example.proyek_papb_fe.core.domain.model.Milestone
import com.example.proyek_papb_fe.core.domain.model.MilestonePlan
import com.example.proyek_papb_fe.core.domain.model.MilestoneProgress
import com.example.proyek_papb_fe.core.domain.model.MyProjectAgreement
import com.example.proyek_papb_fe.core.domain.model.MyProjectItem
import com.example.proyek_papb_fe.core.domain.model.PaymentAttempt
import com.example.proyek_papb_fe.core.domain.model.PaymentDetail
import com.example.proyek_papb_fe.core.domain.model.PaymentHistoryItem
import com.example.proyek_papb_fe.core.domain.model.PaymentSummary
import com.example.proyek_papb_fe.core.domain.model.Portfolio
import com.example.proyek_papb_fe.core.domain.model.PortfolioMedia
import com.example.proyek_papb_fe.core.domain.model.Project
import com.example.proyek_papb_fe.core.domain.model.ProjectFile
import com.example.proyek_papb_fe.core.domain.model.ProjectRecommendation
import com.example.proyek_papb_fe.core.domain.model.ProjectSummary
import com.example.proyek_papb_fe.core.domain.model.PublicReview
import com.example.proyek_papb_fe.core.domain.model.Review
import com.example.proyek_papb_fe.core.domain.model.RevisionItem
import com.example.proyek_papb_fe.core.domain.model.Skill
import com.example.proyek_papb_fe.core.domain.model.SkillMatch
import com.example.proyek_papb_fe.core.domain.model.Submission
import com.example.proyek_papb_fe.core.domain.model.SubmissionFile
import com.example.proyek_papb_fe.core.domain.model.SubmissionList
import com.example.proyek_papb_fe.core.domain.model.SubmissionReview
import com.example.proyek_papb_fe.core.domain.model.UmkmBusinessProfile
import com.example.proyek_papb_fe.core.domain.model.UserSummary
import com.example.proyek_papb_fe.core.domain.model.Wallet
import com.example.proyek_papb_fe.core.domain.model.WalletEntry
import com.example.proyek_papb_fe.core.domain.model.WorkflowDetail
import com.example.proyek_papb_fe.core.domain.model.WorkflowHistory
import com.example.proyek_papb_fe.core.util.DateTimeParser.parseDateMillis
import com.example.proyek_papb_fe.core.util.DateTimeParser.parseUtcMillis

private fun String?.toDecimal(): Double = this?.toDoubleOrNull() ?: 0.0

fun SkillDto.toDomain() = Skill(id, name)

fun ProjectCategoryDto.toDomain() = Category(id, name, description.orEmpty())

fun UmkmProfileDto.toDomain() = UmkmBusinessProfile(userId, businessName, businessCategory, businessAddress, city)

fun FreelancerProfileDto.toDomain() = FreelancerProfessionalProfile(
    userId = userId,
    professionalTitle = professionalTitle,
    city = city,
    bio = bio,
    servicePriceMin = servicePriceMin,
    servicePriceMax = servicePriceMax,
    availabilityStatus = availabilityStatus ?: "AVAILABLE"
)

fun ExperienceDto.toDomain() = Experience(id, jobTitle, organizationName, description, startDate, endDate)

fun PortfolioDto.toDomain() =
    Portfolio(id, title, description, projectUrl, parseUtcMillis(updatedAt ?: createdAt), coverImageUrl)

fun PortfolioMediaDto.toDomain() = PortfolioMedia(id, portfolioId, mimeType, fileSizeBytes, sortOrder, isCover)

fun ProjectDto.toDomain() = Project(
    id = id,
    umkmId = umkmId,
    categoryId = categoryId,
    title = title,
    description = description,
    budget = budget,
    deadline = deadline,
    deadlineMillis = parseDateMillis(deadline),
    status = status,
    publishedAtMillis = parseUtcMillis(publishedAt),
    createdAtMillis = parseUtcMillis(createdAt),
    skillIds = skillIds,
    owner = umkm?.let {
        UserSummary(
            id = it.userId,
            name = it.ownerName,
            subtitle = it.businessName,
            photoUrl = it.profileImageUrl,
            city = it.city,
            averageRating = null,
            reviewCount = 0
        )
    }
)

fun ProjectAttachmentDto.toDomain() = ProjectFile(id, originalFileName, mimeType, fileSizeBytes)

fun ApplicationDto.toDomain() = Application(
    id = id,
    projectId = projectId,
    freelancerId = freelancerId,
    proposedPrice = proposedPrice,
    durationDays = estimatedDurationDays,
    message = proposalMessage,
    status = status,
    submittedAtMillis = parseUtcMillis(submittedAt ?: createdAt)
)

fun SkillMatchDto.toDomain() = SkillMatch(
    scorePercent = scorePercent,
    matchedSkillIds = matchedSkillIds.orEmpty(),
    missingSkillIds = missingSkillIds.orEmpty(),
    meetsThreshold = meetsThreshold
)

fun ProjectRecommendationDto.toDomain() = ProjectRecommendation(
    projectId = project.projectId,
    umkmId = project.umkmId,
    title = project.title,
    categoryId = project.categoryId,
    status = project.status,
    requiredSkillIds = project.requiredSkillIds.orEmpty(),
    match = match.toDomain()
)

fun FreelancerRecommendationDto.toDomain() = FreelancerRecommendation(
    freelancerId = freelancer.freelancerId,
    name = freelancer.name,
    professionalTitle = freelancer.professionalTitle,
    city = freelancer.city,
    skillIds = freelancer.skillIds.orEmpty(),
    servicePriceMin = freelancer.servicePriceMin,
    servicePriceMax = freelancer.servicePriceMax,
    match = match.toDomain()
)

fun FreelancerSummaryDto.toDomain() = FreelancerSummary(
    userId = userId,
    name = name,
    professionalTitle = professionalTitle,
    city = city,
    isAvailable = availabilityStatus == null || availabilityStatus == "AVAILABLE",
    servicePriceMin = servicePriceMin,
    servicePriceMax = servicePriceMax,
    skills = skills.orEmpty().map { it.toDomain() },
    experienceCount = experienceCount ?: 0,
    averageRating = averageRating,
    reviewCount = reviewCount ?: 0,
    photoUrl = profileImageUrl
)

fun FreelancerDetailDto.toDomain() = FreelancerDetail(
    summary = FreelancerSummary(
        userId = userId,
        name = name,
        professionalTitle = professionalTitle,
        city = city,
        isAvailable = availabilityStatus == null || availabilityStatus == "AVAILABLE",
        servicePriceMin = servicePriceMin,
        servicePriceMax = servicePriceMax,
        skills = skills.orEmpty().map { it.toDomain() },
        experienceCount = experienceCount ?: 0,
        averageRating = averageRating,
        reviewCount = reviewCount ?: 0,
        photoUrl = profileImageUrl
    ),
    bio = bio,
    experiences = experiences.orEmpty().map { it.toDomain() },
    portfolios = portfolios.orEmpty().map { it.toDomain() }
)

fun UserSummaryDto.toDomain() = UserSummary(
    id = id,
    name = name,
    subtitle = subtitle,
    photoUrl = profileImageUrl,
    city = city,
    averageRating = averageRating,
    reviewCount = reviewCount ?: 0
)

fun ProjectSummaryDto.toDomain() = ProjectSummary(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    budget = budget,
    deadline = deadline,
    deadlineMillis = parseDateMillis(deadline),
    status = status,
    statusLabel = statusLabel,
    publishedAtMillis = parseUtcMillis(publishedAt),
    startedAtMillis = parseUtcMillis(startedAt),
    completedAtMillis = parseUtcMillis(completedAt)
)

fun PaymentSummaryDto.toDomain() = PaymentSummary(
    id = id,
    status = status,
    statusLabel = statusLabel,
    fundStatus = fundStatus,
    fundStatusLabel = fundStatusLabel,
    grossAmount = grossAmount,
    platformFee = platformFeeAmount,
    freelancerAmount = freelancerAmount,
    transactionNumber = transactionNumber,
    paidAtMillis = parseUtcMillis(paidAt),
    releasedAtMillis = parseUtcMillis(releasedAt),
    expiresAtMillis = parseUtcMillis(expiresAt)
)

fun MyProjectItemDto.toDomain() = MyProjectItem(
    project = project.toDomain(),
    counterpart = counterpart?.toDomain(),
    agreement = agreement?.let { MyProjectAgreement(it.agreedPrice, it.agreedDeadline, it.durationDays, it.status) },
    payment = payment?.toDomain(),
    applicationCount = applicationCount,
    progressPercent = progressPercent
)

fun AgreementDto.toDomain() = Agreement(
    id = id,
    freelancerId = freelancerId,
    applicationId = applicationId,
    scopeOfWork = scopeOfWork,
    agreedPrice = agreedPrice,
    agreedDeadline = agreedDeadline,
    agreedDeadlineMillis = parseDateMillis(agreedDeadline),
    durationDays = durationDays,
    commissionPercent = commissionPercent,
    platformFee = platformFeeAmount,
    freelancerAmount = freelancerAmount,
    paymentMethodLabel = paymentMethodLabel,
    status = status,
    statusLabel = statusLabel,
    version = agreementVersion,
    umkmApprovedAtMillis = parseUtcMillis(umkmApprovedAt),
    freelancerApprovedAtMillis = parseUtcMillis(freelancerApprovedAt),
    approvedByMe = approvedByMe,
    waitingFor = waitingFor.orEmpty(),
    changeRequestNote = changeRequestNote,
    changeRequestedAtMillis = parseUtcMillis(changeRequestedAt),
    createdAtMillis = parseUtcMillis(createdAt)
)

fun WorkflowDetailDto.toDomain() = WorkflowDetail(
    project = project.toDomain(),
    myRole = myRole,
    umkm = umkm.toDomain(),
    freelancer = freelancer?.toDomain(),
    agreement = agreement?.toDomain(),
    payment = payment?.toDomain(),
    latestSubmission = latestSubmission?.let {
        LatestSubmission(it.id, it.versionNumber, it.versionLabel, parseUtcMillis(it.submittedAt), it.decision, it.decisionLabel)
    },
    revisionCount = revisionCount,
    hasReview = hasReview,
    availableActions = availableActions.orEmpty().map { it.action },
    history = history.orEmpty().map {
        WorkflowHistory(
            id = it.id,
            newStatus = it.newStatus,
            newStatusLabel = it.newStatusLabel,
            action = it.action,
            actionLabel = it.actionLabel,
            changedByUserId = it.changedByUserId,
            reason = it.changeReason,
            changedAtMillis = parseUtcMillis(it.changedAt)
        )
    },
    cancellation = cancellation?.let {
        Cancellation(it.reason, it.reasonLabel, it.note, it.cancelledBy, parseUtcMillis(it.cancelledAt))
    }
)

fun MilestoneDto.toDomain() = Milestone(
    id = id,
    name = name,
    description = description.orEmpty(),
    weightPercent = weightPercent.toDecimal(),
    isDone = status == "SELESAI",
    sortOrder = sortOrder,
    completedAtMillis = parseUtcMillis(completedAt)
)

fun MilestonePlanDto.toDomain() = MilestonePlan(
    items = items.orEmpty().map { it.toDomain() }.sortedBy { it.sortOrder },
    totalWeight = totalWeight.toDecimal(),
    isComplete = isPlanComplete
)

fun MilestoneProgressDto.toDomain() = MilestoneProgress(
    progressPercent = progressPercent.toDecimal(),
    totalMilestones = totalMilestones,
    completedMilestones = completedMilestones,
    isPlanComplete = isPlanComplete
)

fun MilestoneDelayRiskDto.toDomain() = DelayRisk(
    level = riskLevel,
    elapsedDays = elapsedDays,
    totalDays = totalDays,
    expectedPercent = expectedProgressPercent.toDecimal(),
    actualPercent = actualProgressPercent.toDecimal()
)

fun PaymentDetailDto.toDomain() = PaymentDetail(
    id = id,
    projectId = project.id,
    projectTitle = project.title,
    transactionNumber = transactionNumber,
    status = status,
    statusLabel = statusLabel,
    fundStatus = fundStatus,
    displayStatus = displayStatus,
    displayStatusLabel = displayStatusLabel,
    paymentMethodLabel = paymentMethodLabel,
    grossAmount = grossAmount,
    platformFee = platformFeeAmount,
    freelancerAmount = freelancerAmount,
    totalAmount = totalAmount,
    commissionPercent = commissionPercent,
    expiresAtMillis = parseUtcMillis(expiresAt),
    secondsRemaining = secondsRemaining,
    paidAtMillis = parseUtcMillis(paidAt),
    createdAtMillis = parseUtcMillis(createdAt),
    scopeOfWork = agreement.scopeOfWork,
    agreedDeadline = agreement.agreedDeadline,
    durationDays = agreement.durationDays,
    umkm = umkm.toDomain(),
    freelancer = freelancer.toDomain(),
    attempts = attempts.orEmpty().map {
        PaymentAttempt(it.attemptNumber, it.status, it.failureReason, parseUtcMillis(it.attemptedAt))
    }
)

fun PaymentHistoryItemDto.toDomain() = PaymentHistoryItem(
    paymentId = paymentId,
    transactionNumber = transactionNumber,
    projectId = projectId,
    projectTitle = projectTitle,
    freelancerName = freelancerName,
    amount = amount,
    status = status,
    fundStatus = fundStatus,
    displayStatus = displayStatus,
    displayStatusLabel = displayStatusLabel,
    occurredAtMillis = parseUtcMillis(occurredAt)
)

fun WalletDto.toDomain() = Wallet(availableBalance, heldBalance, totalBalance)

fun WalletEntryDto.toDomain() = WalletEntry(
    id = id,
    type = type,
    typeLabel = typeLabel,
    amount = amount,
    projectId = projectId,
    projectTitle = projectTitle,
    stillHeld = stillHeld,
    createdAtMillis = parseUtcMillis(createdAt)
)

fun SubmissionDto.toDomain() = Submission(
    id = id,
    projectId = projectId,
    versionNumber = versionNumber,
    versionLabel = versionLabel,
    note = submissionNote,
    resultUrl = resultUrl,
    submittedAtMillis = parseUtcMillis(submittedAt),
    files = files.orEmpty().map {
        SubmissionFile(
            id = it.id,
            fileName = it.originalFileName,
            mimeType = it.mimeType,
            sizeBytes = it.fileSizeBytes,
            downloadPath = it.downloadPath ?: "projects/$projectId/submissions/$id/files/${it.id}"
        )
    },
    review = review?.let { r ->
        SubmissionReview(
            decision = r.decision,
            decisionLabel = r.decisionLabel,
            note = r.reviewNote,
            focusAreas = r.focusAreas.orEmpty(),
            items = r.items.orEmpty().map { RevisionItem(it.title, it.detail, it.area) },
            reviewedAtMillis = parseUtcMillis(r.reviewedAt)
        )
    }
)

fun SubmissionListDto.toDomain() = SubmissionList(
    projectId = projectId,
    projectTitle = projectTitle,
    projectStatus = projectStatus,
    projectStatusLabel = projectStatusLabel,
    freelancer = freelancer?.toDomain(),
    revisionCount = revisionCount,
    items = items.orEmpty().map { it.toDomain() }
)

fun ReviewDto.toDomain() = Review(
    id = id,
    projectId = projectId,
    rating = rating,
    text = reviewText,
    highlightLabels = highlights.orEmpty().map { it.label },
    createdAtMillis = parseUtcMillis(createdAt)
)

fun CompletionDto.toDomain() = Completion(
    projectId = projectId,
    projectTitle = projectTitle,
    completedAtMillis = parseUtcMillis(completedAt),
    freelancer = freelancer?.toDomain(),
    grossAmount = payment.grossAmount,
    platformFee = payment.platformFeeAmount,
    freelancerAmount = payment.freelancerAmount,
    transactionNumber = payment.transactionNumber,
    fundStatusLabel = payment.fundStatusLabel,
    review = review?.toDomain()
)

fun FreelancerReviewsDto.toDomain() = FreelancerReviews(
    averageRating = averageRating,
    reviewCount = reviewCount,
    items = items.orEmpty().map {
        PublicReview(
            id = it.id,
            projectTitle = it.projectTitle,
            rating = it.rating,
            text = it.reviewText,
            highlightLabels = it.highlights.orEmpty().map { h -> h.label },
            reviewerName = it.reviewer.name,
            businessName = it.reviewer.businessName,
            createdAtMillis = parseUtcMillis(it.createdAt)
        )
    }
)

fun ConversationDto.toDomain() = Conversation(id, umkmId, freelancerId, projectId, parseUtcMillis(lastMessageAt))

fun MessageWithAttachmentsDto.toDomain() = message.toDomain(attachments.orEmpty().map { it.toDomain(message.conversationId) })

fun MessageAttachmentDto.toDomain(conversationId: String) = MessageAttachment(
    id = id,
    fileName = originalFileName,
    mimeType = mimeType,
    sizeBytes = fileSizeBytes,
    downloadPath = downloadPath ?: "/api/v1/conversations/$conversationId/attachments/$id/file",
    thumbnailPath = thumbnailPath
)

fun MessageDto.toDomain(attachments: List<MessageAttachment> = emptyList()) = Message(
    id = id,
    conversationId = conversationId,
    senderUserId = senderUserId,
    text = messageText.orEmpty(),
    type = messageType,
    sentAtMillis = parseUtcMillis(sentAt) ?: 0L,
    readAtMillis = parseUtcMillis(readAt),
    attachments = attachments
)

fun NotificationDto.toDomain() = AppNotification(
    id = id,
    type = type,
    category = category,
    title = title,
    body = body,
    referenceType = referenceType,
    referenceId = referenceId,
    isRead = isRead,
    createdAtMillis = parseUtcMillis(createdAt)
)
