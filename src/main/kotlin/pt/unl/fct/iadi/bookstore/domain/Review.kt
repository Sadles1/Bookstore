package pt.unl.fct.iadi.bookstore.domain

/**
 * Domain entity representing a review for a book.
 *
 * @property id      Auto-generated unique identifier for the review.
 * @property rating  Rating score from 1 (worst) to 5 (best).
 * @property comment Optional comment left by the reviewer (max 500 characters).
 */
data class Review(
    val id: Long,
    var rating: Int,
    var comment: String?
)
