package dev.hiwa.iticket.filters;

import dev.hiwa.iticket.domain.entities.User;
import dev.hiwa.iticket.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserProvisioningFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Jwt jwt) {

            UUID supabaseUuid = UUID.fromString(jwt.getSubject());
            if (!userRepository.existsById(supabaseUuid)) {

                User user = new User();
                user.setId(supabaseUuid);
                String name = jwt.getClaim("user_metadata") != null
                        ? ((Map<String, Object>) jwt.getClaim("user_metadata")).get("name").toString()
                        : "Test User";
                user.setName(name);
                user.setEmail(jwt.getClaimAsString("email"));
 
                userRepository.save(user);
            }
        }

        filterChain.doFilter(request, response);
    }
}
