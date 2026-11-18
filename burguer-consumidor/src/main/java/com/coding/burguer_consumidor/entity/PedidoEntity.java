package com.coding.burguer_consumidor.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "pedidos")
public class PedidoEntity {
    @Id
    private String id;
    private String cliente;
    private String tipoHamburguesa;
    private String ingredienteExtra;
    private Double precio;

    public PedidoEntity() {}

    public PedidoEntity(String id, String cliente, String tipoHamburguesa, String ingredienteExtra, Double precio) {
        this.id = id;
        this.cliente = cliente;
        this.tipoHamburguesa = tipoHamburguesa;
        this.ingredienteExtra = ingredienteExtra;
        this.precio = precio;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getTipoHamburguesa() { return tipoHamburguesa; }
    public void setTipoHamburguesa(String tipoHamburguesa) { this.tipoHamburguesa = tipoHamburguesa; }
    public String getIngredienteExtra() { return ingredienteExtra; }
    public void setIngredienteExtra(String ingredienteExtra) { this.ingredienteExtra = ingredienteExtra; }
    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

}


