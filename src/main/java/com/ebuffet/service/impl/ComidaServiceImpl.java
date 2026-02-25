package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.comida.ComidaRequest;
import com.ebuffet.controller.dto.comida.ComidaResponse;
import com.ebuffet.controller.exceptions.ForbiddenException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.Arquivo;
import com.ebuffet.models.Buffet;
import com.ebuffet.models.Comida;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumTipoArquivo;
import com.ebuffet.repository.BuffetRepository;
import com.ebuffet.repository.ComidaRepository;
import com.ebuffet.service.ArquivoService;
import com.ebuffet.service.ComidaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ComidaServiceImpl implements ComidaService {

    private final ComidaRepository comidaRepo;
    private final BuffetRepository buffetRepo;
    private final ArquivoService arquivoService;

    public ComidaServiceImpl(ComidaRepository comidaRepo, BuffetRepository buffetRepo, ArquivoService arquivoService) {
        this.comidaRepo = comidaRepo;
        this.buffetRepo = buffetRepo;
        this.arquivoService = arquivoService;
    }

    @Transactional
    @Override
    public ComidaResponse create(Long buffetId, ComidaRequest req, @Nullable MultipartFile imagem, Long ownerId) {
        Buffet b = loadAndCheckOwner(buffetId, ownerId);

        Comida c = new Comida();
        c.setBuffet(b);
        c.setNome(req.getNome().trim());
        c.setDescricao(req.getDescricao());
        c.setCategoria(req.getCategoria());
        c.setStatus(EnumStatus.ATIVO);

        if (imagem != null && !imagem.isEmpty()) {
            try {
                Arquivo arquivo = arquivoService.uploadAndCreateArquivo(imagem, EnumTipoArquivo.COMIDA_IMAGEM);
                c.setImagem(arquivo);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem", e);
            }
        }

        return new ComidaResponse(comidaRepo.save(c));
    }

    @Transactional
    @Override
    public ComidaResponse get(Long id) {
        Comida c = comidaRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Comida não encontrada"));
        return new ComidaResponse(c);
    }

    @Transactional
    @Override
    public Page<ComidaResponse> listByBuffet(Long buffetId,
                                             @Nullable EnumCategoria categoria,
                                             @Nullable EnumStatus status,
                                             Pageable pageable) {
        Page<Comida> page;
        if (categoria != null) {
            page = comidaRepo.findByBuffetIdAndCategoria(buffetId, categoria, pageable);
        } else if (status != null) {
            page = comidaRepo.findByBuffetIdAndStatus(buffetId, status, pageable);
        } else {
            page = comidaRepo.findByBuffetId(buffetId, pageable);
        }
        return page.map(ComidaResponse::new);
    }

    @Transactional
    @Override
    public ComidaResponse update(Long buffetId, Long comidaId, ComidaRequest req, @Nullable MultipartFile imagem, Long ownerId) {
        Buffet b = loadAndCheckOwner(buffetId, ownerId);

        Comida c = comidaRepo.findById(comidaId)
                .orElseThrow(() -> new NotFoundException("Comida não encontrada"));

        if (!c.getBuffet().getId().equals(b.getId())) {
            throw new ForbiddenException("Comida não pertence a este buffet");
        }

        c.setNome(req.getNome().trim());
        c.setDescricao(req.getDescricao());
        c.setCategoria(req.getCategoria());

        if (imagem != null && !imagem.isEmpty()) {
            try {
                Arquivo arquivo = arquivoService.uploadAndCreateArquivo(imagem, EnumTipoArquivo.COMIDA_IMAGEM);
                c.setImagem(arquivo);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem", e);
            }
        }

        return new ComidaResponse(c);
    }

    @Transactional
    @Override
    public void delete(Long buffetId, Long comidaId, Long ownerId, boolean softDelete) {
        Buffet b = loadAndCheckOwner(buffetId, ownerId);

        Comida c = comidaRepo.findById(comidaId)
                .orElseThrow(() -> new NotFoundException("Comida não encontrada"));

        if (!c.getBuffet().getId().equals(b.getId())) {
            throw new ForbiddenException("Comida não pertence a este buffet");
        }

        if (softDelete) {
            c.setStatus(EnumStatus.INATIVO);
        } else {
            comidaRepo.delete(c);
        }
    }

    @Override
    public Buffet loadAndCheckOwner(Long buffetId, Long ownerId) {
        Buffet b = buffetRepo.findById(buffetId)
                .orElseThrow(() -> new NotFoundException("Buffet não encontrado"));
        if (!b.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Você não é o dono deste buffet");
        }
        return b;
    }
}
