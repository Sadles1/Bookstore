package pt.unl.fct.iadi.bookstore

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@OpenAPIDefinition(
    info = Info(
        title = "Bookstore API",
        version = "1.0.0",
        description = "A RESTful API for managing books and their reviews"
    )
)
@SpringBootApplication class BookstoreApplication

fun main(args: Array<String>) {
    runApplication<BookstoreApplication>(*args)
}
