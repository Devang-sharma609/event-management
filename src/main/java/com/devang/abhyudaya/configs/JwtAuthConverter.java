package com.devang.abhyudaya.configs;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthConverter implements Converter<Jwt, JwtAuthenticationToken> {

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Object userMetadata = jwt.getClaim("user_metadata");

        if (!(userMetadata instanceof java.util.Map)) {
            return Collections.emptyList();
        }

        Object roleClaim = ((java.util.Map<?, ?>) userMetadata).get("role");

        if (roleClaim == null) {
            return Collections.emptyList();
        }

        List<String> roles;
        if (roleClaim instanceof String) {
            roles = List.of((String) roleClaim);
        } else if (roleClaim instanceof List) {
            roles = ((List<?>) roleClaim).stream()
                    .filter(item -> item instanceof String)
                    .map(item -> (String) item)
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
