package com.devang.abhyudaya.configs;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.devang.abhyudaya.filters.UserProvisioningFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Value("${jwk.set.uri}")
        private String JWK_SET_URI;

        @Value("${jwt.issuer}")
        private String ISSUER;

        @Value("${cors.allowed-origins}")
        private String allowedOrigins;

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        UserProvisioningFilter userProvisioningFilter,
                        JwtAuthConverter jwtAuthConverter) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                                .authorizeHttpRequests(authorize -> authorize
                                                // Public endpoints (most specific first)
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events/published").permitAll() //browse all published events
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events/published/{id}").permitAll() //get particular published event
                                                
                                                // Event endpoints for organizers (specific paths first)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events/{eventId}/staff").hasRole("ORGANIZER") //get assigned event staff
                                                .requestMatchers(HttpMethod.POST, "/api/v1/events/{eventId}/staff").hasRole("ORGANIZER") //assign staff
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events").hasRole("ORGANIZER") //get all types of events
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events/{eventId}").hasRole("ORGANIZER") //get particular type of event
                                                .requestMatchers(HttpMethod.POST, "/api/v1/events").hasRole("ORGANIZER") //create event
                                                .requestMatchers(HttpMethod.PUT, "/api/v1/events/{eventId}").hasRole("ORGANIZER") //update event
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/events/{eventId}").hasRole("ORGANIZER") //delete event

                                                // Ticket endpoints for attendees (specific paths first)
                                                .requestMatchers(HttpMethod.GET, "/api/v1/tickets/{id}/qr-code").hasRole("ATTENDEE") //get qr code for ticket
                                                .requestMatchers(HttpMethod.POST, "/api/v1/tickets/purchase").hasRole("ATTENDEE") //purchase ticket
                                                .requestMatchers(HttpMethod.GET, "/api/v1/tickets").hasRole("ATTENDEE") //get all tickets for user
                                                .requestMatchers(HttpMethod.GET, "/api/v1/tickets/{id}").hasRole("ATTENDEE") //get particular ticket for user
                                                .requestMatchers(HttpMethod.DELETE, "/api/v1/tickets/{id}").hasRole("ATTENDEE") //cancel ticket for user

                                                // Ticket validation endpoints for staff
                                                .requestMatchers(HttpMethod.POST, "/api/v1/ticket-validations").hasRole("STAFF"))
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                                                configurer -> configurer
                                                                .jwtAuthenticationConverter(jwtAuthConverter)
                                                                .decoder(jwtDecoder())))
                                .addFilterAfter(userProvisioningFilter, AuthorizationFilter.class);

                return http.build();
        }

        @Bean
        public JwtDecoder jwtDecoder() {
                NimbusJwtDecoder decoder = NimbusJwtDecoder
                                .withJwkSetUri(JWK_SET_URI)
                                .jwsAlgorithm(SignatureAlgorithm.ES256)
                                .build();

                OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(ISSUER);

                OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<List<String>>("aud",
                                aud -> aud != null && aud.contains("authenticated"));

                decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator));
                return decoder;
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
                configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setExposedHeaders(
                                List.of("Authorization", "Content-Type", "Access-Control-Allow-Origin"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}