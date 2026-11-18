package com.coding.burguer_consumidor.model;


public record PedidoHamburguesa(
        String id,
        String cliente,
        String tipoHamburguesa,
        String ingredienteExtra,
        Double precio
) {}