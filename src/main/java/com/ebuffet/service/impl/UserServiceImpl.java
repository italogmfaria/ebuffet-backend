package com.ebuffet.service.impl;

import com.ebuffet.config.SingleBuffetProperties;
import com.ebuffet.controller.dto.register.RegisterRequest;
import com.ebuffet.controller.dto.user.UpdateUserRequest;
import com.ebuffet.controller.exceptions.ConflictException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.Arquivo;
import com.ebuffet.models.User;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumTipoArquivo;
import com.ebuffet.models.enums.EnumUserRole;
import com.ebuffet.repository.BuffetRepository;
import com.ebuffet.repository.UserRepository;
import com.ebuffet.service.ArquivoService;
import com.ebuffet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private BuffetRepository buffetRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private SingleBuffetProperties singleBuffetProperties;

    @Transactional
    @Override
    public User register(RegisterRequest req) {
        Long buffetId = singleBuffetProperties.getBuffetId();
        if (!buffetRepository.existsById(buffetId)) {
            throw new IllegalArgumentException("Buffet não encontrado");
        }

        User u = buildUserFromRequest(req);
        u.setRole(EnumUserRole.CLIENTE);
        u.setBuffetId(buffetId);
        return repository.save(u);
    }

    @Transactional
    @Override
    public User registerBuffet(RegisterRequest req) {
        Long buffetId = singleBuffetProperties.getBuffetId();
        if (!buffetRepository.existsById(buffetId)) {
            throw new IllegalArgumentException("Buffet não encontrado");
        }

        User u = buildUserFromRequest(req);
        u.setRole(EnumUserRole.BUFFET);
        u.setBuffetId(buffetId);
        return repository.save(u);
    }

    private User buildUserFromRequest(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        String telefone = req.getTelefone() != null ? req.getTelefone().replaceAll("\\D+", "") : null;

        if (repository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("E-mail já está em uso.");
        }

        if (telefone != null && repository.existsByTelefone(telefone)) {
            throw new ConflictException("Telefone já está em uso.");
        }

        User u = new User();
        u.setNome(req.getNome().trim());
        u.setEmail(email);
        u.setTelefone(telefone);
        u.setSenha(encoder.encode(req.getSenha()));
        u.setStatus(EnumStatus.ATIVO);
        return u;
    }

    @Override
    public User findEntityByUsername(String username) {
        return repository.findByEmailIgnoreCase(username)
                .or(() -> repository.findByTelefone(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByEmailIgnoreCase(username)
                .or(() -> repository.findByTelefone(username))
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }

    @Transactional
    @Override
    public User updateUser(Long userId, UpdateUserRequest req, @Nullable MultipartFile foto) {
        User user = repository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        String email = req.getEmail().trim().toLowerCase();
        String telefone = req.getTelefone() != null ? req.getTelefone().replaceAll("\\D+", "") : null;

        if (!user.getEmail().equalsIgnoreCase(email) &&
                repository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("E-mail já está em uso");
        }

        if (telefone != null && !telefone.equals(user.getTelefone()) &&
                repository.existsByTelefone(telefone)) {
            throw new ConflictException("Telefone já está em uso");
        }

        user.setNome(req.getNome().trim());
        user.setEmail(email);
        user.setTelefone(telefone);

        if (foto != null && !foto.isEmpty()) {
            try {
                Arquivo arquivo = arquivoService.uploadAndCreateArquivo(foto, EnumTipoArquivo.CLIENTE_FOTO);
                user.setFoto(arquivo);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da foto de perfil", e);
            }
        }

        return repository.save(user);
    }
}
