package pt.unl.fct.iadi.bookstore.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse

@Component
class ApiTokenFilter(
    private val objectMapper: ObjectMapper,
    private val registry: ApiTokenRegistry
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(ApiTokenFilter::class.java)

    private val publicPaths = listOf(
        "/swagger-ui", "/v3/api-docs"
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.requestURI

        // Skip token check for OpenAPI documentation endpoints
        if (publicPaths.any { path.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = request.getHeader("X-Api-Token")

        if (token == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(error = "UNAUTHORIZED", message = "Missing or invalid X-Api-Token")
                )
            )
            return
        }

        val appName = registry.tokenToApp[token]

        if (appName == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(error = "UNAUTHORIZED", message = "Missing or invalid X-Api-Token")
                )
            )
            return
        }

        request.setAttribute("appName", appName)

        filterChain.doFilter(request, response)
    }
}
