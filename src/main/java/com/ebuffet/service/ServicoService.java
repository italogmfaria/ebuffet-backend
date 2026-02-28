package com.ebuffet.service;

import com.ebuffet.controller.dto.servico.ServicoRequest;
import com.ebuffet.controller.dto.servico.ServicoResponse;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface ServicoService {

    ServicoResponse create(ServicoRequest req, @Nullable MultipartFile imagem, Long ownerId);

    ServicoResponse get(Long id);

    Page<ServicoResponse> listByBuffet(EnumCategoria categoria, EnumStatus status, String q, Pageable pageable);

    ServicoResponse update(Long id, ServicoRequest req, @Nullable MultipartFile imagem, Long ownerId);

    void delete(Long id, Long ownerId, boolean soft);
}
