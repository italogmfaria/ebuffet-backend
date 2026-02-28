package com.ebuffet.service;

import com.ebuffet.controller.dto.calendario.DatasIndisponiveisResponse;
import com.ebuffet.controller.dto.evento.ClienteEventoUpdateRequest;
import com.ebuffet.controller.dto.evento.EventoResponse;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface EventoService {

    EventoResponse getById(Long id);

    Page<EventoResponse> listByBuffet(EnumStatusEvento statusEvento,
                                      EnumStatus status,
                                      LocalDate dataEventoFrom,
                                      LocalDate dataEventoTo,
                                      Pageable pageable);

    void delete(Long id, Long ownerId, boolean soft);

    DatasIndisponiveisResponse getDatasIndisponiveis(LocalDate dataInicio,
                                                        LocalDate dataFim);

    Page<EventoResponse> listarMeusEventos(Long clienteId, Pageable pageable);

    EventoResponse updateValor(Long id, BigDecimal valor, Long ownerId);

    EventoResponse concluirEvento(Long id, Long ownerId);

    EventoResponse cancelarEvento(Long id, Long ownerId);

    EventoResponse reverterCancelamentoEvento(Long id, Long ownerId);

    EventoResponse atualizarEventoPeloCliente(Long id, Long clienteId, ClienteEventoUpdateRequest req);

    EventoResponse cancelarEventoPeloCliente(Long id, Long clienteId);

}
