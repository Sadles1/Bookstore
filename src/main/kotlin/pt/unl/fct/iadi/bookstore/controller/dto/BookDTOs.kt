package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

// ─────────────────────────────────────────────────────────────────────────────
// Request DTOs
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Request body for creating a new book (US2).
 * All fields are nullable so that Bean Validation can produce proper error messages
 * when a field is missing from the JSON payload.
 */
@Schema(description = "Request body for creating or replacing a book")
data class CreateBookRequest(
    @field:NotBlank(message = "ISBN must not be blank")
    @field:Schema(
        description = "Unique ISBN identifier",
        example = "9780132350884",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val isbn: String?,

    @field:NotBlank(message = "Title must not be blank")
    @field:Size(min = 1, max = 120, message = "Title must be between 1 and 120 characters")
    @field:Schema(
        description = "Title of the book (1–120 characters)",
        example = "Clean Code",
        minLength = 1,
        maxLength = 120,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val title: String?,

    @field:NotBlank(message = "Author must not be blank")
    @field:Size(min = 1, max = 80, message = "Author must be between 1 and 80 characters")
    @field:Schema(
        description = "Author of the book (1–80 characters)",
        example = "Robert C. Martin",
        minLength = 1,
        maxLength = 80,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val author: String?,

    @field:NotNull(message = "Price must not be null")
    @field:Positive
    @field:Schema(
        description = "Price of the book, must be greater than 0",
        example = "34.99",
        minimum = "0",
        exclusiveMinimum = true,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val price: Double?,

    @field:NotBlank(message = "Image URL must not be blank")
    @field:Pattern(regexp = "^https?://.*", message = "Image must be a valid HTTP or HTTPS URL")
    @field:Schema(
        description = "URL to the book cover image (must be a valid HTTP/HTTPS URL)",
        example = "https://example.com/images/clean-code.jpg",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val image: String?
)

/**
 * Request body for a partial update of a book (US5).
 * All fields are optional; only non-null fields will be applied.
 */
@Schema(description = "Request body for partially updating a book. Only provided fields are updated.")
data class PatchBookRequest(
    @field:Size(min = 1, max = 120, message = "Title must be between 1 and 120 characters")
    @field:Schema(
        description = "Title of the book (1–120 characters)",
        example = "Clean Code",
        minLength = 1,
        maxLength = 120
    )
    val title: String?,

    @field:Size(min = 1, max = 80, message = "Author must be between 1 and 80 characters")
    @field:Schema(
        description = "Author of the book (1–80 characters)",
        example = "Robert C. Martin",
        minLength = 1,
        maxLength = 80
    )
    val author: String?,

    @field:Positive
    @field:Schema(
        description = "Price of the book, must be greater than 0",
        example = "29.99",
        minimum = "0",
        exclusiveMinimum = true
    )
    val price: Double?,

    @field:Pattern(regexp = "^https?://.*", message = "Image must be a valid HTTP or HTTPS URL")
    @field:Schema(
        description = "URL to the book cover image (must be a valid HTTP/HTTPS URL)",
        example = "https://example.com/images/clean-code.jpg"
    )
    val image: String?
)

// ─────────────────────────────────────────────────────────────────────────────
// Response DTO
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Response representation of a book resource.
 */
@Schema(description = "Book resource representation")
data class BookResponse(
    @field:NotBlank(message = "ISBN must not be blank")
    @field:Schema(
        description = "Unique ISBN identifier",
        example = "9780132350884",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val isbn: String,

    @field:NotBlank(message = "Title must not be blank")
    @field:Size(min = 1, max = 120, message = "Title must be between 1 and 120 characters")
    @field:Schema(
        description = "Title of the book (1–120 characters)",
        example = "Clean Code",
        minLength = 1,
        maxLength = 120,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val title: String,

    @field:NotBlank(message = "Author must not be blank")
    @field:Size(min = 1, max = 80, message = "Author must be between 1 and 80 characters")
    @field:Schema(
        description = "Author of the book (1–80 characters)",
        example = "Robert C. Martin",
        minLength = 1,
        maxLength = 80,
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    val author: String,

    @field:Positive
    @field:Schema(
        description = "Price of the book, must be greater than 0",
        example = "29.99",
        minimum = "0",
        exclusiveMinimum = true
    )
    val price: Double,

    @field:Pattern(regexp = "^https?://.*", message = "Image must be a valid HTTP or HTTPS URL")
    @field:Schema(
        description = "URL to the book cover image (must be a valid HTTP/HTTPS URL)",
        example = "https://example.com/images/clean-code.jpg"
    )
    val image: String
)
