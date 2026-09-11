package com.BinarySeint.vsCitas.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "catalog-svc", url = "http://catalog-svc:8080")
public interface CatalogFeignClient {
    
    @PutMapping("/api/catalog/cupos/{cupoId}/consumir")
    void consumirCupo(@PathVariable("cupoId") Long cupoId);
}