package com.cantina.dto;

import java.util.ArrayList;
import java.util.List;

public class VendaRequest {

    // 1. Adicionado o campo para realmente guardar o nome do cliente
    private String nomeCliente;

    // 2. Inicializamos a lista com 'new ArrayList<>()' para que ela NUNCA seja null
    private List<ItemVendaRequest> itens = new ArrayList<>();

    // --- Getters e Setters ---

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public List<ItemVendaRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemVendaRequest> itens) {
        this.itens = itens;
    }
}