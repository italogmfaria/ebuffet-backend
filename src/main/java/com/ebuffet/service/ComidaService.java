package com.ebuffet.service;

import com.ebuffet.controller.dto.comida.ComidaRequest;
import com.ebuffet.controller.dto.comida.ComidaResponse;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface ComidaService {

    ComidaResponse create(ComidaRequest req, @Nullable MultipartFile imagem, Long ownerId);

    ComidaResponse get(Long id);

    Page<ComidaResponse> listByBuffet(@Nullable EnumCategoria categoria,
                                      @Nullable EnumStatus status,
                                      Pageable pageable);

    ComidaResponse update(Long comidaId, ComidaRequest req, @Nullable MultipartFile imagem, Long ownerId);

    void delete(Long comidaId, Long ownerId, boolean softDelete);
}
