package pt.unl.fct.iadi.bookstore.security

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@SecuritySchemes(
    SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
    ),
    SecurityScheme(
        name = "apiToken",
        type = SecuritySchemeType.APIKEY,
        `in` = SecuritySchemeIn.HEADER,
        paramName = "X-Api-Token"
    )
)
class SecurityConfig {

    companion object {
        private const val BOOKS_PATTERN = "/books/**"
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder): UserDetailsService {
        val editor1 = User.builder()
            .username("editor1")
            .password(passwordEncoder.encode("editor1pass"))
            .roles("EDITOR")
            .build()
        val editor2 = User.builder()
            .username("editor2")
            .password(passwordEncoder.encode("editor2pass"))
            .roles("EDITOR")
            .build()
        val admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("adminpass"))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(editor1, editor2, admin)
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        apiTokenFilter: ApiTokenFilter,
        authenticationEntryPoint: AuthenticationEntryPoint,
        accessDeniedHandler: AccessDeniedHandler
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .addFilterBefore(apiTokenFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authorizeHttpRequests { auth ->
                auth
                    // OpenAPI docs and Swagger UI are publicly accessible
                    .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                    // GET requests only require a valid API token (no Basic Auth)
                    .requestMatchers(HttpMethod.GET, BOOKS_PATTERN).permitAll()
                    // Write operations require authentication
                    .requestMatchers(HttpMethod.POST, BOOKS_PATTERN).authenticated()
                    .requestMatchers(HttpMethod.PUT, BOOKS_PATTERN).authenticated()
                    .requestMatchers(HttpMethod.PATCH, BOOKS_PATTERN).authenticated()
                    .requestMatchers(HttpMethod.DELETE, BOOKS_PATTERN).authenticated()
                    .anyRequest().authenticated()
            }
            .httpBasic { it.authenticationEntryPoint(authenticationEntryPoint) }
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
                it.accessDeniedHandler(accessDeniedHandler)
            }

        return http.build()
    }
}
