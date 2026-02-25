package com.ebuffet.repository;

import com.ebuffet.models.Evento;
import com.ebuffet.models.enums.EnumStatus;
import com.ebuffet.models.enums.EnumStatusEvento;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventoRepositoryCustomImpl implements EventoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Evento> findByFilters(Long buffetId,
                                      EnumStatusEvento statusEvento,
                                      EnumStatus status,
                                      LocalDate dataEventoFrom,
                                      LocalDate dataEventoTo,
                                      Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Evento> cq = cb.createQuery(Evento.class);
        Root<Evento> root = cq.from(Evento.class);

        List<Predicate> predicates = buildPredicates(cb, root, buffetId, statusEvento, status, dataEventoFrom, dataEventoTo);

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Evento> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Evento> eventos = query.getResultList();

        Long total = countByFilters(cb, buffetId, statusEvento, status, dataEventoFrom, dataEventoTo);

        return new PageImpl<>(eventos, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                           Root<Evento> root,
                                           Long buffetId,
                                           EnumStatusEvento statusEvento,
                                           EnumStatus status,
                                           LocalDate dataEventoFrom,
                                           LocalDate dataEventoTo) {
        List<Predicate> predicates = new ArrayList<>();

        if (buffetId != null) {
            predicates.add(cb.equal(root.get("buffet").get("id"), buffetId));
        }

        if (statusEvento != null) {
            predicates.add(cb.equal(root.get("statusEvento"), statusEvento));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        if (dataEventoFrom != null && dataEventoTo != null) {
            predicates.add(cb.between(root.get("dataEvento"), dataEventoFrom, dataEventoTo));
        } else if (dataEventoFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dataEvento"), dataEventoFrom));
        } else if (dataEventoTo != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dataEvento"), dataEventoTo));
        }

        return predicates;
    }

    private Long countByFilters(CriteriaBuilder cb,
                                Long buffetId,
                                EnumStatusEvento statusEvento,
                                EnumStatus status,
                                LocalDate dataEventoFrom,
                                LocalDate dataEventoTo) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Evento> countRoot = countQuery.from(Evento.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> predicates = buildPredicates(cb, countRoot, buffetId, statusEvento, status, dataEventoFrom, dataEventoTo);
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
