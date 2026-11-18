package com.coding.burguer_productor.model;

public record PedidoHamburguesa(
        String id,
        String cliente,
        String tipoHamburguesa,
        String ingredienteExtra,
        Double precio
)
{}
