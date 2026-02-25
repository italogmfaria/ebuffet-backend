package com.ebuffet.service;

import com.ebuffet.controller.dto.register.RegisterRequest;
import com.ebuffet.controller.dto.user.UpdateUserRequest;
import com.ebuffet.models.User;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends UserDetailsService {
    User register(RegisterRequest req);

    User registerBuffet(RegisterRequest req);

    User findEntityByUsername(String username, Long buffetId);

    UserDetails loadUserByUsernameAndBuffetId(String username, Long buffetId) throws UsernameNotFoundException;

    User updateUser(Long userId, Long buffetId, UpdateUserRequest req, @Nullable MultipartFile foto);
}
