package pt.unl.fct.iadi.bookstore.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import pt.unl.fct.iadi.bookstore.controller.dto.ErrorResponse

@Configuration
class SecurityExceptionHandlerConfig {

    @Bean
    fun customAuthenticationEntryPoint(objectMapper: ObjectMapper): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { _, response, _ ->
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(error = "UNAUTHORIZED", message = "Missing or invalid X-Api-Token")
                )
            )
        }
    }

    @Bean
    fun customAccessDeniedHandler(objectMapper: ObjectMapper): AccessDeniedHandler {
        return AccessDeniedHandler { _, response, _ ->
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(
                objectMapper.writeValueAsString(
                    ErrorResponse(error = "FORBIDDEN", message = "Missing or invalid X-Api-Token")
                )
            )
        }
    }
}
