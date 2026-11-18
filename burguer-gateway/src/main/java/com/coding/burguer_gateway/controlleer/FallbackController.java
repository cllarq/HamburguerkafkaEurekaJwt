package com.coding.burguer_gateway.controlleer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/pedidos")
    public ResponseEntity<String> pedidosFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("🍔 La cocina está saturada o en mantenimiento. Intenta tu pedido en unos minutos.");
    }
}
