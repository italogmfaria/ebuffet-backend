package com.ebuffet.controller;

import com.ebuffet.config.SingleBuffetProperties;
import com.ebuffet.controller.dto.buffet.BuffetRequest;
import com.ebuffet.controller.dto.buffet.BuffetResponse;
import com.ebuffet.models.User;
import com.ebuffet.service.BuffetService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buffets")
public class BuffetController {

    private final BuffetService service;
    private final SingleBuffetProperties singleBuffetProperties;

    public BuffetController(BuffetService service, SingleBuffetProperties singleBuffetProperties) {
        this.service = service;
        this.singleBuffetProperties = singleBuffetProperties;
    }

    @PostMapping
    public ResponseEntity<BuffetResponse> create(@Valid @RequestBody BuffetRequest req) {
        BuffetResponse created = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public BuffetResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/me")
    public Page<BuffetResponse> listMine(@AuthenticationPrincipal User owner,
                                         @PageableDefault(size = 20, sort = "dataCriacao", direction = Sort.Direction.DESC)
                                         Pageable pageable) {
        return service.listMine(owner.getId(), pageable);
    }

    @PutMapping("/{id}")
    public BuffetResponse update(@PathVariable Long id,
                                 @Valid @RequestBody BuffetRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
