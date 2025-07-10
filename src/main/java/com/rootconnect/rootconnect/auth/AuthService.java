package com.rootconnect.rootconnect.auth;


import com.rootconnect.rootconnect.dto.AuthResponse;
import com.rootconnect.rootconnect.dto.LoginRequest;
import com.rootconnect.rootconnect.dto.RegisterRequest;
import com.rootconnect.rootconnect.model.User;
import com.rootconnect.rootconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthResponse register(RegisterRequest request){
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }


    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }


}
