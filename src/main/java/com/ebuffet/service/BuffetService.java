package com.ebuffet.service;

import com.ebuffet.controller.dto.buffet.BuffetRequest;
import com.ebuffet.controller.dto.buffet.BuffetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BuffetService {

    BuffetResponse create(BuffetRequest req);

    BuffetResponse get(Long id);

    Page<BuffetResponse> listMine(Long ownerId, Pageable pageable);

    BuffetResponse update(Long id, BuffetRequest req);

    void delete(Long id);
}
