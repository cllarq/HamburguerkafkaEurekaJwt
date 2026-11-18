package com.coding.burguer_consumidor.repository;

import com.coding.burguer_consumidor.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<PedidoEntity, String> {
}
