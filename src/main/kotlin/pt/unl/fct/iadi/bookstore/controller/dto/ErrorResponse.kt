package pt.unl.fct.iadi.bookstore.controller.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Consistent error payload returned by all API error responses.
 */
@Schema(description = "Error response payload")
data class ErrorResponse(
    @field:Schema(description = "Machine-readable error identifier", example = "NOT_FOUND")
    val error: String,

    @field:Schema(description = "Human-readable description of the error", example = "Book with ISBN 9780134685991 not found")
    val message: String
)
