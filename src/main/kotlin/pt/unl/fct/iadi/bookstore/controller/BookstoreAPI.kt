package pt.unl.fct.iadi.bookstore.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import pt.unl.fct.iadi.bookstore.controller.dto.BookResponse
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse
import pt.unl.fct.iadi.bookstore.controller.dto.PatchBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.PatchReviewRequest


@RequestMapping("/books")
@Tag(name = "Books", description = "Operations for managing books and their reviews")
interface BookstoreAPI {
    @Operation(summary = "List all books")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Successfully retrieved list of books")
    )
    @GetMapping(produces = ["application/json"])
    fun getAllBooks(): ResponseEntity<*>

    @Operation(summary = "Create a new book", operationId = "createBook")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Book created successfully"),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "409", description = "Book with this ISBN already exists",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PostMapping(consumes = ["application/json"], produces = ["application/json"])
    fun createBook(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Book to create", required = true)
        @RequestBody body: CreateBookRequest
    ): ResponseEntity<*>

    @Operation(summary = "Get a book by ISBN", operationId = "getBookByIsbn")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Book found",
            content = [Content(schema = Schema(implementation = BookResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @GetMapping("/{isbn}", produces = ["application/json"])
    fun getBookByIsbn(
        @Parameter(description = "ISBN of the book to retrieve", required = true)
        @PathVariable isbn: String
    ): ResponseEntity<*>

    @Operation(summary = "Upsert a book")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Book replaced successfully"),
        ApiResponse(responseCode = "201", description = "Book created successfully"),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PutMapping("/{isbn}", consumes = ["application/json"], produces = ["application/json"])
    fun upsertBook(
        @Parameter(required = true)
        @PathVariable isbn: String,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Full book representation", required = true)
        @RequestBody body: CreateBookRequest
    ): ResponseEntity<*>

    @Operation(summary = "Partially update a book",)
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Book updated successfully"),
        ApiResponse(
            responseCode = "400", description = "Validation error",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PatchMapping("/{isbn}", consumes = ["application/json"], produces = ["application/json"])
    fun patchBook(
        @Parameter(description = "ISBN of the book to update", required = true)
        @PathVariable isbn: String,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to update", required = true)
        @RequestBody body: PatchBookRequest
    ): ResponseEntity<*>

    @Operation(summary = "Delete a book")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Book deleted successfully"),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @DeleteMapping("/{isbn}")
    fun deleteBook(
        @Parameter(required = true)
        @PathVariable isbn: String
    ): ResponseEntity<*>

    @Operation(summary = "List reviews for a book")
    @ApiResponses(
        ApiResponse(responseCode = "200"),
        ApiResponse(responseCode = "404", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    )
    @GetMapping("/{isbn}/reviews", produces = ["application/json"])
    fun getReviews(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String
    ): ResponseEntity<*>

    @Operation(summary = "Create a review for a book")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Review created successfully"),
        ApiResponse(
            responseCode = "400", description = "Validation error - invalid request body",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PostMapping("/{isbn}/reviews", consumes = ["application/json"], produces = ["application/json"])
    fun createReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Review to create", required = true)
        @RequestBody body: CreateReviewRequest
    ): ResponseEntity<*>

    @Operation(summary = "Replace a review",)
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Review replaced successfully"),
        ApiResponse(
            responseCode = "400", description = "Validation error - invalid request body",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PutMapping("/{isbn}/reviews/{reviewId}", consumes = ["application/json"], produces = ["application/json"])
    fun replaceReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Parameter(description = "ID of the review to replace", required = true)
        @PathVariable reviewId: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Full review representation",
            required = true
        )
        @RequestBody body: CreateReviewRequest
    ): ResponseEntity<*>

    @Operation(summary = "Partially update a review")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Review updated successfully"),
        ApiResponse(
            responseCode = "400", description = "Validation error - invalid field value",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "404", description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @PatchMapping("/{isbn}/reviews/{reviewId}", consumes = ["application/json"], produces = ["application/json"])
    fun patchReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Parameter(description = "ID of the review to update", required = true)
        @PathVariable reviewId: Long,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Fields to update", required = true)
        @RequestBody body: PatchReviewRequest
    ): ResponseEntity<*>

    @Operation(summary = "Delete a review")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Review deleted successfully"),
        ApiResponse(
            responseCode = "404", description = "Book or review not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    @DeleteMapping("/{isbn}/reviews/{reviewId}")
    fun deleteReview(
        @Parameter(description = "ISBN of the book", required = true)
        @PathVariable isbn: String,
        @Parameter(description = "ID of the review to delete", required = true)
        @PathVariable reviewId: Long
    ): ResponseEntity<*>
}
