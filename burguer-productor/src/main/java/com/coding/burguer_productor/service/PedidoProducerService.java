package com.coding.burguer_productor.service;

import com.coding.burguer_productor.model.PedidoHamburguesa;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PedidoProducerService {

    private final KafkaTemplate<String, PedidoHamburguesa> kafkaTemplate;
    private static final String TOPICO = "pedidos-hamburguesas";

    public PedidoProducerService(KafkaTemplate<String, PedidoHamburguesa> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarPedido(PedidoHamburguesa pedido) {
        kafkaTemplate.send(TOPICO, pedido.id(), pedido);
        System.out.println("🍔 Pedido enviado a cocina (Kafka): " + pedido);
    }
}
