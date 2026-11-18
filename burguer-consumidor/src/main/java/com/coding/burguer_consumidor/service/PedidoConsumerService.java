package com.coding.burguer_consumidor.service;


import com.coding.burguer_consumidor.entity.PedidoEntity;
import com.coding.burguer_consumidor.model.PedidoHamburguesa;
import com.coding.burguer_consumidor.repository.PedidoRepository;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class PedidoConsumerService {

    private final PedidoRepository pedidoRepository;

    public PedidoConsumerService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 2000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "pedidos-hamburguesas", groupId = "grupo-cocina")
    public void procesarPedido(PedidoHamburguesa pedido){
        System.out.println("👨‍🍳 Intentando preparar pedido: " + pedido.id() + " (" + pedido.tipoHamburguesa() + ")");

        //Regla de negocio, se valida que el pedido sea valido
        if (pedido.precio() == null || pedido.precio() <= 0) {
            throw new IllegalArgumentException("❌ Precio inválido en el pedido: " + pedido.precio());
        }
        if ("piña".equalsIgnoreCase(pedido.ingredienteExtra())){
            throw new IllegalArgumentException("❌ ¡Sacrilegio! No le ponemos piña a las hamburguesas.");
        }
        // sitodo esta bien, guardamos en PostgreSQL
        PedidoEntity entidad = new PedidoEntity(
          pedido.id(),
          pedido.cliente(),
          pedido.tipoHamburguesa(),
          pedido.ingredienteExtra(),
          pedido.precio()
        );
        pedidoRepository.save(entidad);
        System.out.println("✅ ¡Hamburguesa preparada y guardada en PostgreSQL! ID:" + pedido.id());
    }

    @DltHandler
    public void manejarPedidoFallido(
       PedidoHamburguesa pedidoFallido,
       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
       @Header(KafkaHeaders.EXCEPTION_MESSAGE) String mensajeError){

        System.err.println("🚨 ==================================================");
        System.err.println("🚨 PEDIDO ENVIADO AL DEAD LETTER TOPIC (DLT)");
        System.err.println("🚨 Tópico origen: " + topic);
        System.err.println("🚨 Pedido rechazado: " + pedidoFallido);
        System.err.println("🚨 Motivo del fallo: " + mensajeError);
        System.err.println("🚨 ==================================================");
    }
}














