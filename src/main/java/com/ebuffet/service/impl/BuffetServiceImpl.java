package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.buffet.BuffetRequest;
import com.ebuffet.controller.dto.buffet.BuffetResponse;
import com.ebuffet.controller.dto.endereco.EnderecoRequest;
import com.ebuffet.controller.exceptions.ConflictException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.Buffet;
import com.ebuffet.models.Endereco;
import com.ebuffet.models.User;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumUserRole;
import com.ebuffet.repository.BuffetRepository;
import com.ebuffet.repository.UserRepository;
import com.ebuffet.service.BuffetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuffetServiceImpl implements BuffetService {

    private final BuffetRepository repo;
    private final UserRepository userRepo;

    public BuffetServiceImpl(UserRepository userRepo, BuffetRepository repo) {
        this.userRepo = userRepo;
        this.repo = repo;
    }

    @Transactional
    @Override
    public BuffetResponse create(BuffetRequest req) {
        User owner = userRepo.findById(req.getOwnerId())
                .orElseThrow(() -> new NotFoundException("Owner não encontrado"));

        if (owner.getRole() == null || !owner.getRole().equals(EnumUserRole.BUFFET)) {
            throw new ConflictException("Owner informado não possui a role BUFFET.");
        }

        if (repo.existsByOwnerId(owner.getId())) {
            throw new ConflictException("Este owner já possui um Buffet.");
        }

        Buffet b = new Buffet();
        b.setOwner(owner);
        b.setNome(req.getNome().trim());
        b.setTelefone(req.getTelefone());
        b.setEmail(req.getEmail() == null ? null : req.getEmail().trim().toLowerCase());
        b.setStatus(EnumStatus.ATIVO);

        Endereco e = new Endereco();
        EnderecoRequest er = req.getEndereco();
        e.setRua(er.getRua());
        e.setNumero(er.getNumero());
        e.setBairro(er.getBairro());
        e.setCidade(er.getCidade());
        e.setEstado(er.getEstado());
        e.setCep(er.getCep());
        e.setComplemento(er.getComplemento());
        e.setStatus(EnumStatus.ATIVO);
        b.setEndereco(e);

        return new BuffetResponse(repo.save(b));
    }

    @Transactional
    @Override
    public BuffetResponse get(Long id) {
        Buffet b = repo.findById(id).orElseThrow(() -> new NotFoundException("Buffet não encontrado"));
        return new BuffetResponse(b);
    }

    @Transactional
    @Override
    public Page<BuffetResponse> listMine(Long ownerId, Pageable pageable) {
        return repo.findByOwnerId(ownerId, pageable).map(BuffetResponse::new);
    }

    @Transactional
    @Override
    public BuffetResponse update(Long id, BuffetRequest req) {
        Buffet b = repo.findById(id).orElseThrow(() -> new NotFoundException("Buffet não encontrado"));

        if (!b.getOwner().getId().equals(req.getOwnerId())) {
            User newOwner = userRepo.findById(req.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("Novo owner não encontrado"));
            if (newOwner.getRole() == null || !newOwner.getRole().equals(EnumUserRole.BUFFET)) {
                throw new ConflictException("Novo owner não possui role BUFFET.");
            }
            if (repo.existsByOwnerId(newOwner.getId())) {
                throw new ConflictException("Novo owner já possui um Buffet.");
            }
            b.setOwner(newOwner);
        }

        b.setNome(req.getNome().trim());
        b.setTelefone(req.getTelefone());
        b.setEmail(req.getEmail() == null ? null : req.getEmail().trim().toLowerCase());

        Endereco e = b.getEndereco();
        EnderecoRequest er = req.getEndereco();
        e.setRua(er.getRua());
        e.setNumero(er.getNumero());
        e.setBairro(er.getBairro());
        e.setCidade(er.getCidade());
        e.setEstado(er.getEstado());
        e.setCep(er.getCep());
        e.setComplemento(er.getComplemento());

        return new BuffetResponse(b);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Buffet b = repo.findById(id).orElseThrow(() -> new NotFoundException("Buffet não encontrado"));
        b.setStatus(EnumStatus.INATIVO);
        repo.delete(b);
    }
}
