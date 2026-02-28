package com.ebuffet.service;

import com.ebuffet.controller.dto.login.AuthRequest;
import com.ebuffet.controller.dto.login.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);
}
