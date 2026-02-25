package com.ebuffet.repository;

import com.ebuffet.models.Notificacao;
import com.ebuffet.models.enums.EnumStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    @Query("""
        SELECT n FROM Notificacao n
        WHERE n.usuario.id = :usuarioId
          AND n.status = :status
        ORDER BY n.dataCriacao DESC
    """)
    Page<Notificacao> findByUsuarioIdAndStatus(
            @Param("usuarioId") Long usuarioId,
            @Param("status") EnumStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(n) FROM Notificacao n
        WHERE n.usuario.id = :usuarioId
          AND n.lida = false
          AND n.status = :status
    """)
    Long countUnreadByUsuarioId(
            @Param("usuarioId") Long usuarioId,
            @Param("status") EnumStatus status
    );
}
