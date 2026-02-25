package com.ebuffet.service.impl;

import com.ebuffet.controller.dto.servico.ServicoRequest;
import com.ebuffet.controller.dto.servico.ServicoResponse;
import com.ebuffet.controller.exceptions.ConflictException;
import com.ebuffet.controller.exceptions.NotFoundException;
import com.ebuffet.models.Arquivo;
import com.ebuffet.models.Buffet;
import com.ebuffet.models.Servico;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumTipoArquivo;
import com.ebuffet.repository.BuffetRepository;
import com.ebuffet.repository.ReservaRepository;
import com.ebuffet.repository.ServicoRepository;
import com.ebuffet.service.ArquivoService;
import com.ebuffet.service.ServicoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ServicoServiceImpl implements ServicoService {

    private final ServicoRepository servicoRepo;
    private final BuffetRepository buffetRepo;
    private final ReservaRepository reservaRepo;
    private final ArquivoService arquivoService;

    public ServicoServiceImpl(ServicoRepository servicoRepo, BuffetRepository buffetRepo, ReservaRepository reservaRepo, ArquivoService arquivoService) {
        this.servicoRepo = servicoRepo;
        this.buffetRepo = buffetRepo;
        this.reservaRepo = reservaRepo;
        this.arquivoService = arquivoService;
    }

    @Transactional
    @Override
    public ServicoResponse create(Long buffetId, ServicoRequest req, @Nullable MultipartFile imagem, Long ownerId) {
        Buffet b = buffetRepo.findById(buffetId).orElseThrow(() -> new NotFoundException("Buffet não encontrado"));

        Servico s = new Servico();
        s.setNome(req.getNome());
        s.setDescricao(req.getDescricao());
        s.setCategoria(req.getCategoria());
        s.setBuffet(b);
        s.setStatus(req.getStatus());

        if (imagem != null && !imagem.isEmpty()) {
            try {
                Arquivo arquivo = arquivoService.uploadAndCreateArquivo(imagem, EnumTipoArquivo.SERVICO_IMAGEM);
                s.setImagem(arquivo);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem", e);
            }
        }

        s = servicoRepo.save(s);
        return new ServicoResponse(s);
    }

    @Transactional(readOnly = true)
    @Override
    public ServicoResponse get(Long id) {
        Servico s = servicoRepo.findById(id).orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
        return new ServicoResponse(s);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ServicoResponse> listByBuffet(Long buffetId, EnumCategoria categoria, EnumStatus status, String q, Pageable pageable) {
        return servicoRepo.findByFilters(buffetId, categoria, status, q, pageable)
                .map(ServicoResponse::new);
    }

    @Transactional
    @Override
    public ServicoResponse update(Long buffetId, Long id, ServicoRequest req, @Nullable MultipartFile imagem, Long ownerId) {
        Servico s = servicoRepo.findById(id).orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
        if (!s.getBuffet().getId().equals(buffetId))
            throw new ConflictException("Serviço não pertence ao buffet informado");

        s.setNome(req.getNome());
        s.setDescricao(req.getDescricao());
        s.setCategoria(req.getCategoria());
        s.setStatus(req.getStatus());

        if (imagem != null && !imagem.isEmpty()) {
            try {
                Arquivo arquivo = arquivoService.uploadAndCreateArquivo(imagem, EnumTipoArquivo.SERVICO_IMAGEM);
                s.setImagem(arquivo);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem", e);
            }
        }

        return new ServicoResponse(servicoRepo.save(s));
    }

    @Transactional
    @Override
    public void delete(Long buffetId, Long id, Long ownerId, boolean soft) {
        Servico s = servicoRepo.findById(id).orElseThrow(() -> new NotFoundException("Serviço não encontrado"));
        if (!s.getBuffet().getId().equals(buffetId))
            throw new ConflictException("Serviço não pertence ao buffet informado");

        if (soft) {
            s.setStatus(EnumStatus.INATIVO);
            servicoRepo.save(s);
        } else {
            servicoRepo.delete(s);
        }
    }
}
