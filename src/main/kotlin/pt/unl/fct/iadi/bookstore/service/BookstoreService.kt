package pt.unl.fct.iadi.bookstore.service

import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicLong

@Service
class BookstoreService {
    private val books = mutableMapOf<String, Book>()
    private val reviews = mutableMapOf<String, MutableList<Review>>()
    private val reviewIdCounter = AtomicLong(1)

    /**
     * Returns all books currently stored in the bookstore
     */
    fun getAllBooks(): List<Book> = books.values.toList()

    /**
     * Creates a new book
     *
     * @throws BookAlreadyExistsException if a book with the same ISBN already exists
     */
    fun createBook(isbn: String, title: String, author: String, price: Double, image: String): Book {
        if (books.containsKey(isbn))
            throw BookAlreadyExistsException(isbn)

        val book = Book(isbn = isbn, title = title, author = author, price = price, image = image)
        books[isbn] = book
        reviews[isbn] = mutableListOf()

        return book
    }

    /**
     * Returns a book by its ISBN
     *
     * @throws BookNotFoundException if no book with the given ISBN exists
     */
    fun getBookByIsbn(isbn: String): Book =
        books[isbn] ?: throw BookNotFoundException(isbn)

    /**
     * Full replacement of a book
     *
     * Returns a [Pair] where the first element is the persisted [Book] and the second
     * element is `true` if the book was *created*, or `false` if it was *replaced*
     */
    fun upsertBook(isbn: String, title: String, author: String, price: Double, image: String): Pair<Book, Boolean> {
        val created = !books.containsKey(isbn)
        val book = Book(isbn = isbn, title = title, author = author, price = price, image = image)
        books[isbn] = book

        if (created)
            reviews[isbn] = mutableListOf()

        return Pair(book, created)
    }

    /**
     * Partial update of a book (US5). Only non-null fields in the request are applied.
     *
     * @throws BookNotFoundException if no book with the given ISBN exists.
     */
    fun patchBook(isbn: String, title: String?, author: String?, price: Double?, image: String?): Book {
        val book = books[isbn] ?: throw BookNotFoundException(isbn)
        title?.let { book.title = it }
        author?.let { book.author = it }
        price?.let { book.price = it }
        image?.let { book.image = it }

        return book
    }

    /**
     * Deletes a book and all its associated reviews (US6).
     *
     * @throws BookNotFoundException if no book with the given ISBN exists.
     */
    fun deleteBook(isbn: String) {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        books.remove(isbn)
        reviews.remove(isbn)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Review operations
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns all reviews for the given book (US7).
     *
     * @throws BookNotFoundException if no book with the given ISBN exists.
     */
    fun getReviews(isbn: String): List<Review> {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        return reviews[isbn]?.toList() ?: emptyList()
    }

    /**
     * Creates a new review for the given book (US8).
     *
     * @throws BookNotFoundException if no book with the given ISBN exists.
     */
    fun createReview(isbn: String, rating: Int, comment: String?): Review {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        val review = Review(reviewIdCounter.getAndIncrement(), rating, comment)
        reviews.getOrPut(isbn) { mutableListOf() }.add(review)

        return review
    }

    /**
     * Full replacement of a review (US9).
     *
     * @throws BookNotFoundException   if no book with the given ISBN exists.
     * @throws ReviewNotFoundException if no review with the given ID exists for the book.
     */
    fun replaceReview(isbn: String, id: Long, rating: Int, comment: String?): Review {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        val list = reviews[isbn] ?: throw BookNotFoundException(isbn)
        val index = list.indexOfFirst { it.id == id }

        if (index == -1)
            throw ReviewNotFoundException(id)

        val updated = Review(id = id, rating = rating, comment = comment)
        list[index] = updated
        return updated
    }

    /**
     * Partial update of a review (US10). Only non-null fields are applied.
     *
     * @throws BookNotFoundException   if no book with the given ISBN exists.
     * @throws ReviewNotFoundException if no review with the given ID exists for the book.
     */
    fun patchReview(isbn: String, id: Long, rating: Int?, comment: String?): Review {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        val list = reviews[isbn] ?: throw BookNotFoundException(isbn)

        val review = list.find { it.id == id } ?: throw ReviewNotFoundException(id)

        rating?.let { review.rating = it }
        comment?.let { review.comment = it }
        return review
    }

    /**
     * Deletes a review (US11).
     *
     * @throws BookNotFoundException   if no book with the given ISBN exists.
     * @throws ReviewNotFoundException if no review with the given ID exists for the book.
     */
    fun deleteReview(isbn: String, id: Long) {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)
        val list = reviews[isbn] ?: throw BookNotFoundException(isbn)
        val removed = list.removeIf { it.id == id }
        if (!removed) throw ReviewNotFoundException(id)
    }
}
