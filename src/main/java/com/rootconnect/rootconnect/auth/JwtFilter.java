package com.rootconnect.rootconnect.auth;

import com.rootconnect.rootconnect.model.User;
import com.rootconnect.rootconnect.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🔍 JwtFilter: Running filter...");
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⛔ No Bearer token found.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("📩 Email extracted from token: " + email);
            User user = userRepo.findByEmail(email).orElse(null);

            if (user != null && jwtUtil.validateToken(token, user)) {
                System.out.println("✅ Token valid. Setting auth for: " + email);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities() // ✅ Your User now implements UserDetails
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                System.out.println("✅ Auth token set for user: " + user.getEmail());
                System.out.println("✅ Principal class: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass());
            } else {
                System.out.println("❌ Invalid token or user not found.");
            }
        } else {
            System.out.println("⚠️ Token already authenticated or email is null.");
        }

        filterChain.doFilter(request, response);
    }
}
