package pt.unl.fct.iadi.bookstore.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import pt.unl.fct.iadi.bookstore.controller.dto.BookResponse
import pt.unl.fct.iadi.bookstore.controller.dto.CreateBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.CreateReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.PatchBookRequest
import pt.unl.fct.iadi.bookstore.controller.dto.PatchReviewRequest
import pt.unl.fct.iadi.bookstore.controller.dto.ReviewResponse
import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import pt.unl.fct.iadi.bookstore.service.BookstoreService

@RestController
class BookstoreController(private val service: BookstoreService) : BookstoreAPI {
    private fun Book.toResponse() = BookResponse(isbn, title, author, price, image)
    private fun Review.toResponse() = ReviewResponse(id, rating, comment)

    override fun getAllBooks(): ResponseEntity<*> {
        val books = service.getAllBooks().map { it.toResponse() }
        return ResponseEntity.ok(books)
    }

    override fun createBook(
        @Valid @RequestBody body: CreateBookRequest
    ): ResponseEntity<Unit> {
        val book = service.createBook(body.isbn!!, body.title!!, body.author!!, body.price!!, body.image!!)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{isbn}").buildAndExpand(book.isbn).toUri()
        return ResponseEntity.created(location).build()
    }

    override fun getBookByIsbn(@PathVariable isbn: String): ResponseEntity<*> {
        val book = service.getBookByIsbn(isbn)
        return ResponseEntity.ok(book.toResponse())
    }

    override fun upsertBook(
        @PathVariable isbn: String,
        @Valid @RequestBody body: CreateBookRequest
    ): ResponseEntity<*> {
        val (book, created) = service.upsertBook(isbn, body.title!!, body.author!!, body.price!!, body.image!!)
        return if (created) {
            val location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()
            ResponseEntity.created(location).body(book.toResponse())
        } else {
            ResponseEntity.ok(book.toResponse())
        }
    }

    override fun patchBook(
        @PathVariable isbn: String,
        @Valid @RequestBody body: PatchBookRequest
    ): ResponseEntity<*> {
        val book = service.patchBook(isbn, body.title, body.author, body.price, body.image)
        return ResponseEntity.ok(book.toResponse())
    }

    override fun deleteBook(@PathVariable isbn: String): ResponseEntity<*> {
        service.deleteBook(isbn)
        return ResponseEntity.noContent().build<Unit>()
    }

    override fun getReviews(@PathVariable isbn: String): ResponseEntity<*> {
        val reviews = service.getReviews(isbn).map { it.toResponse() }
        return ResponseEntity.ok(reviews)
    }

    override fun createReview(
        @PathVariable isbn: String,
        @Valid @RequestBody body: CreateReviewRequest
    ): ResponseEntity<*> {
        val review = service.createReview(isbn, body.rating!!, body.comment)
        val location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(review.id).toUri()

        return ResponseEntity.created(location).body(review.toResponse())
    }

    override fun replaceReview(
        @PathVariable isbn: String,
        @PathVariable reviewId: Long,
        @Valid @RequestBody body: CreateReviewRequest
    ): ResponseEntity<*> {
        val review = service.replaceReview(isbn, reviewId, body.rating!!, body.comment)
        return ResponseEntity.ok(review.toResponse())
    }

    override fun patchReview(
        @PathVariable isbn: String,
        @PathVariable reviewId: Long,
        @Valid @RequestBody body: PatchReviewRequest
    ): ResponseEntity<*> {
        val review = service.patchReview(isbn, reviewId, body.rating, body.comment)
        return ResponseEntity.ok(review.toResponse())
    }

    override fun deleteReview(
        @PathVariable isbn: String,
        @PathVariable reviewId: Long
    ): ResponseEntity<*> {
        service.deleteReview(isbn, reviewId)
        return ResponseEntity.noContent().build<Unit>()
    }
}
