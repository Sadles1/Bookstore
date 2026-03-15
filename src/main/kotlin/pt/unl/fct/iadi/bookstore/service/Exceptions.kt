package pt.unl.fct.iadi.bookstore.service

/**
 * Thrown when a book with the given ISBN cannot be found in the store.
 */
class BookNotFoundException(val isbn: String) :
    RuntimeException("Book with ISBN $isbn not found")

/**
 * Thrown when a review with the given ID cannot be found for a particular book.
 */
class ReviewNotFoundException(id: Long) :
    RuntimeException("Review with id $id not found")

/**
 * Thrown when attempting to create a book whose ISBN already exists in the store.
 */
class BookAlreadyExistsException(isbn: String) :
    RuntimeException("Book with ISBN $isbn already exists")

/**
 * Thrown when a business-level validation rule is violated.
 */
class ValidationException(message: String) : RuntimeException(message)
