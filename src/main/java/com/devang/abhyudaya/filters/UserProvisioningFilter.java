package com.devang.abhyudaya.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devang.abhyudaya.domains.entities.User;
import com.devang.abhyudaya.repositories.UserRepository;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            UUID supabaseId = UUID.fromString(jwt.getSubject());
            String email = jwt.getClaim("email");
            String username = ((Map<?, ?>) jwt.getClaim("user_metadata")).get("name").toString();

            userRepo.findById(supabaseId)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setId(supabaseId);
                        newUser.setEmail(email);
                        newUser.setName(username);
                        userRepo.save(newUser);
                        log.info("Provisioned new user: {} ({})", username, email);
                        return newUser;
                    });
        }

        filterChain.doFilter(request, response);
    }
}