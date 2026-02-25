package com.ebuffet.controller;

import com.ebuffet.controller.dto.notificacao.NotificacaoResponse;
import com.ebuffet.service.NotificacaoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService service;

    public NotificacaoController(NotificacaoService service) {
        this.service = service;
    }

    @GetMapping
    public Page<NotificacaoResponse> list(
            @RequestParam Long usuarioId,
            @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return service.listByUsuario(usuarioId, pageable);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> countUnread(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(service.countUnread(usuarioId));
    }

    @PutMapping("/{id}/mark-as-read")
    public NotificacaoResponse markAsRead(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        return service.markAsRead(id, usuarioId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam Long usuarioId
    ) {
        service.delete(id, usuarioId);
        return ResponseEntity.noContent().build();
    }
}
