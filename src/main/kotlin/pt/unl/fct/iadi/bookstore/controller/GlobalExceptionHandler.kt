package pt.unl.fct.iadi.bookstore.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse
import pt.unl.fct.iadi.bookstore.service.BookAlreadyExistsException
import pt.unl.fct.iadi.bookstore.service.BookNotFoundException
import pt.unl.fct.iadi.bookstore.service.ReviewNotFoundException
import pt.unl.fct.iadi.bookstore.service.ValidationException

@RestControllerAdvice
class GlobalExceptionHandler {

    private fun resolveLanguage(acceptLanguage: String?): String {
        if (acceptLanguage.isNullOrBlank()) return "en"
        val primary = acceptLanguage.split(",").first().trim().lowercase()
        return if (primary.startsWith("pt")) "pt" else "en"
    }

    /**
     * Handles 404 - book not found. Returns a localized message based on Accept-Language.
     */
    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(ex: BookNotFoundException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        val lang = resolveLanguage(request.getHeader("Accept-Language"))
        val message = if (lang == "pt")
            "Livro com ISBN ${ex.isbn} não encontrado"
        else
            ex.message ?: "Book not found"

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .header("Content-Language", lang)
            .body(ErrorResponse(error = "NOT_FOUND", message = message))
    }

    /**
     * Handles 404 - review not found.
     */
    @ExceptionHandler(ReviewNotFoundException::class)
    fun handleReviewNotFound(ex: ReviewNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(error = "NOT_FOUND", message = ex.message ?: "Review not found"))

    /**
     * Handles 409 - duplicate ISBN on book creation.
     */
    @ExceptionHandler(BookAlreadyExistsException::class)
    fun handleBookAlreadyExists(ex: BookAlreadyExistsException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(error = "CONFLICT", message = ex.message ?: "Book already exists"))

    /**
     * Handles 400 - business-level validation failure.
     */
    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(error = "BAD_REQUEST", message = ex.message ?: "Validation error"))

    /**
     * Handles 400 - Arguments Validation failure
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val details = ex.bindingResult.fieldErrors
            .joinToString("; ") { "${it.field}: ${it.defaultMessage}" }
        val message = if (details.isNotBlank()) details else "Validation failed"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(error = "VALIDATION_ERROR", message = message))
    }
}
