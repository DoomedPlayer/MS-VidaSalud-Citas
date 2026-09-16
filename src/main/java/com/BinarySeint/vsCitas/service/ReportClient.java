package com.BinarySeint.vsCitas.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "report-svc", url = "http://report-svc:8080/api/report")
public interface ReportClient {

    @PostMapping("/event")
    void registrarEventoReporte(@RequestBody Map<String, String> evento);
}