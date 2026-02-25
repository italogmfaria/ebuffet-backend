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

    EventoResponse getById(Long buffetId, Long id);

    Page<EventoResponse> listByBuffet(Long buffetId,
                                      EnumStatusEvento statusEvento,
                                      EnumStatus status,
                                      LocalDate dataEventoFrom,
                                      LocalDate dataEventoTo,
                                      Pageable pageable);

    void delete(Long buffetId, Long id, Long ownerId, boolean soft);

    DatasIndisponiveisResponse getDatasIndisponiveis(Long buffetId,
                                                        LocalDate dataInicio,
                                                        LocalDate dataFim);

    Page<EventoResponse> listarMeusEventos(Long buffetId, Long clienteId, Pageable pageable);

    EventoResponse updateValor(Long buffetId, Long id, BigDecimal valor, Long ownerId);

    EventoResponse concluirEvento(Long buffetId, Long id, Long ownerId);

    EventoResponse cancelarEvento(Long buffetId, Long id, Long ownerId);

    EventoResponse reverterCancelamentoEvento(Long buffetId, Long id, Long ownerId);

    EventoResponse atualizarEventoPeloCliente(Long buffetId, Long id, Long clienteId, ClienteEventoUpdateRequest req);

    EventoResponse cancelarEventoPeloCliente(Long buffetId, Long id, Long clienteId);

}
