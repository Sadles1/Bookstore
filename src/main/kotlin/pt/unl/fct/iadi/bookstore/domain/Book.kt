package pt.unl.fct.iadi.bookstore.domain

import java.math.BigDecimal

/**
 * @property isbn   Unique identifier
 * @property title  Title of the book (1..120 characters).
 * @property author Author of the book (1..80 characters).
 * @property price  Price of the book
 * @property image  URL to the book cover image.
 */
data class Book(
    val isbn: String,
    var title: String,
    var author: String,
    var price: BigDecimal,
    var image: String
)
