package com.coding.burguer_productor.controller;

import com.coding.burguer_productor.model.PedidoHamburguesa;
import com.coding.burguer_productor.service.PedidoProducerService;
import jakarta.ws.rs.POST;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoProducerService producerService;

    public PedidoController(PedidoProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping
    public ResponseEntity<String> crearPedido(@RequestBody PedidoHamburguesa pedido){
        producerService.enviarPedido(pedido);
        return ResponseEntity.ok("🍔 ¡Orden recibida y enviada a Kafka con éxito!");
    }

}



























