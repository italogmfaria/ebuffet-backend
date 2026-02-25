package com.ebuffet.service;

import com.ebuffet.controller.dto.comida.ComidaRequest;
import com.ebuffet.controller.dto.comida.ComidaResponse;
import com.ebuffet.models.Buffet;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public interface ComidaService {

    ComidaResponse create(Long buffetId, ComidaRequest req, @Nullable MultipartFile imagem, Long ownerId);

    ComidaResponse get(Long id);

    Page<ComidaResponse> listByBuffet(Long buffetId,
                                      @Nullable EnumCategoria categoria,
                                      @Nullable EnumStatus status,
                                      Pageable pageable);

    ComidaResponse update(Long buffetId, Long comidaId, ComidaRequest req, @Nullable MultipartFile imagem, Long ownerId);

    void delete(Long buffetId, Long comidaId, Long ownerId, boolean softDelete);

    Buffet loadAndCheckOwner(Long buffetId, Long ownerId);
}
