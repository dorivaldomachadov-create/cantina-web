package com.cantina.dto;

public class ItemVendaRequest {
    private Integer produtoId;
    private int quantidade;

    // Getters e Setters
    public Integer getProdutoId() {
        return produtoId; }
    public void setProdutoId(Integer produtoId) {
        this.produtoId = produtoId;
    }
    public int getQuantidade() {
        return quantidade; }
    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}