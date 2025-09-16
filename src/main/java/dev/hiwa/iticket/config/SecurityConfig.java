package dev.hiwa.iticket.config;

import dev.hiwa.iticket.filters.UserProvisioningFilter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Value("${jwk.set.uri}")
        private String JWK_SET_URI;

        @Value("${jwt.issuer}")
        private String ISSUER;

        @Value("${cors.allowed-origins}")
        private String allowedOrigins;

        @Autowired
        UserProvisioningFilter userProvisioningFilter;

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        UserProvisioningFilter userProvisioningFilter,
                        JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .cors(cors -> cors.configure(http))
                                // .cors(cors->cors.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/actuator/health").permitAll()
                                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                                .requestMatchers("/swagger-ui.html").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events/published/**")
                                                .permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events/{eventId}/staff")
                                                .hasRole("ORGANIZER")
                                                .requestMatchers(HttpMethod.POST, "/api/v1/events/{eventId}/staff")
                                                .hasRole("ORGANIZER")
                                                .requestMatchers("/api/v1/events/**").hasRole("ORGANIZER")
                                                .requestMatchers("/api/v1/tickets/**").hasRole("ATTENDEE")
                                                .requestMatchers("/api/v1/ticket-validations/**").hasRole("STAFF")
                                                .anyRequest().authenticated())
                                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                                                configurer -> configurer
                                                                .jwtAuthenticationConverter(jwtAuthenticationConverter)
                                                                .decoder(jwtDecoder())))
                                .addFilterAfter(userProvisioningFilter, BearerTokenAuthenticationFilter.class);

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
                configuration.setAllowedOrigins(List.of("*"));
                configuration.setAllowedMethods(List.of("*"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setExposedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }
}