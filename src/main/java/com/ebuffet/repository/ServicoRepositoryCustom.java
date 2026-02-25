package com.ebuffet.repository;

import com.ebuffet.models.Servico;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServicoRepositoryCustom {

    Page<Servico> findByFilters(Long buffetId,
                                EnumCategoria categoria,
                                EnumStatus status,
                                String searchQuery,
                                Pageable pageable);
}
