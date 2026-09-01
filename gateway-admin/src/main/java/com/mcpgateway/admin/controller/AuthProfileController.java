package com.mcpgateway.admin.controller;

import com.mcpgateway.admin.dto.AuthProfileResponse;
import com.mcpgateway.admin.dto.CreateAuthProfileRequest;
import com.mcpgateway.admin.service.AuthProfileService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth-profiles")
public class AuthProfileController {

    private final AuthProfileService authProfileService;

    public AuthProfileController(AuthProfileService authProfileService) {
        this.authProfileService = authProfileService;
    }

    @GetMapping
    public List<AuthProfileResponse> list() {
        return authProfileService.list();
    }

    @PostMapping
    public AuthProfileResponse create(@Valid @RequestBody CreateAuthProfileRequest request) {
        return authProfileService.create(request);
    }
}
