package com.rms.restaurant_management_system.auth.service;

import com.rms.restaurant_management_system.auth.dto.TokenResponse;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.user.model.User;
import com.rms.restaurant_management_system.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    @Value("${security.jwt.expiration-seconds}")
    private long expirationSeconds;

    public TokenResponse issueToken(Authentication authentication) {
        User user = userRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(expirationSeconds);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("restaurant-management-system")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .subject(user.getLogin())
                .claim("userId", user.getId())
                .claim("fullName", user.getFullName())
                .claim("roles", List.of("ROLE_" + user.getRole().name()))
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new TokenResponse(token, "Bearer", expirationSeconds, user.getId(), user.getLogin(),
                user.getFullName(), user.getRole());
    }
}