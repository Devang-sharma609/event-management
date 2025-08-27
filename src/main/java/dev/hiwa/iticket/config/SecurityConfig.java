package dev.hiwa.iticket.config;

import dev.hiwa.iticket.filters.UserProvisioningFilter;

import java.util.List;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private static final String JWK_SET_URI = "https://sgwkisydeybuvgbsxxsp.supabase.co/auth/v1/.well-known/jwks.json";
        private static final String ISSUER = "https://sgwkisydeybuvgbsxxsp.supabase.co/auth/v1";

        @Bean
        public SecurityFilterChain filterChain(
                        HttpSecurity http,
                        UserProvisioningFilter userProvisioningFilter,
                        JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/swagger-ui/**").permitAll()
                                                .requestMatchers("/swagger-ui.html").permitAll()
                                                .requestMatchers("/v3/api-docs/**").permitAll()
                                                .requestMatchers(HttpMethod.GET, "/api/v1/events/published/**")
                                                .permitAll()
                                                .requestMatchers("/api/v1/events/**").hasRole("ORGANIZER")
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
}