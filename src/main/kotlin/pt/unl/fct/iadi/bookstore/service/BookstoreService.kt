package pt.unl.fct.iadi.bookstore.service

import pt.unl.fct.iadi.bookstore.domain.Book
import pt.unl.fct.iadi.bookstore.domain.Review
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class BookstoreService {
    private val books = mutableMapOf<String, Book>()
    private val reviews = mutableMapOf<String, MutableList<Review>>()
    private val reviewIdCounter = AtomicLong(1)

    fun getAllBooks(): List<Book> = books.values.toList()

    fun createBook(isbn: String, title: String, author: String, price: Double, image: String): Book {
        if (books.containsKey(isbn))
            throw BookAlreadyExistsException(isbn)

        val book = Book(isbn = isbn, title = title, author = author, price = price, image = image)
        books[isbn] = book
        reviews[isbn] = mutableListOf()

        return book
    }

    fun getBookByIsbn(isbn: String): Book =
        books[isbn] ?: throw BookNotFoundException(isbn)

    fun upsertBook(isbn: String, title: String, author: String, price: Double, image: String): Pair<Book, Boolean> {
        val created = !books.containsKey(isbn)
        val book = Book(isbn = isbn, title = title, author = author, price = price, image = image)
        books[isbn] = book

        if (created)
            reviews[isbn] = mutableListOf()

        return Pair(book, created)
    }

    fun patchBook(isbn: String, title: String?, author: String?, price: Double?, image: String?): Book {
        val book = books[isbn] ?: throw BookNotFoundException(isbn)
        title?.let { book.title = it }
        author?.let { book.author = it }
        price?.let { book.price = it }
        image?.let { book.image = it }

        return book
    }

    fun deleteBook(isbn: String) {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        books.remove(isbn)
        reviews.remove(isbn)
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Review operations
    // ─────────────────────────────────────────────────────────────────────────

    fun getReviews(isbn: String): List<Review> {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        return reviews[isbn]?.toList() ?: emptyList()
    }

    fun createReview(isbn: String, rating: Int, comment: String?, author: String): Review {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        val review = Review(reviewIdCounter.getAndIncrement(), rating, comment, author)
        reviews.getOrPut(isbn) { mutableListOf() }.add(review)

        return review
    }

    fun getReviewAuthor(isbn: String, reviewId: Long): String {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        val list = reviews[isbn] ?: throw BookNotFoundException(isbn)
        val review = list.find { it.id == reviewId } ?: throw ReviewNotFoundException(reviewId)
        return review.author
    }

    fun replaceReview(isbn: String, id: Long, rating: Int, comment: String?): Review {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        val list = reviews[isbn] ?: throw BookNotFoundException(isbn)
        val index = list.indexOfFirst { it.id == id }

        if (index == -1)
            throw ReviewNotFoundException(id)

        val originalAuthor = list[index].author
        val updated = Review(id = id, rating = rating, comment = comment, author = originalAuthor)
        list[index] = updated
        return updated
    }

    fun patchReview(isbn: String, id: Long, rating: Int?, comment: String?): Review {
        if (!books.containsKey(isbn))
            throw BookNotFoundException(isbn)

        val list = reviews[isbn] ?: throw BookNotFoundException(isbn)

        val review = list.find { it.id == id } ?: throw ReviewNotFoundException(id)

        rating?.let { review.rating = it }
        comment?.let { review.comment = it }
        return review
    }

    fun deleteReview(isbn: String, id: Long) {
        if (!books.containsKey(isbn)) throw BookNotFoundException(isbn)
        val list = reviews[isbn] ?: throw BookNotFoundException(isbn)
        val removed = list.removeIf { it.id == id }
        if (!removed) throw ReviewNotFoundException(id)
    }
}
