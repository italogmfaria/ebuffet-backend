package com.ebuffet.utils.schedules;

import com.ebuffet.models.Evento;
import com.ebuffet.models.Reserva;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import com.ebuffet.models.enums.EnumStatusReserva;
import com.ebuffet.repository.EventoRepository;
import com.ebuffet.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class ExpiracaoScheduler {

    private static final Logger log = LoggerFactory.getLogger(ExpiracaoScheduler.class);

    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;

    public ExpiracaoScheduler(ReservaRepository reservaRepository, EventoRepository eventoRepository) {
        this.reservaRepository = reservaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void executar() {
        log.info("[ExpiracaoScheduler] Iniciando execução diária de expiração...");

        expirarReservas();
        concluirEventos();

        log.info("[ExpiracaoScheduler] Execução concluída.");
    }


    private void expirarReservas() {
        LocalDate hoje = LocalDate.now();

        List<Reserva> reservas = reservaRepository.findReservasExpiradas(
                EnumStatusReserva.PENDENTE,
                EnumStatus.ATIVO,
                hoje
        );

        if (reservas.isEmpty()) {
            log.info("[ExpiracaoScheduler] Nenhuma reserva pendente expirada.");
            return;
        }

        for (Reserva r : reservas) {
            r.setStatusReserva(EnumStatusReserva.CANCELADA);
            r.setStatus(EnumStatus.INATIVO);
            log.info("[ExpiracaoScheduler] Reserva #{} cancelada por expiração (dataDesejada: {}).",
                    r.getId(), r.getDataDesejada());
        }

        reservaRepository.saveAll(reservas);
        log.info("[ExpiracaoScheduler] {} reserva(s) expirada(s) cancelada(s).", reservas.size());
    }


    private void concluirEventos() {
        LocalDate hoje = LocalDate.now();

        List<Evento> eventos = eventoRepository.findEventosExpirados(
                List.of(EnumStatusEvento.AGENDADO),
                EnumStatus.ATIVO,
                hoje
        );

        if (eventos.isEmpty()) {
            log.info("[ExpiracaoScheduler] Nenhum evento agendado para concluir.");
            return;
        }

        for (Evento e : eventos) {
            e.setStatusEvento(EnumStatusEvento.CONCLUIDO);
            log.info("[ExpiracaoScheduler] Evento #{} marcado como CONCLUIDO (dataEvento: {}).",
                    e.getId(), e.getDataEvento());
        }

        eventoRepository.saveAll(eventos);
        log.info("[ExpiracaoScheduler] {} evento(s) agendado(s) concluído(s).", eventos.size());
    }
}
