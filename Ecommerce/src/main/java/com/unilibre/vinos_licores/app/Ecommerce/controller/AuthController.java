package com.unilibre.vinos_licores.app.Ecommerce.controller;

import com.unilibre.vinos_licores.app.Ecommerce.model.User;
import com.unilibre.vinos_licores.app.Ecommerce.security.JwtUtil;
import com.unilibre.vinos_licores.app.Ecommerce.service.AuthService;
import com.unilibre.vinos_licores.app.Ecommerce.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final IUserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        try {
            User user = authService.validateUser(email, password);
            String token = jwtUtil.generateToken(user);

            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {

        try {
            User user = userService.register(
                    body.get("name"),
                    body.get("email"),
                    body.get("role"),
                    body.get("password")
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Usuario registrado correctamente",
                    "user", user
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}