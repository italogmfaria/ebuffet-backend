package com.ebuffet.repository;

import com.ebuffet.models.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long>, ServicoRepositoryCustom {

    List<Servico> findByIdIn(Collection<Long> ids);

}
