package com.BinarySeint.vsCitas.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "notify-svc", url = "http://notify-svc:8080/api/notify")
public interface NotifyClient {

    @PostMapping("/email")
    void enviarEmail(@RequestBody Map<String, Object> payload);

    @PostMapping("/admission")
    void emitirTicket(@RequestBody Map<String, Object> payload);

    @PostMapping("/record")
    void generarPdf(@RequestBody Map<String, Object> payload);
}