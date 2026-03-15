package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

// ─────────────────────────────────────────────────────────────────────────────
// Request DTOs
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Request body for creating a new review (US8).
 * [rating] is nullable so that Bean Validation can report a proper error
 * when the field is absent from the JSON payload.
 */
@Schema(description = "Request body for creating or replacing a review")
data class CreateReviewRequest(
    @field:NotNull(message = "Rating must not be null")
    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    @field:Schema(
        description = "Rating score from 1 (worst) to 5 (best)",
        example = "5",
        minimum = "1",
        maximum = "5",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val rating: Int?,

    @field:Size(max = 500, message = "Comment must not exceed 500 characters")
    @field:Schema(
        description = "Optional comment (max 500 characters)",
        example = "An excellent read!",
        maxLength = 500
    )
    val comment: String?
)

/**
 * Request body for a partial update of a review (US10).
 * All fields are optional; only non-null fields will be applied.
 */
@Schema(description = "Request body for partially updating a review. Only provided fields are updated.")
data class PatchReviewRequest(
    @field:Min(value = 1, message = "Rating must be at least 1")
    @field:Max(value = 5, message = "Rating must be at most 5")
    @field:Schema(description = "Rating score from 1 (worst) to 5 (best)", example = "4", minimum = "1", maximum = "5")
    val rating: Int?,

    @field:Size(max = 500, message = "Comment must not exceed 500 characters")
    @field:Schema(
        description = "Optional comment (max 500 characters)",
        example = "Updated opinion after re-reading.",
        maxLength = 500
    )
    val comment: String?
)

// ─────────────────────────────────────────────────────────────────────────────
// Response DTO
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Response representation of a review resource.
 */
@Schema(description = "Review resource representation")
data class ReviewResponse(
    @field:Schema(description = "Auto-generated unique review identifier", example = "1")
    val id: Long,

    @field:Schema(description = "Rating score from 1 (worst) to 5 (best)", example = "5", minimum = "1", maximum = "5")
    val rating: Int,

    @field:Schema(description = "Optional reviewer comment (max 500 characters)", example = "An excellent read!")
    val comment: String?
)
