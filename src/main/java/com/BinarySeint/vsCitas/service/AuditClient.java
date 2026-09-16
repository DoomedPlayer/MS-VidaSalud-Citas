package com.BinarySeint.vsCitas.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-svc", url = "http://audit-svc:8080/api/audit")
public interface AuditClient {

    @PostMapping("/event")
    void registrarEventoAuditoria(@RequestBody Object evento);
}