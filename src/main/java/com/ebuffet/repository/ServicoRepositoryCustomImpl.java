package com.ebuffet.repository;

import com.ebuffet.models.Servico;
import com.ebuffet.models.enums.EnumCategoria;
import com.ebuffet.models.enums.EnumStatus;
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

import java.util.ArrayList;
import java.util.List;

public class ServicoRepositoryCustomImpl implements ServicoRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Servico> findByFilters(Long buffetId,
                                       EnumCategoria categoria,
                                       EnumStatus status,
                                       String searchQuery,
                                       Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Servico> cq = cb.createQuery(Servico.class);
        Root<Servico> root = cq.from(Servico.class);

        List<Predicate> predicates = buildPredicates(cb, root, buffetId, categoria, status, searchQuery);

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Servico> query = entityManager.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Servico> servicos = query.getResultList();

        Long total = countByFilters(cb, buffetId, categoria, status, searchQuery);

        return new PageImpl<>(servicos, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb,
                                           Root<Servico> root,
                                           Long buffetId,
                                           EnumCategoria categoria,
                                           EnumStatus status,
                                           String searchQuery) {
        List<Predicate> predicates = new ArrayList<>();

        if (buffetId != null) {
            predicates.add(cb.equal(root.get("buffet").get("id"), buffetId));
        }

        if (categoria != null) {
            predicates.add(cb.equal(root.get("categoria"), categoria));
        }

        if (status != null) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            String searchPattern = "%" + searchQuery.trim().toLowerCase() + "%";
            Predicate nomePredicate = cb.like(cb.lower(root.get("nome")), searchPattern);
            Predicate descricaoPredicate = cb.like(cb.lower(root.get("descricao")), searchPattern);
            predicates.add(cb.or(nomePredicate, descricaoPredicate));
        }

        return predicates;
    }

    private Long countByFilters(CriteriaBuilder cb,
                                Long buffetId,
                                EnumCategoria categoria,
                                EnumStatus status,
                                String searchQuery) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Servico> countRoot = countQuery.from(Servico.class);
        countQuery.select(cb.count(countRoot));

        List<Predicate> predicates = buildPredicates(cb, countRoot, buffetId, categoria, status, searchQuery);
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
